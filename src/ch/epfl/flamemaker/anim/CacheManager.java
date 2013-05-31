package ch.epfl.flamemaker.anim;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ch.epfl.flamemaker.anim.FlameAnimation.Builder;

public class CacheManager {
	
	private FlameAnimation.Builder m_flameBuilder;
	
	private Map<Integer, SoftReference<BufferedImage>> m_cache;
	
	private Set<Listener> m_listeners = new TreeSet<Listener>();
	
	public CacheManager(FlameAnimation.Builder builder){
		m_flameBuilder = builder;
		m_cache = new HashMap<Integer, SoftReference<BufferedImage>>(m_flameBuilder.getDuration()+1);
		
		builder.addListener(new FlameAnimation.Builder.Listener(){
			@Override
			public void onFlameBuilderChange(Builder b) {
				clear();
			}
		});
	}
	
	public boolean available(int frame){
		SoftReference<BufferedImage> ref = m_cache.get(frame);
		return (ref != null) ? (ref.get() != null) : false;
	}
	
	public void setFrame(int pos, BufferedImage frame){
		m_cache.put(pos, new SoftReference<BufferedImage>(frame));
		notifyListeners();
	}
	
	public BufferedImage getFrame(int pos){
		SoftReference<BufferedImage> ref = m_cache.get(pos);
		return (ref != null) ? ref.get() : null;
	}
	
	public void clear(){
		// Clears the cache
		m_cache = null;
		m_cache = new HashMap<Integer, SoftReference<BufferedImage>>(m_flameBuilder.getDuration()+1);
		notifyListeners();
	}
	
	private void notifyListeners(){
		for(Listener l : m_listeners){
			l.onCacheChange(this);
		}
	}
	
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	public interface Listener{
		public void onCacheChange(CacheManager cache);
	}
}
