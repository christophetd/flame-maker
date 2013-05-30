package ch.epfl.flamemaker.anim;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Animation<T extends Animable<E>, E>
{
	private final List<KeyFrame<T, E>> m_keyFrames;
	
	public Animation(){
		m_keyFrames = new LinkedList<KeyFrame<T, E>>();
	}
	
	public Animation(Animation<T, E> source){
		m_keyFrames = new LinkedList<KeyFrame<T, E>>(source.m_keyFrames);
	}
	
	public final void set(T elem, int time){
		ListIterator<KeyFrame<T, E>> it = m_keyFrames.listIterator();
		while(it.hasNext()){
			KeyFrame<T, E> k = it.next();
			
			if(k.time() == time){
				it.remove();
				break;
			} else if(k.time() > time){
				break;
			}
		}
		it.add(new KeyFrame<T,E>(time, elem));
	}
	
	public final void remove(int time){
		ListIterator<KeyFrame<T, E>> it = m_keyFrames.listIterator();
		while(it.hasNext()){
			KeyFrame<T, E> k = it.next();
			if(k.time() == time){
				it.remove();
				return;
			}
		}
	}
	
	public final E get(int time){
		KeyFrame<T, E> last = null;
		
		for(KeyFrame<T, E> k : m_keyFrames){
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
		
		return null;
	}
	
	public static class KeyFrame<T extends Animable<E>, E> {
		private final int m_time;
		
		private final T m_data;
		
		public KeyFrame(int time, T data){
			m_time = time;
			m_data = data;
		}
		
		public int time(){
			return m_time;
		}
		
		public T get(){
			return m_data;
		}
		
		public E interpolate(KeyFrame<T, E> other, int time){
			return m_data.interpolate(other.m_data, other.m_time - m_time, time - other.m_time);
		}
	}
}
