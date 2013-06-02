package ch.epfl.flamemaker.color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.flamemaker.anim.Animation;

/**
 * Implements a palette with colors freely placed among it
 */
public class CustomPalette implements Palette {
	
	private static final long serialVersionUID = -2362616395864965427L;
	
	private static final int EXPAND_COEFF = Integer.MAX_VALUE;
	
	// Uses internally an animation to interpolate the colors
	private final Animation<Color> m_palette;
	
	public CustomPalette(List<Color> colors){
		Animation.Builder<Color> builder = new Animation.Builder<Color>();
		
		int step = EXPAND_COEFF/(colors.size()-1);
		int pos = 0;
		for(Color c : colors){
			builder.set(new AnimableColor(c), pos);
			pos += step;
		}
		
		m_palette = builder.build();
	}
	
	private CustomPalette(Animation<Color> palette){
		m_palette = palette;
	}
	
	@Override
	public Color colorForIndex(double index) {
		return m_palette.get((int)(Math.floor(index*EXPAND_COEFF)));
	}
	
	public Map<Double, Color> getColors(){
		List<Animation.KeyFrame<Color>> colors = m_palette.keyFrames();
		
		Map<Double, Color> ret = new HashMap<Double, Color>();
		
		for(Animation.KeyFrame<Color> k : colors){
			ret.put(k.time()/(double)EXPAND_COEFF, k.get().get());
		}
		
		return ret;
	}
	
	public static class Builder {
		
		private Map<Double, Color> m_colors;
		
		private Set<Listener> m_listeners = new HashSet<Listener>();
		
		public Builder(){
			m_colors = new HashMap<Double, Color>();
		}
		
		public Builder(CustomPalette ref){
			m_colors = new HashMap<Double, Color>(ref.getColors());
		}
		
		public void setColor(Color c, double pos){
			m_colors.put(pos, c);
			
			notifyListeners();
		}
		
		public Color getColor(double pos){
			return m_colors.get(pos);
		}
		
		public void removeColorAtPos(double pos){
			m_colors.remove(pos);
			
			notifyListeners();
		}
		
		public Map<Double, Color> getColors(){
			return new HashMap<Double, Color>(m_colors);
		}
		
		public int getColorsCount(){
			return m_colors.size();
		}
		
		public CustomPalette build(){
			
			Animation.Builder<Color> paletteBuilder = new Animation.Builder<Color>();
			
			Set<Double> keys = m_colors.keySet();
			
			for(Double k : keys){
				paletteBuilder.set(new AnimableColor(m_colors.get(k)), (int)(Math.floor(k*EXPAND_COEFF)));
			}
			
			return new CustomPalette(paletteBuilder.build());
		}
		
		private void notifyListeners(){
			for(Listener l : m_listeners){
				l.onPaletteChange(this);
			}
		}
		
		public void addListener(Listener l){
			m_listeners.add(l);
		}
		
		public void removeListener(Listener l){
			m_listeners.remove(l);
		}
		
		public interface Listener {
			public void onPaletteChange(CustomPalette.Builder p);
		}
	}
}
