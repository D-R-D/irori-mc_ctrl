package irorimc_pxctrl.irorimc_pxctrl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class ShellRunner {
    public static void Docker(String containername , int state , Path dataDirectory) throws IOException {
        Path shell = Path.of(dataDirectory + (state == 0 ? "start.sh" : "stop.sh"));
        Process process = Runtime.getRuntime().exec("/bin/bash " + shell + " " + containername);

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
    }
}
