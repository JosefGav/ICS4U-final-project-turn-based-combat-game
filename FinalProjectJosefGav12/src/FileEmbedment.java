/*
 * FileEmbedment objects allow me to access images for my game within Jar files
 * Based off MR BENUMs classroom post
 * */

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class FileEmbedment {
	public ImageIcon returnImageIcon(String name) {
		ImageIcon icon = null;
		try {
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream(name));
		    icon = new ImageIcon(image);
			return icon;
		} catch (Exception e) {
			return icon;
		}
	}
	
	public static void main(String[] args) {
		FileEmbedment fe = new FileEmbedment();
		
		System.out.println(fe.returnImageIcon("characters_icon/frosty.png"));
	}
}
