package poe.level.data;

import com.sun.istack.internal.NotNull;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Logger;

public class Util {

    private static final Logger m_logger = Logger.getLogger(Util.class.getName());

    public static class HttpResponse {
        public int responseCode = -1;
        public String responseString = "Undefined";
    }

    public static Image charToImage(String className, String asc) {
        Image result = null;
        try {
            BufferedImage img = ImageIO.read(Util.class.getResource("/classes/" + className + "/" + asc + ".png"));
            result = SwingFXUtils.toFXImage(img, null);
        } catch (Exception ex) {
            m_logger.severe("Failed to load ascendancy image for class: " + className + " asc: " + asc);
        }

        return result;
    }

    @NotNull
    public static HttpResponse httpToString(String url) {
        m_logger.info("Retrieving URL: " + url);
        HttpURLConnection connection = null;
        HttpResponse httpResponse = new HttpResponse();
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Path-Of-Leveling");
            connection.connect();
            httpResponse.responseCode = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            connection.disconnect();
            in.close();
            httpResponse.responseString = response.toString();
            connection.disconnect();

        } catch (IOException ex) {
            m_logger.severe(ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return httpResponse;
    }

    public static void downloadFile(URL url, File outputFile) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream())) {
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }
}
