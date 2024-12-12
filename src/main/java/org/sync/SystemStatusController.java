package org.sync;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static org.sync.SyncthingStatsGUI.*;

public class SystemStatusController {
    public static JSONObject fetchStatus() throws Exception {
        // Syncthing API endpoint
        String apiURL = applicationUrl + "/rest/system/status";

        // Open a connection
        URI uri = URI.create(apiURL);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-API-Key", apiKey);
        conn.setConnectTimeout(5000); // 5-second timeout
        conn.setReadTimeout(5000);

        // Check response code
        int responseCode = conn.getResponseCode();
        System.out.println(responseCode);
        if (responseCode != 200) {
            System.out.println("Response Code: " + responseCode);
            throw new RuntimeException("HTTP GET Request Failed with Error Code : " + responseCode);
        }

        // Read the response
        String response = readResponse(conn);
        return new JSONObject(response);
    }
}
