package irorimc_pxctrl.irorimc_pxctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class CommandRunner {
    private static final Logger logger = LoggerFactory.getLogger("irori-mc_pxctrl.CommandRunner");
    public static void Command(String command, Path dataDirectory, String clientName){
        String firstsplit = command.split(":")[0];

        if(firstsplit.equals("alert")){ //alert:clientname:content
            Alert(command, clientName);
            return;
        }

        if(firstsplit.equals("container")){
            Container(command, dataDirectory);
            return;
        }

        if (firstsplit.equals("whitelist")){
            Whitelist(command, dataDirectory);
        }
    }

    private static void Alert(String command, String clientName){
        String[] split = command.split(":");

        IroriMc_pxctrl.Alert("[" + clientName + "]: " + split[1]);
    }

    private static void Container(String command, Path dataDirectory) {
        String secondsplit = command.split(":")[1];

        if(secondsplit.equals("start")){
            ShellRunner.Docker(command.split(":")[2], 0, dataDirectory);
            return;
        }
        if (secondsplit.equals("stop")) {
            ShellRunner.Docker(command.split(":")[2], 1, dataDirectory);
        }
    }

    private static void Whitelist(String command, Path dataDirectory){
        String secondsplit = command.split(":")[1];
        String[] split = command.split(":");

        if(secondsplit.equals("add") && split.length == 3){
            logger.info("Whitelist add.");
            WhitelistSerializer.WhiteListAdd(dataDirectory, split[2]);
            return;
        }
        if(secondsplit.equals("remove") && split.length == 3){
            logger.info("Whitelist remove.");
            WhitelistSerializer.WhiteListRemove(dataDirectory, split[2]);
            return;
        }
        if(secondsplit.equals("reload")){
            logger.info("Whitelist reload.");
            WhitelistSerializer whitelistSerializer = new WhitelistSerializer();
            whitelistSerializer.LoadWhitelist(dataDirectory);
            whitelistSerializer.ApplyLatestWhitelist();
            return;
        }
        if(secondsplit.equals("addmanual")){
            logger.info("Whitelist add Manual Mode.");
        }

        logger.error("コマンド[" + command + "]は不明なサブコマンドがあるか、コマンド形状が不正です。");
    }
}
