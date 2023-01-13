package irorimc_pxctrl.irorimc_pxctrl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MinecraftApi {
    private static final String APIUrl = "https://api.mojang.com/users/profiles/minecraft/";

    public static String getPlayerJsonFromName(String playerName) {
        String surl = APIUrl + playerName;

        try{
            return getOnlineUUID(surl);
        }catch (Exception ex) { System.out.println(ex); }

        return null;
    }

    public static String getOnlineUUID(String surl) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(surl)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

}
