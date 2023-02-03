package irorimc_pxctrl.irorimc_pxctrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MinecraftApi {
    private static final String APIUrl = "https://api.mojang.com/users/profiles/minecraft/";
    private static final Logger logger = LoggerFactory.getLogger("irori-mc_pxctrl.MinecraftApi");

    public static String getPlayerJsonFromName(String playerName) {
        String surl = APIUrl + playerName;

        try{
            String json = getOnlineUUID(surl);
            if(!json.equals("")) {
                return json;
            }
        }catch (Exception ex) { logger.error("getOnlineUUID failed ... ",ex); }

        logger.info("player[" + playerName + "] is not found.");
        return null;
    }

    public static String getOnlineUUID(String surl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(surl)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
