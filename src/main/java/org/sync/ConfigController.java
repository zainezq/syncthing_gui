package org.sync;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.HttpURLConnection;

import static org.sync.SyncthingStatsGUI.*;

public class ConfigController {

    static JSONArray fetchFolders() throws Exception {
        // Syncthing API endpoint
        String apiURL = applicationUrl + "/rest/config/folders";

        // Open a connection
        HttpURLConnection connection = createConnection(apiURL, apiKey);
        // Check response code

        if (connection.getResponseCode() != 200) {
            System.out.println("Response Code: " + connection.getResponseCode());
            throw new RuntimeException("HTTP GET Request Failed with Error Code : " + connection.getResponseCode());
        }

        // Read the response
        String response = readResponse(connection);
        return new JSONArray(response);

    }
    static String[] extractFolders(JSONArray folders) {
        String [] folderTotal = new String[folders.length()];

        for (int i = 0; i < folders.length(); i++) {
            folderTotal[i] = folders.optJSONObject(i).optString("label");
        }
        return folderTotal;
    }

    static JSONArray fetchDevices() throws Exception {
        // Syncthing API endpoint
        String apiURL = applicationUrl + "/rest/config/devices";

        // Open a connection
        HttpURLConnection connection = createConnection(apiURL, apiKey);
        // Check response code
        if (connection.getResponseCode() != 200) {
            System.out.println("Response Code: " + connection.getResponseCode());
            throw new RuntimeException("HTTP GET Request Failed with Error Code : " + connection.getResponseCode());
        }
        // Read the response
        String response = readResponse(connection);
        return new JSONArray(response);
    }

    static String[] extractDeviceNames(JSONArray devices) {
        String [] deviceNames = new String[devices.length()];
        for (int i = 0; i < devices.length(); i++) {
            deviceNames[i] = devices.optJSONObject(i).optString("name");
        }
        return deviceNames;
    }

    static JSONObject fetchConfig() throws IOException {
        String apiURL = applicationUrl + "/rest/system/config";
        HttpURLConnection connection = createConnection(apiURL, apiKey);
        connection.connect();
        String response = readResponse(connection);
        return new JSONObject(response);
    }



}
