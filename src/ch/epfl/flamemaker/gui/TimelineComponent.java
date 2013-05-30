package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;

import ch.epfl.flamemaker.anim.FlameAnimation;

public class TimelineComponent extends JComponent{

	private static final long serialVersionUID = 1347861945789465193L;

	private FlameAnimation.Builder m_anim;
	
	private int m_time;
	
	private Set<Listener> m_listeners = new TreeSet<Listener>();
	
	// User interaction variables
	private boolean m_draggingCursor;
	
	public TimelineComponent(FlameAnimation.Builder anim) {
		m_anim = anim;
		setPreferredSize(new Dimension(500, 200));
		createBehaviour();
	}
	
	@Override
	protected void paintComponent(final Graphics g){
		
		Graphics2D g0 = (Graphics2D) g;
		
		g0.setColor(new Color(180, 180, 180));
		g0.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		double duration = m_anim.getDuration() / FlameAnimation.FRAME_RATE;
		double blockLength = getWidth() / duration;
		
		g0.setColor(Color.BLACK);
		
		
		for(int i = 0 ; i < duration ; i++){
			g0.draw( new Line2D.Double(blockLength * i, 0, blockLength * i, 20));
		}
		
		for(int i = 0 ; i < 4*duration ; i++){
			g0.draw( new Line2D.Double(blockLength/4 * i, 0, blockLength/4 * i, 10));
		}
		
		
		// Draws a dummy keyframe
		//drawKeyFrame(g0, getWidth()/9);
		
		// Draws the cursor
		double cursorPos = (double)m_time/m_anim.getDuration() * getWidth();
		g0.setColor(Color.RED);
		g0.draw( new Line2D.Double(cursorPos, 0, cursorPos, getHeight()));
	}
	
	private void drawKeyFrame(final Graphics2D g0, double x){
		
		double y = getHeight()/2;
		double r = 2*getHeight()/5;
		
		g0.setColor(Color.YELLOW);
		
		g0.fill( new Ellipse2D.Double(x - r/2, y - r/2, r, r));
	}
	
	public void setTime(int time){
		m_time = time;
		repaint();
	}
	
	public void setSelectedTransformationIndex(int id){
		
	}
	
	private void changeTime(int t){
		if(t < 0) t = 0;
		if(t > m_anim.getDuration()) t = m_anim.getDuration();
		
		m_time = t;
		for(Listener l : m_listeners){
			l.onTimeChange(m_time);
		}
	}
	
	
	private void createBehaviour(){
		
		m_draggingCursor = false;
		
		
		addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent evt) {
				if(m_draggingCursor){
					changeTime((int)((double)evt.getX()/getWidth() * m_anim.getDuration()));
				}
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
				if(hoverCursor(evt.getX())){
					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		
		addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				if(hoverCursor(evt.getX())){
					m_draggingCursor = true;
				} else {
					changeTime((int)((double)evt.getX()/getWidth() * m_anim.getDuration()));
					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
					m_draggingCursor = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				m_draggingCursor = false;
			}
			
		});
	}
	
	private boolean hoverCursor(int x){
		double cursorPos = (double)m_time/m_anim.getDuration() * getWidth();
		return x < cursorPos + 2 && x > cursorPos - 2;
	}
	
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	public interface Listener{
		public void onTimeChange(int time);
		public void onTransformationSelected(int id);
	}
}
