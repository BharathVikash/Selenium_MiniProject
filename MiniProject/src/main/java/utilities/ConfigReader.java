package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    private static void loadProperties() throws IOException {
        if (properties == null) {
            properties = new Properties();
            FileInputStream fis = new FileInputStream("config/config.properties");
            properties.load(fis);
            fis.close();
        }
    }

    public static String getProperty(String key) throws IOException {
        loadProperties();
        return properties.getProperty(key);
    }
}
