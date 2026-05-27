package edu.cit.panugaling.motomeet.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class CleanupImagesRunner {

    public static void main(String[] args) throws Exception {
        Path envPath = Path.of(".env");
        if (!Files.exists(envPath)) {
            System.err.println(".env file not found in current directory: " + System.getProperty("user.dir"));
            System.exit(2);
        }

        Map<String,String> env = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new FileReader(envPath.toFile()))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || !line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                env.put(parts[0].trim(), parts[1].trim());
            }
        }

        String url = env.get("DB_URL");
        String user = env.get("DB_USER");
        String pass = env.get("DB_PASSWORD");

        if (url == null || user == null) {
            System.err.println("DB_URL or DB_USER not found in .env");
            System.exit(3);
        }

        System.out.println("Connecting to: " + url);
        try (Connection c = DriverManager.getConnection(url, user, pass); Statement s = c.createStatement()) {
            int left = s.executeUpdate("UPDATE feed_posts SET image_left_url = NULL WHERE image_left_url LIKE 'https://images.unsplash.com%'");
            int right = s.executeUpdate("UPDATE feed_posts SET image_right_url = NULL WHERE image_right_url LIKE 'https://images.unsplash.com%'");
            System.out.println("Image cleanup complete. Left cleared: " + left + ", Right cleared: " + right + ".");
        }
    }
}
