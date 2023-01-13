package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.List;
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
    private String WhiteList;
    private Map<String, Object> configMap;

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
        WhitelistLoad();
        ConfigLoad();
        ConStart();
    }

    public void ConStart() {
        logger.info("ConStart");
        new Thread(()-> {
            try {
                Map<String,Object> jsonObject = ConfigFile.ReadAsMap(dataDirectory, "tcp.json");
                Tcp_Console con = new Tcp_Console();

                con.SetSocketAndInit(Integer.parseInt((String) jsonObject.get("Port")), dataDirectory);
                con.StartAccept();
            } catch (Exception ex) {
                logger.error("ConStart ERROR ... ",ex);
            }
        }).start();
    }

    public void WhitelistLoad(){
        logger.info("whitelist loading...");
        try {
            WhiteList = ConfigFile.ReadAsString(dataDirectory, "whitelist.json");
            logger.info("whitelist loaded!!");
        }catch (Exception ex){ logger.error("Whitelist Load Failed ... " , ex); }
    }

    public void ConfigLoad() {
        logger.info("ConfigLoad start ... ");
        try {
            configMap = ConfigFile.ReadAsMap(dataDirectory, "config.json");
            logger.info("Config loaded!!");
        }catch (Exception ex) { logger.error("Config load Failed ... " , ex); }
    }

    @Subscribe
    public void onLoginEvent(LoginEvent login) {
        logger.info("PlayerName:[" + login.getPlayer().getUsername() + "] UUID:[" + String.valueOf(login.getPlayer().getUniqueId()) + "]");
        if (!CheckPlayer.Check(login.getPlayer().getUsername(), String.valueOf(login.getPlayer().getUniqueId()), WhiteList)) {
            login.getPlayer().disconnect(Component.text(configMap.get("DisconnectMessage").toString()));
        }
    }

    @Subscribe
    public void onServerPostConnectEvent(ServerPostConnectEvent serverpostconnect){
        logger.info("player connect event");
        server.getConsoleCommandSource().sendMessage(Component.text(serverpostconnect.getPlayer().getUsername()));
    }

    @Subscribe
    public void onPlayerChatEvent(PlayerChatEvent playerchat){

        Map<String,Object> map = GetObjectFromJson.GetObjectFromJson(MinecraftApi.getPlayerJsonFromName(playerchat.getPlayer().getUsername()));

        if(map != null) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> mapList = new ArrayList<>() {};

            mapList.add(map);
            mapList.add(map);

            try {
                logger.info("\n"+ mapper.writeValueAsString(mapList));
            }catch (Exception ex) { logger.error("onPlayerChatEvent mapper.writeValueAsString ...", ex); }
        } else { logger.info("map is null."); }
        logger.info("player chat event");
        server.getAllServers().forEach(sv -> sv.sendMessage(Component.text("")));
    }
}
