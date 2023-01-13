package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class GetObjectFromJson {
    public static Map<String,Object> GetObjectFromJson (String json){
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> result = null;

        try {
            result = mapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
