package poe.level.data;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    public static Image charToImage(String className, String asc) {
        Image result = null;
        try {
            BufferedImage img = ImageIO.read(Util.class.getResource("/classes/" + className + "/" + asc + ".png"));
            result = SwingFXUtils.toFXImage(img, null);
        } catch (Exception ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, "Failed to load ascendancy image for class: " + className + " asc: " + asc);
        }

        return result;
    }
}
