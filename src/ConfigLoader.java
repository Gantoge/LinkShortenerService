
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "src\\config.properties.txt";

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Ошибка загрузки конфигурации: " + e.getMessage());
        }
    }

    public static long getMaxLinkLifetime() {
        String value = properties.getProperty("maxLinkLifetime");
        return Long.parseLong(value);
    }
    public static int getMaxClicksLimit() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(CONFIG_FILE));
            return Integer.parseInt(properties.getProperty("maxClicks"));
        } catch (IOException | NumberFormatException e) {
            System.out.println("Ошибка загрузки лимита переходов из конфигурации: " + e.getMessage());
            return 5;
        }
    }
}
