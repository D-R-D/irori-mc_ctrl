package irorimc_pxctrl.irorimc_pxctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;

public class Tcp_Console {
    private static final Logger logger = LoggerFactory.getLogger("irori-mc_pxctrl.Tcp_Console");
    private static Path dataDirectory;

    //クライアントとの接続情報を持つクラス
    private static class SocketItem {
        String ClientName;
        Socket socket;
        InputStream inputStream;
        OutputStream outputStream;
        byte[] buffer = new byte[1024];
    }

    private final ArrayList<SocketItem> Connections = new ArrayList<>();
    private final Deque<String> Orders = new ArrayDeque<>();
    private ServerSocket svsock;

    //サーバーソケットを作成する
    public void SetSocketAndInit(int port, Path dataDir) throws IOException {
        svsock = new ServerSocket(port);
        dataDirectory = dataDir;

        logger.info("Starting Server at Port[" + port + "]");
    }

    //クライアントに送信するデータをキューに入れる
    public void SetOrder(String order){
        Orders.add(order);
    }

    //キューが持っている内容をtcpで各クライアントに送信
    @SuppressWarnings("InfiniteRecursion")
    public void RunOrder() throws InterruptedException {
        String content = Orders.pop();
        for (SocketItem ConItem : Connections) {
            new Thread(() -> {
                try {
                    Send(ConItem, content);
                } catch (Exception ex) {
                    logger.error("Send ERROR ... ", ex);
                    EndConnect(ConItem);
                }
            }).start();
        }
        Thread.sleep(500);
        RunOrder();
    }

    //作成したサーバーソケットでクライアントの接続を待つ
    @SuppressWarnings("InfiniteRecursion")
    public void StartAccept() throws IOException {
        logger.info("Waiting Connect.");
        Socket sock = svsock.accept();

        //接続要請があった時はソケットの情報を別スレッドに飛ばしてクライアントからの最初のデータをクライアント名として登録する
        new Thread(() -> {
            try {
                SocketItem ConItem = new SocketItem();
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                byte[] data = ConItem.buffer;

                int readSize = in.read(data);
                data = Arrays.copyOf(data, readSize);

                ConItem.ClientName = new String(data, StandardCharsets.UTF_8);
                ConItem.socket = sock;
                ConItem.inputStream = in;
                ConItem.outputStream = out;

                new Thread(() -> Receive(ConItem)).start();

                Connections.add(ConItem);

                logger.info("Connection Accepted [client name : " + ConItem.ClientName + "]");
            }catch (Exception ex) {
                logger.error("Accept ERROR ... " , ex);
            }
        }).start();

        StartAccept();
    }

    public void Send(SocketItem ConItem, String content) throws Exception {
        ConItem.outputStream.write(content.getBytes(StandardCharsets.UTF_8));
    }

    public void Receive(SocketItem ConItem){
        try {
            byte[] data = ConItem.buffer;
            int readSize = ConItem.inputStream.read(data); //受信
            if(readSize == 0) //受信データが0の時はソケットを閉じる
            {
                logger.info("Close packet Received ... ");
                EndConnect(ConItem);
                return;
            }
            data = Arrays.copyOf(data, readSize);
            String content = new String(data, StandardCharsets.UTF_8);

            new Thread(() -> {
                try {
                    CommandRunner.Command(content, dataDirectory);
                } catch (IOException e) {
                    logger.error("CommandRunner Error ... ",e);
                }
            }).start();

        }catch (Exception ex){
            logger.error("Receive ERROR ... ", ex);
            EndConnect(ConItem);
            return;
        }
        Receive(ConItem);
    }

    //リストから該当要素を削除し、受信・送信をシャットダウンしてから、ソケットを閉じる
    public void EndConnect(SocketItem ConItem) {
        Connections.remove(ConItem);
        logger.info("Closing connection ["+ ConItem.ClientName + "]");

        try {
            ConItem.socket.shutdownInput();
        }catch (Exception ex) { logger.error("EndConnect ERROR ... ",ex); }

        try {
            ConItem.socket.shutdownOutput();
        }catch (Exception ex) { logger.error("EndConnect ERROR ... ",ex); }

        try {
            ConItem.socket.close();
        }catch (Exception ex){ logger.error("EndConnect ERROR ... ",ex); }
    }
}
