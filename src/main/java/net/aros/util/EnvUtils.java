package net.aros.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EnvUtils {
    public static Properties getEnv(String path) {
        Properties properties = new Properties();
        try (InputStream stream = Files.newInputStream(Paths.get(path))) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            createEnv(path);
        }
        return properties;
    }

    public static void createEnv(String path) {
        try {
            try (Writer stream = Files.newBufferedWriter(Files.createFile(Paths.get(path)))) {
                stream.write("TOKEN=*YOUR_TOKEN_HERE*");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getToken(String path) {
        return getEnv(path).getProperty("TOKEN");
    }
}
