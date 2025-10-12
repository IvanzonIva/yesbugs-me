package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config() {
        try(InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("❌ config.properties not found in resources");
                throw new RuntimeException("config.properties not found in resources");
            }
            properties.load(input);
            System.out.println("✅ config.properties loaded successfully: " + properties);
        } catch (IOException e) {
            System.err.println("❌ Failed to load config.properties: " + e.getMessage());
            throw new RuntimeException("Fail to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        //Приоритет 1 - переменная системы baseApiUrl =...
        String systemValue = System.getProperty(key);

        if (systemValue != null) {
            System.out.println("🔧 Using system property '" + key + "': " + systemValue);
            return systemValue;
        }

        //Приоритет 2 - переменная окружения baseApi - BASEAPIURL
        //Если в переменной присутвутют точки, то будет преобразование с нижним подчеркиваним
        String envKey = key.toUpperCase().replace('.','_');
        String envValue = System.getenv(envKey);

        if (envValue != null) {
            System.out.println("🔧 Using environment variable '" + envKey + "': " + envValue);
            return envValue;
        }

        //Приоритет 3 - config.properties
        String value = INSTANCE.properties.getProperty(key);
        System.out.println("🔧 Getting property '" + key + "': " + value);
        return value;
    }
}