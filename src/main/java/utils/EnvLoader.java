package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvLoader {
    private static final Map<String, String> envMap = new HashMap<>();

    static {
        // Загрузка системных переменных
        System.getenv().forEach((k, v) -> envMap.put(k, v));

        // Загрузка из .env файла
        try {
            Properties props = new Properties();
            var path = Paths.get(".env");
            if (Files.exists(path)) {
                props.load(Files.newBufferedReader(path));
                props.forEach((k, v) -> envMap.put(k.toString(), v.toString()));
                System.out.println("Loaded .env file successfully");
            } else {
                System.out.println("No .env file found, using system environment variables");
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return envMap.get(key);
    }
}