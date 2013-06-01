package ch.epfl.flamemaker.anim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Animation<E>
{
	private final List<KeyFrame<E>> m_keyFrames;
	
	
	public Animation(Animable<E> elem){
		m_keyFrames = new ArrayList<KeyFrame<E>>();
		m_keyFrames.add(new KeyFrame<E>(0, elem));
	}
	
	public Animation(Animation<E> source){
		m_keyFrames = new ArrayList<KeyFrame<E>>(source.m_keyFrames);
	}
	
	public Animation(List<KeyFrame<E>> keyFrames){
		if(keyFrames.size() == 0) throw new IllegalStateException("An animation must have at least one keyframe");
		m_keyFrames = new ArrayList<KeyFrame<E>>(keyFrames);
	}
	
	public final E get(int time){
		KeyFrame<E> last = null;
		
		for(KeyFrame<E> k : m_keyFrames){
			if(k.time() > time){
				if(last != null){
					return last.interpolate(k, time);
				}
				return k.get().get();
			}
			last = k;
		}
		
		if(last != null)
			return last.get().get();
		
		throw new IllegalStateException("Animation does not contain any keyframe");
	}
	
	public List<KeyFrame<E>> keyFrames(){
		return new ArrayList<KeyFrame<E>>(m_keyFrames);
	}
	
	public KeyFrame<E> getKeyFrame(int id){
		return m_keyFrames.get(id);
	}
	
	public int getKeyframesCount(){
		return m_keyFrames.size();
	}
	
	public static class KeyFrame<E> {
		private final int m_time;
		
		private final Animable<E> m_data;
		
		public KeyFrame(int time, Animable<E> data){
			m_time = time;
			m_data = data;
		}
		
		public int time(){
			return m_time;
		}
		
		public Animable<E> get(){
			return m_data;
		}
		
		public E interpolate(KeyFrame<E> other, int time){
			return m_data.interpolate(other.m_data, (double)(time - m_time)/(other.m_time - m_time));
		}
	}
	
	public static class Builder<E>{
		private final List<KeyFrame<E>> m_keyFrames;
		
		public Builder(){
			m_keyFrames = new LinkedList<KeyFrame<E>>();
		}
		
		public Builder(Animation<E> source){
			m_keyFrames = new LinkedList<KeyFrame<E>>(source.m_keyFrames);
		}
		
		public final E get(int time){
			return build().get(time);
		}
		
		public final void set(Animable<E> elem, int time){
			
			ListIterator<KeyFrame<E>> it = m_keyFrames.listIterator();
			while(it.hasNext()){
				KeyFrame<E> k = it.next();
				
				if(k.time() == time){
					it.remove();
					break;
				} else if(k.time() > time){
					it.previous();
					break;
				}
			}
			it.add(new KeyFrame<E>(time, elem));
		}
		
		public final void removeKeyAtTime(int time){
			ListIterator<KeyFrame<E>> it = m_keyFrames.listIterator();
			while(it.hasNext()){
				KeyFrame<E> k = it.next();
				if(k.time() == time){
					it.remove();
					return;
				}
			}
		}
		
		public final void removeKey(int id){
			m_keyFrames.remove(id);
		}
		
		public List<KeyFrame<E>> keyFrames(){
			return new ArrayList<KeyFrame<E>>(m_keyFrames);
		}
		
		public KeyFrame<E> getKeyFrame(int id){
			return m_keyFrames.get(id);
		}
		
		public int getKeyframesCount(){
			return m_keyFrames.size();
		}
		
		public Animation<E> build(){
			return new Animation<E>(m_keyFrames);
		}
	}
}
