package org.sync;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class SyncthingStatsGUI {
    public static ConfigFileReader configFileReader = new ConfigFileReader();
    static String applicationUrl = configFileReader.getApplicationUrl();
    static String apiKey = configFileReader.getApiKey();

    public static void main(String[] args) {
        // Apply FlatLaf Look-and-Feel
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            Logger.getLogger(SyncthingStatsGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }

        SwingUtilities.invokeLater(SyncthingStatsGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        // Create the main frame
        JFrame frame = new JFrame("Syncthing Stats");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Header Panel with Gradient
        JPanel headerPanel = getHeaderPanel();
        frame.add(headerPanel, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(0, 1, 10, 10));
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(new Color(30, 30, 30));

        JLabel deviceIDLabel = createModernLabel("Device ID: Loading...");
        JLabel foldersLabel = createModernLabel("Folders: Loading...");
        JLabel connectionsLabel = createModernLabel("Connections: Loading...");
        JLabel configLabel = createModernLabel("Additional Info: Loading...");


        statsPanel.add(deviceIDLabel);
        statsPanel.add(foldersLabel);
        statsPanel.add(connectionsLabel);
        //statsPanel.add(configLabel);

        // Wrap statsPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(statsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(scrollPane, BorderLayout.CENTER);

        // Footer Panel
        JLabel footerLabel = new JLabel("Powered by Syncthing API", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        footerLabel.setForeground(Color.LIGHT_GRAY);
        footerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        frame.add(footerLabel, BorderLayout.SOUTH);

        // Add periodic update timer
        Timer timer = getTimer(deviceIDLabel, foldersLabel, connectionsLabel, configLabel);
        timer.start();

        // Show the frame
        frame.setVisible(true);
    }

    private static JPanel getHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(45, 45, 45),
                        0, getHeight(), new Color(60, 63, 65));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel headerLabel = new JLabel("Syncthing Statistics", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Roboto", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        return headerPanel;
    }

    private static JLabel createModernLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Roboto", Font.PLAIN, 14)); // Smaller font
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(new Color(45, 45, 45));
        label.setBorder(new EmptyBorder(5, 5, 5, 5)); // Reduced padding
        return label;
    }


    private static Timer getTimer(JLabel deviceIDLabel, JLabel foldersLabel, JLabel connectionsLabel, JLabel configLabel) {
        Timer timer = new Timer(5000, e -> {
            try {
                System.out.println("Fetching stats...");
                JSONObject stats = fetchStatus();
                JSONArray folders = ConfigController.fetchFolders();
                JSONArray devices = ConfigController.fetchDevices();
                JSONObject config = ConfigController.fetchConfig();

                // Update the labels with the latest data
                deviceIDLabel.setText("<html><b style='color: #00ff00;'>Device ID:</b> " + stats.optString("myID", "Unknown") + "</html>");
                foldersLabel.setText("<html><b style='color: #00ff00;'>Folders:</b> " + Arrays.toString(ConfigController.extractFolders(folders)) + "</html>");
                connectionsLabel.setText("<html><b style='color: #00ff00;'>Devices:</b> " + Arrays.toString(ConfigController.extractDeviceNames(devices)) + "</html>");


            } catch (Exception ex) {
                Logger.getLogger(SyncthingStatsGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                deviceIDLabel.setText("Device ID: Error loading stats");
                foldersLabel.setText("Folders: Error loading stats");
                connectionsLabel.setText("Connections: Error loading stats");
            }
        });
        timer.setInitialDelay(0);
        return timer;
    }

    private static JSONObject fetchStatus() throws IOException {
        String apiURL = applicationUrl + "/rest/system/status";
        HttpURLConnection connection = createConnection(apiURL, apiKey);
        connection.connect();
        String response = readResponse(connection);
        return new JSONObject(response);
    }



    static String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        connection.disconnect();
        return response.toString();
    }

    static HttpURLConnection createConnection(String apiURL, String apiKey) throws IOException {
        try {
            URI uri = URI.create(apiURL);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-API-Key", apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000); // 5-second timeout
            connection.setReadTimeout(5000);
            return connection;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String displayKeyMetrics(JSONObject config) {
        StringBuilder html = new StringBuilder("<html><b style='color: #00ff00;'>Config:</b><br>");

        // Extract and display folder paths
        JSONArray folders = config.getJSONArray("folders");
        html.append("<b>Folders:</b><br>");
        for (int i = 0; i < folders.length(); i++) {
            JSONObject folder = folders.getJSONObject(i);
            html.append("- ").append(folder.getString("label"))
                    .append(" (Path: ").append(folder.getString("path")).append(")<br>");
        }

        // Extract and display device info
        JSONArray devices = config.getJSONArray("devices");
        html.append("<b>Devices:</b><br>");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject device = devices.getJSONObject(i);
            html.append("- ").append(device.getString("name"))
                    .append(" (ID: ").append(device.getString("deviceID")).append(")<br>");
        }

        // Extract and display GUI settings
        JSONObject gui = config.getJSONObject("gui");
        html.append("<b>GUI Settings:</b><br>")
                .append("Enabled: ").append(gui.getBoolean("enabled")).append("<br>")
                .append("Address: ").append(gui.getString("address")).append("<br>");

        // Extract and display options
        JSONObject options = config.getJSONObject("options");
        html.append("<b>Options:</b><br>")
                .append("Global Announce: ").append(options.getBoolean("globalAnnounceEnabled")).append("<br>")
                .append("Relays Enabled: ").append(options.getBoolean("relaysEnabled")).append("<br>")
                .append("Start Browser: ").append(options.getBoolean("startBrowser")).append("<br>");

        // Close the HTML tag
        html.append("</html>");

        // Return the formatted text
        return html.toString();
    }
}
