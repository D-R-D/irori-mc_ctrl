package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public class ConfigFile {
    private static final Logger logger = LoggerFactory.getLogger("irori-mc_pxctrl.ConfigFile");

    public static Map<String,Object> ReadAsMap(Path dataDirectory, String configFile) throws IOException{
        Path jsonpath = Path.of(dataDirectory + "/" + configFile);
        ObjectMapper mapper = new ObjectMapper();

        Map<String,Object> result = mapper.readValue(new FileReader(jsonpath.toString()), new TypeReference<>() {});

        return result;
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
        br.close();

        return result;
    }

    public static void WriteString(Path dataDirectory, String configFile, String content){
        File file = new File(dataDirectory + "/" + configFile);
        try {
            writeStringToFile(content, file);
        }catch (Exception ex) { logger.error("writeStringToFile failed." , ex); }
    }

    public static void WriteObject(Path dataDirectory, String configFile, Object object){
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(dataDirectory + "/" + configFile);

        try {
            writeStringToFile(mapper.writeValueAsString(object), file);
        }catch (Exception ex){ logger.error("writeStringToFile failed." , ex); }
    }

    public static void writeStringToFile(String str, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(str);
        writer.close();
    }
}
