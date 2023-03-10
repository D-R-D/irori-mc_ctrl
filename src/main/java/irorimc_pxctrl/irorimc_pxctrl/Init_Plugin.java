package irorimc_pxctrl.irorimc_pxctrl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Init_Plugin {
    public static Path dataDirectory;

    public static void init(Path dataDir ,Logger logger) {
        dataDirectory = dataDir;

        try{
            InitFiles();
        }catch (Exception ex) { logger.error("InitFiles ERROR ... ", ex); }

        try{
            InitJson("tcp.json");
        }catch (Exception ex) { logger.error("InitJson　ERROR　... ", ex); }

        try{
            InitJson("config.json");
        }catch (Exception ex) { logger.error("InitJson ERROR ... ", ex); }

        try {
            InitJson("whitelist.json");
        }catch (Exception ex) { logger.error("InitJson ERROR ... ", ex); }
    }

    private static void InitFiles() throws IOException {
        if(!Files.exists(dataDirectory)){
            Files.createDirectories(dataDirectory);
        }

        if(!Files.exists(Path.of(dataDirectory + "/tcp.json"))) {
            Files.createFile(Path.of(dataDirectory + "/tcp.json"));
        }

        if(!Files.exists(Path.of(dataDirectory + "/whitelist.json"))){
            Files.createFile(Path.of(dataDirectory + "/whitelist.json"));
        }

        if(!Files.exists(Path.of(dataDirectory + "/config.json"))){
            Files.createFile(Path.of(dataDirectory + "/config.json"));
        }
    }

    private static void InitJson(String jsonName) throws IOException {
        Path Jsonpath = Path.of(dataDirectory + "/" + jsonName);
        String SJson = Files.readString(Jsonpath);

        ObjectMapper mapper = new ObjectMapper();

        if(jsonName.equals("tcp.json") && SJson.equals("")){
            Map<String,String> map = new HashMap<>();
            map.put("Port","6001");

            writeStringToFile(mapper.writeValueAsString(map),new File(Jsonpath.toString()));
            return;
        }

        if(jsonName.equals("config.json") && SJson.equals("")){
            Map<String, Object> map = new HashMap<>();
            map.put("DisconnectMessage", "Sorry you don't have access permission.");

            writeStringToFile(mapper.writeValueAsString(map),new File(Jsonpath.toString()));
            return;
        }

        if(jsonName.equals("whitelist.json") && SJson.equals("")) {
            List<Map<String, Object>> mapList = new ArrayList<Map<String , Object>>() {};

            writeStringToFile(mapper.writeValueAsString(mapList), new File(Jsonpath.toString()));
            return;
        }
    }

    public static void writeStringToFile(String str, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(str);
        writer.close();
    }
}
