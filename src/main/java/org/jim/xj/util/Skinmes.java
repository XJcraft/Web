package org.jim.xj.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Skinmes {

	private static String URL = "http://www.skinme.cc:88/MinecraftSkins/%s.png";
	private static String URLCLOAK = "http://www.skinme.cc:88/MinecraftCloaks/%s.png";

	public static BufferedImage skin(String uid) throws IOException {
		URL url = new URL(String.format(URL, uid));
		BufferedImage image = ImageIO.read(url);
		return image;
	}

	public static BufferedImage cloak(String uid) throws IOException {
		URL url = new URL(String.format(URLCLOAK, uid));
		BufferedImage image = ImageIO.read(url);
		return image;
	}

}
