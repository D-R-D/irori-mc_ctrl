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
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;


@Plugin(
        id = "irori-mc_pxctrl",
        name = "Irori Mc_pxctrl",
        version = "1.0-SNAPSHOT"
)
public class IroriMc_pxctrl {
    private final ProxyServer server;
    private final Logger logger;
    private final  Path dataDirectory;

    @Inject
    public IroriMc_pxctrl(ProxyServer server, Logger logger,@DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        logger.info("WorkingDir : " + dataDirectory.toString());
        logger.info("Hello there! I made my first plugin with Velocity.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Init_Plugin.init(dataDirectory,logger);
        ConStart();
    }

    public void ConStart() {
        logger.info("ConStart");
        new Thread(()-> {
            try {
                Map<String,Object> jsonObject = ConfigFile.Read(dataDirectory, "tcp.json");
                Tcp_Console con = new Tcp_Console();

                con.SetSocketAndInit(Integer.parseInt((String) jsonObject.get("Port")), dataDirectory);
                con.StartAccept();
            } catch (Exception ex) {
                logger.error("ConStart ERROR ... ",ex);
            }
        }).start();
    }

    @Subscribe
    public void onLoginEvent(LoginEvent login){
        logger.info("PlayerName:[" + login.getPlayer().getUsername() + "] UUID:[" + login.getPlayer().getUniqueId() + "]");
        //login.getPlayer().disconnect(Component.text("test"));
    }

    @Subscribe
    public void onServerPostConnectEvent(ServerPostConnectEvent serverpostconnect){
        logger.info("player connect event");
        server.getConsoleCommandSource().sendMessage(Component.text(serverpostconnect.getPlayer().getUsername()));
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent playerchat){
        logger.info("player chat event");
        server.getAllServers().forEach(sv -> sv.sendMessage(Component.text("")));
    }
}
