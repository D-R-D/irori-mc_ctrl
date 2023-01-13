package irorimc_pxctrl.irorimc_pxctrl;

import java.io.IOException;
import java.nio.file.Path;

public class CommandRunner {
    public static void Command(String command, Path dataDirectory) throws IOException {
        String firstsplit = command.split(":")[0];

        if(firstsplit.equals("container")){
            Container(command, dataDirectory);
        }
    }

    private static void Container(String command, Path dataDirectory) throws IOException {
        String secondsplit = command.split(":")[1];

        if(secondsplit.equals("start")){
            ShellRunner.Docker(command.split(":")[2], 0, dataDirectory);
            return;
        }
        if (secondsplit.equals("stop")) {
            ShellRunner.Docker(command.split(":")[2], 1, dataDirectory);
            return;
        }
    }
}
