package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ConfigFile {
    public static Map<String,Object> Read(Path dataDirectory, String configFile) throws IOException{
        Path jsonpath = Path.of(dataDirectory + "/" + configFile);

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue((new FileReader(jsonpath.toString())), new TypeReference<>() {
        });
    }
}
