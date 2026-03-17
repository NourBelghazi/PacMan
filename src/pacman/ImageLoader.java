package pacman;

import java.awt.Image;
import javax.swing.ImageIcon;

public final class ImageLoader {

    private ImageLoader() {}

    public static Image load(String path) {
        return new ImageIcon(ImageLoader.class.getResource(path)).getImage();
    }
}
