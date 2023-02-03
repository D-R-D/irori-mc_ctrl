package irorimc_pxctrl.irorimc_pxctrl;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.geysermc.floodgate.api.FloodgateApi;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;


@Plugin(
        id = "irori-mc_pxctrl",
        name = "Irori Mc_pxctrl",
        version = "1.0-SNAPSHOT"
)
public class IroriMc_pxctrl {
    private static ProxyServer server;
    private static Logger logger;
    private final Path dataDirectory;
    private final FloodgateApi floodgateApi;

    public static String WhiteList;
    private Map<String, Object> configMap;
    private Tcp_Console tcp_console;

    @Inject
    public IroriMc_pxctrl(ProxyServer server, Logger logger,@DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.floodgateApi = FloodgateApi.getInstance();
        logger.info("WorkingDir : " + dataDirectory.toString());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Init_Plugin.init(dataDirectory,logger);
        WhitelistLoad();
        ConfigLoad();
        ConStart();

        logger.info(WhiteList);
    }

    //tcp接続を開始する
    public void ConStart() {
        logger.info("ConStart");
        new Thread(()-> {
            try {
                Map<String,Object> jsonObject = ConfigFile.ReadAsMap(dataDirectory, "tcp.json");
                tcp_console = new Tcp_Console();

                tcp_console.Init(dataDirectory, server);
                tcp_console.SetSocket(Integer.parseInt((String) jsonObject.get("Port")));
                tcp_console.StartAccept();
            } catch (Exception ex) {
                logger.error("ConStart ERROR ... ",ex);
            }
        }).start();
    }

    //ホワイトリストをロードする
    public void WhitelistLoad(){
        logger.info("whitelist loading...");
        try {
            WhitelistSerializer whitelistSerializer = new WhitelistSerializer();
            whitelistSerializer.LoadWhitelist(dataDirectory);
            whitelistSerializer.ApplyLatestWhitelist();
            logger.info("whitelist loaded!!");
        }catch (Exception ex){ logger.error("Whitelist Load Failed ... " , ex); }
    }

    //コンフィグファイルをロードする
    public void ConfigLoad() {
        logger.info("ConfigLoad start ... ");
        try {
            configMap = ConfigFile.ReadAsMap(dataDirectory, "config.json");
            logger.info("Config loaded!!");
        }catch (Exception ex) { logger.error("Config load Failed ... " , ex); }
    }

    public static void Alert(String msg){
        logger.info("[Alert]: " + msg);
        server.getAllServers().forEach(sv -> sv.sendMessage(Component.text(msg)));
    }

    @Subscribe
    public void onLoginEvent(LoginEvent login) {
        if (!CheckPlayer.Check(login.getPlayer().getUsername(), String.valueOf(login.getPlayer().getUniqueId()), WhiteList)) {
            if(floodgateApi.isFloodgatePlayer(login.getPlayer().getUniqueId())){
                logger.info("be player login.");
                tcp_console.SetOrder("");
                return;
            }
            logger.info("不届き者のプレイヤー["+ login.getPlayer().getUsername() + "]は我らがフィルタープラグインにブロックされました。ざまあみろ！！ﾍﾟｯ!!");
            login.getPlayer().disconnect(Component.text(configMap.get("DisconnectMessage").toString()));
            server.getAllServers().forEach(sv -> sv.sendMessage(Component.text("不届き者のプレイヤー["+ login.getPlayer().getUsername() + "]はホワイトリストの前に散っていきました。")));
        }
    }

    @Subscribe
    public void onServerPostConnectEvent(ServerPostConnectEvent serverpostconnect){
        logger.info("player connect event");
        server.getConsoleCommandSource().sendMessage(Component.text(serverpostconnect.getPlayer().getUsername()));
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent playerchat){
        server.getConsoleCommandSource().sendMessage(Component.text("これはテスト送信です。"));
    }
}
