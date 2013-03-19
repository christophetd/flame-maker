package ch.epfl.flamemaker.color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import ch.epfl.flamemaker.InterpolatedPalette;
import ch.epfl.flamemaker.Palette;

public class PaletteTest {

	public static void main(String[] args){
		
		List<Color> c = new ArrayList<Color>();
		c.add(Color.RED); c.add(Color.GREEN); c.add(Color.BLUE);
		Palette palette = new InterpolatedPalette(c);
		
		int[] byteArray = new int[100*10];
		
		double step = 1.0/100;
		for(int i = 0 ; i < 100 ; i ++){
			
			int color = palette.colorForIndex(step*i).asPackedRGB();
			
			for(int j = 0 ; j < 10 ; j ++){
				byteArray[100*j+i] = color;
				
			}
		}
		
		WritableRaster raster = Raster.createPackedRaster(DataBuffer.TYPE_INT, 100, 10, 3, 8, null);
		raster.setDataElements(0, 0, 100, 10, byteArray);

		BufferedImage newimage = new BufferedImage(100, 10, BufferedImage.TYPE_3BYTE_BGR);
		newimage.setData(raster);
       
		System.out.println("Writing to HDD...");
		
        File outputfile = new File("band.png");
        try {
			ImageIO.write(newimage, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
