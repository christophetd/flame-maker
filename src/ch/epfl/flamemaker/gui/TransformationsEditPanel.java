/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import ch.epfl.flamemaker.anim.FlameAnimation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;

@SuppressWarnings("serial")
public class TransformationsEditPanel extends JPanel {

	private List<Listener> m_listeners = new LinkedList<Listener>();
	
	private int m_time;
	
	JList m_transformationsList;
	
	public TransformationsEditPanel(final FlameAnimation.Builder animBuilder){
		setLayout(new BorderLayout());
		
		this.add(new TimelineComponent(animBuilder), BorderLayout.CENTER);
	}
	
	//private JPanel makeRow(){
		
	//}
	
	public void setSelectedTransformationIndex(int id) {
		m_transformationsList.setSelectedIndex(id);
	}
	
	public void setTime(int time){
		m_time = time;
	}
	
	
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	private void notifyTransformationSelected(int id){
		for(Listener l : m_listeners){
			l.onTransformationSelected(id);
		}
	}
	
	public interface Listener {
		public void onTransformationSelected(int transfoId);
		public void onTimeChange(int time);
	}
}
