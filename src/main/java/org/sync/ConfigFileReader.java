package org.sync;


import java.io.File;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.util.Properties;

public class ConfigFileReader {

    private Properties properties;

    private final String configFilePath= "src/main/resources/application.properties";

    public ConfigFileReader() {
        File  ConfigFile=new File(configFilePath);

        try {
            FileInputStream configFileReader=new FileInputStream(ConfigFile);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }  catch (FileNotFoundException e)

        {
            System.out.println(e.getMessage());
            throw new RuntimeException("config.properties not found at config file path " + configFilePath);

        }

    }

    public String getApplicationUrl() {
        String applicationurl= properties.getProperty("api.url");
        if(applicationurl != null)
            return applicationurl;
        else
            throw new RuntimeException("Application url not specified in the config.properties file.");
    }
    public String getApiKey() {
        String apiKey= properties.getProperty("api.key");
        if(apiKey != null)
            return apiKey;
        else
            throw new RuntimeException("API Key not specified in the config.properties file.");
    }

}
