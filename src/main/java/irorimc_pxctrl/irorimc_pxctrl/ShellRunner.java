package irorimc_pxctrl.irorimc_pxctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class ShellRunner {
    private static final Logger logger = LoggerFactory.getLogger("irori-mc_pxctrl.ShellRunner");

    public static void Docker(String containername , int state , Path dataDirectory) {
        Path shell = Path.of(dataDirectory + (state == 0 ? "start.sh" : "stop.sh"));

        try {
            Process process = Runtime.getRuntime().exec("/bin/bash " + shell + " " + containername);

            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            logger.info(output.toString());
        }catch (Exception ex) { logger.error("ShellRunner [Docker Start] failed ... " ,ex); }
    }
}
