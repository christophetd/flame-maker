package ch.epfl.flamemaker.anim;

import java.util.ArrayList;
import java.util.List;

public class FlameAnimation {
	
	public final static double FRAME_RATE = 24;
	
	private final int m_duration;
	
	private final List<KeyFrame> m_frames = new ArrayList<KeyFrame>();
	
	public FlameAnimation(){
		m_duration = 240;
	}
	
	public final void computeFrame(int frameNb) {
		
	}
	
	public int getDuration(){
		return m_duration;
	}
}
