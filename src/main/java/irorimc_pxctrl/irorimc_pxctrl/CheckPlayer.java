package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CheckPlayer {
    private static final Logger logger = LoggerFactory.getLogger("irori-mc_pxctrl.Tcp_Console");
    public static boolean Check(String PlayerName, String uuid, String WhiteList){
        Map<String,Object> map = new HashMap<>();
        map.put("name", PlayerName); //set name
        map.put("id", uuid); //set uuid

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> playerList = mapper.readValue(WhiteList, new TypeReference<List<Map<String, Object>>>() {});

            logger.info(String.valueOf(map));
            logger.info("");

            for (Map<String, Object> player : playerList) {
                if(map.get("id").equals(player.get("id")))
                {
                    return true;
                }

                logger.info(String.valueOf(player));
            }
        }catch (Exception ex) { logger.error("Check error ... ",ex);}

        return false;
    }
}
