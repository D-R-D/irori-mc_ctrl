package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WhitelistSerializer {
    private static final Logger logger = LoggerFactory.getLogger("irori-mc_pxctrl.WhitelistSerializer");
    private static ObjectMapper mapper = new ObjectMapper();
    private static String json;

    public static void LoadWhitelist(Path dataDirectory) {
        try {
            json = ConfigFile.ReadAsString(dataDirectory, "whitelist.json");
        }catch (Exception ex) { logger.error("LoadWhitelist failed." , ex); }
    }

    public static void ApplyLatestWhitelist() {
        IroriMc_pxctrl.WhiteList = json;
    }

    public static void WhiteListAdd(Path dataDirectory, String playerName){
        logger.info("Checking Whitelist ... ");
        String player = MinecraftApi.getPlayerJsonFromName(playerName);
        if(player == null){
            logger.info("指定されたプレイヤーが存在しないためホワイトリスト登録はスキップされます。");
            return;
        }

        try {
            Map<String , Object> map = mapper.readValue(player, new TypeReference<>() {});
            map.replace("id",UUID.fromString(insertDashUUID((String) map.get("id"))));

            LoadWhitelist(dataDirectory);
            List<Map<String,Object>> whitelist = mapper.readValue(json, new TypeReference<>() {});
            for (Map<String,Object> item:whitelist) {
                if(item.get("name").equals(map.get("name")) || item.get("id").toString().equals(map.get("id").toString())){
                    logger.info("指定されたプレイヤーは既に追加済みです。");
                    return;
                }
            }
            whitelist.add(map);
            ConfigFile.WriteObject(dataDirectory, "whitelist.json", whitelist);
            LoadWhitelist(dataDirectory);
            ApplyLatestWhitelist();
            logger.info("指定されたプレイヤーをホワイトリストに追加しました。");
        }catch (Exception ex) { logger.error("WhiteListAdd failed.",ex); }
    }

    public static void WhiteListRemove(Path dataDirectory, String playerName){
        logger.info("Checking Whitelist ... ");
        try {
            LoadWhitelist(dataDirectory);
            List<Map<String, Object>> whitelist = mapper.readValue(json, new TypeReference<>() {});
            Map<String,Object> targetMap = null;
            for (Map<String, Object> item : whitelist) {
                if (item.get("name").equals(playerName)) {
                    targetMap = item;
                }
            }

            if(targetMap == null){
                logger.info("指定されたプレイヤー["+ playerName +"]はホワイトリストに登録されていません。");
                return;
            }

            whitelist.remove(targetMap);
            ConfigFile.WriteObject(dataDirectory, "whitelist.json", whitelist);
            LoadWhitelist(dataDirectory);
            ApplyLatestWhitelist();
            logger.info("指定されたプレイヤーはホワイトリストから削除されました。");
        } catch (Exception ex){ logger.error("WhiteListRemove failed.",ex); }
    }

    public static String insertDashUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");

        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");

        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");

        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }
}
