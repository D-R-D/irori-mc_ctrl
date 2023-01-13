package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public class ConfigFile {
    public static Map<String,Object> ReadAsMap(Path dataDirectory, String configFile) throws IOException{
        Path jsonpath = Path.of(dataDirectory + "/" + configFile);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue((new FileReader(jsonpath.toString())), new TypeReference<>() {});
    }

    public static String ReadAsString(Path dataDirectory, String configFile) throws IOException {
        Path jsonpath = Path.of(dataDirectory + "/" + configFile);
        File file = new File(jsonpath.toString());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = "";
        String result = "";
        while((str = br.readLine()) != null){
            result += str;
        }
        return result;
    }
}
