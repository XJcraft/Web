package org.jim.xj.skin;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SkinPreview {

	private BufferedImage source;
	private BufferedImage preview;
	
	public SkinPreview(File file) {
		super();
		try {
			this.source = ImageIO.read(file);
		} catch (IOException e) {
			throw new IllegalArgumentException("Image file not found", e);
		}
		
	}
	
	public SkinPreview(BufferedImage source) {
		super();
		this.source = source;
	}

	public BufferedImage draw(){
		if(preview == null){
			preview = new BufferedImage(16, 28, BufferedImage.TYPE_INT_ARGB);
			// head
			BufferedImage head = getImage(source, new Rectangle(8, 8, 8, 8),new Rectangle(40, 8, 8, 8));
			BufferedImage body = getImage(source, new Rectangle(20, 20, 8, 12),new Rectangle(20, 36, 8, 12));
			BufferedImage leftLeg = getImage(source, new Rectangle(36, 52, 4, 12),new Rectangle(52, 52, 4, 12));
			BufferedImage rightLeg = getImage(source, new Rectangle(44, 20, 4, 12),new Rectangle(44, 36, 4, 12));
			BufferedImage leftFoot = getImage(source, new Rectangle(4, 20, 4, 12),new Rectangle(4, 36, 4, 12));
			BufferedImage rightFoot = getImage(source, new Rectangle(20, 52, 4, 12),new Rectangle(4, 52, 4, 12));
			
			preview.getGraphics().drawImage(head, 4, 0, null);
			preview.getGraphics().drawImage(body, 4, 8, null);
			preview.getGraphics().drawImage(rightLeg, 0, 8, null);
			preview.getGraphics().drawImage(leftLeg, 12, 8, null);
			preview.getGraphics().drawImage(rightFoot, 4, 16, null);
			preview.getGraphics().drawImage(leftFoot, 8, 16, null);
		}
		return preview;
	}
	
	public BufferedImage drawRight(BufferedImage image){
		BufferedImage img = image.getSubimage(8, 8, 8, 8);
		BufferedImage goggles = image.getSubimage(40, 8, 8, 8);
		img.getGraphics().drawImage(goggles, 0, 0, null);
		return img;
	}
	private BufferedImage getImage(BufferedImage image,Rectangle... rects){
		BufferedImage result = null;
		for(Rectangle rect : rects){
			BufferedImage i = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
			if(result == null)
				result = i;
			else
				result.getGraphics().drawImage(i, 0, 0, null);
		}
		return result;
	}
	public void write(File file) throws IOException{
		ImageIO.write(draw(), "PNG", file);
	}
}
