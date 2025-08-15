import java.awt.Image;

import javax.swing.ImageIcon;

/*
 * Miscellaneous stuff
 * ICS4U FINAL PROJ JOSEF GAV 
 * 2024/2025
 * */
public class Misc {
	/**
	 * returns resized image icon
	 * */
	public static ImageIcon resizeImageIcon(ImageIcon img,int width, int height) {
		return new ImageIcon(img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}
}
