package ch.epfl.flamemaker.color;

import java.awt.image.BufferedImage;

public class ColorUtils {
	
	public static BufferedImage renderPalette(Palette p, int width){
		BufferedImage img = new BufferedImage(width,1, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			// On met à jour la couleur du pixel courant
			img.setRGB(x, 0, p.colorForIndex((double)x/width).asPackedRGB());
		}
		
		return img;
	}
	
	public static Color awtToColor(java.awt.Color col){
		return new Color(col.getRed()/255.0, col.getGreen()/255.0, col.getBlue()/255.0);
	}
	
	public static java.awt.Color colorToAwt(Color c){
		return new java.awt.Color((int)(255*c.red()), (int)(255*c.green()), (int)(255*c.blue()));
	}
	
}
