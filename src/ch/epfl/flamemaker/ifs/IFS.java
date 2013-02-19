package ch.epfl.flamemaker.ifs;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.flamemaker.geometry2d.*;

public class IFS {

	private List<AffineTransformation> m_transforms;
	
	public IFS(List<AffineTransformation> transformations){
		
		m_transforms = new ArrayList<AffineTransformation>(transformations);
	}
	
	public IFSAccumulator compute(Rectangle frame, int width, int height, int density){
		//TODO : not yet implemented
		return null;
	}
}
