package ch.epfl.flamemaker.anim;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameFactory;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Variations;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;

public class FlameAnimation {
	
	public final static double FRAME_RATE = 24;
	
	private final int m_duration;
	
	private final List<Animation<FlameTransformation>> m_transforms;
	
	private final FlameFactory m_factory;
	
	public FlameAnimation(List<Animation<FlameTransformation>> transforms, FlameFactory factory, int duration){
		m_duration = duration;
		m_transforms = new ArrayList<Animation<FlameTransformation>>(transforms);
		m_factory = factory;
	}
	
	public final Flame getFlame(int time){
		
		List<FlameTransformation> transformations = new ArrayList<FlameTransformation>(m_transforms.size());
		
		for(Animation<FlameTransformation> t : m_transforms){
			transformations.add(t.get(time));
		}
		
		Flame.Builder builder = new Flame.Builder(new Flame(transformations), m_factory);
		
		return builder.build();
	}
	
	public int getDuration(){
		return m_duration;
	}
	
	public int transformationsCount(){
		return m_transforms.size();
	}
	
	public Animation<FlameTransformation> getTransformation(int id){
		return m_transforms.get(id);
	}
	
	public static class Builder{
		
		private int m_duration = 168;
		
		private List<Animation<FlameTransformation>> m_transformations = new ArrayList<Animation<FlameTransformation>>();
		
		private Set<Listener> m_listeners = new HashSet<Listener>();
		
		private FlameFactory m_factory;
		
		public Builder(FlameAnimation source, FlameFactory factory){
			for(Animation<FlameTransformation> transformation : source.m_transforms) {
				m_transformations.add(new Animation<FlameTransformation>(transformation));
			}
			
			m_factory = factory;
		}
		
		public Builder(FlameAnimation source){
			for(Animation<FlameTransformation> transformation : source.m_transforms) {
				m_transformations.add(new Animation<FlameTransformation>(transformation));
			}
			
			for(FlameFactory f : FlameFactory.ALL_FACTORIES){
				if(f.isSupported()){
					m_factory = f;
					m_factory.enable();
					break;
				}
			}
		}
		
		public void setDuration(int duration){
			m_duration = duration;
			notifyListeners();
		}
		
		public int getDuration(){
			return m_duration;
		}
		
		public FlameAnimation build(){
			return new FlameAnimation(m_transformations, m_factory, m_duration);
		}
		
		public int transformationsCount(){
			return m_transformations.size();
		}
		
		public Animation<FlameTransformation> getTransformation(int id){
			return m_transformations.get(id);
		}
		
		public void addTransformation(Animation<FlameTransformation> transformation) {
			m_transformations.add(new Animation<FlameTransformation>(transformation));
			notifyListeners();
		}
		
		public void removeTransformation(int transfoId){
			m_transformations.remove(transfoId);
			notifyListeners();
		}
		
		public void setTransformation(int id, Animation<FlameTransformation> transformation){
			m_transformations.set(id, transformation);
			notifyListeners();
		}
		
		public AffineTransformation affineTransformation(int id, int time){
			return m_transformations.get(id).get(time).affineTransformation();
		}
		
		public void setAffineTransformation(int id, AffineTransformation trns, int time){
			
			Animation.Builder<FlameTransformation> animBuilder = new Animation.Builder<FlameTransformation>(m_transformations.get(id));
			FlameTransformation.Builder builder = new FlameTransformation.Builder(animBuilder.get(time));
			builder.setAffineTransformation(trns);
			animBuilder.set(new AnimableTransformation(builder.build()), time);
			
			m_transformations.set(id, animBuilder.build());
			
			notifyListeners();
		}
		
		public FlameFactory getComputeStrategy(){
			return m_factory;
		}
		
		public void setComputeStrategy(FlameFactory factory){
			m_factory = factory;
			
			notifyListeners();
		}
		
		public double getVariationWeight(int index, Variations variation, int time) {
			checkIndex(index);
			
			return m_transformations.get(index).get(time).weight(variation.index());
		}
		
		public void setVariationWeight(int id, Variations variation, double newWeight, int time) {
			Animation.Builder<FlameTransformation> animBuilder = new Animation.Builder<FlameTransformation>(m_transformations.get(id));
			FlameTransformation.Builder builder = new FlameTransformation.Builder(animBuilder.get(time));
			builder.setWeight(variation.index(), newWeight);
			animBuilder.set(new AnimableTransformation(builder.build()), time);
			
			m_transformations.set(id, animBuilder.build());
			
			notifyListeners();
		}
		
		private void notifyListeners(){
			for(Listener l : m_listeners){
				l.onFlameBuilderChange(this);
			}
		}
		
		private void checkIndex(int index) {
			if (index < 0 || index >= m_transformations.size()) {
				throw new IllegalArgumentException();
			}
		}
		
		
		
		public void addListener(Listener l){
			m_listeners.add(l);
		}
		
		public void removeListener(Listener l){
			m_listeners.add(l);
		}
		
		public interface Listener{
			public void onFlameBuilderChange(Builder b);
		}
	}
}