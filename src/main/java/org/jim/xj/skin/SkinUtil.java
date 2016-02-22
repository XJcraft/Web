package org.jim.xj.skin;

import java.awt.Image;
import java.awt.image.BufferedImage;

public class SkinUtil {

	public static BufferedImage head(BufferedImage image){
		Image img = image.getSubimage(8, 8, 8, 8);
		Image goggles = image.getSubimage(40, 8, 8, 8);
		BufferedImage finalImage = new BufferedImage(64, 64, 4);
		finalImage.getGraphics().drawImage(img, 0, 0, 64, 64, null);
		finalImage.getGraphics().drawImage(goggles, 0, 0, 64, 64, null);
		return finalImage;
	}
	
	public static BufferedImage skinPreview(BufferedImage image){
		return new SkinPreview(image).draw();
	}
}
