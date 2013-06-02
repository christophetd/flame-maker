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
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import ch.epfl.flamemaker.anim.AnimableTransformation;
import ch.epfl.flamemaker.anim.Animation;
import ch.epfl.flamemaker.anim.CacheManager;
import ch.epfl.flamemaker.anim.FlameAnimation;
import ch.epfl.flamemaker.color.CustomPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.FlameTransformation;

public class TimelineComponent extends JComponent{

	private static final long serialVersionUID = 1347861945789465193L;

	private final FlameAnimation.Builder m_animBuilder;
	
	private final CacheManager m_cache;
	
	private int m_time;
	
	private int m_selectedTransformationId;
	
	private int m_selectedKeyframeTime = -1;
	private int m_selectedKeyframeTID  = -1; // Row id of selected key frame
	
	private Set<Listener> m_listeners = new HashSet<Listener>();
	
	private double m_unitLength;
	
	// User interaction variables
	private boolean m_draggingCursor;
	private boolean m_draggingKeyframe;
	
	// Drawing properties :
	private final static int HEADER_HEIGHT 		= 20;
	private final static int BIG_GRADU_HEIGHT 	= 12;
	private final static int SMALL_GRADU_HEIGHT = 6;
	private final static int TRANSFORM_HEIGHT	= 25;
	private final static int KEYFRAME_RADIUS	= 6;
	private final static int LABELS_PANEL_WIDTH = 70;
	
	private final static Color TRANSFO_COLOR_EVEN 	= new Color(150, 150, 150);
	private final static Color TRANSFO_COLOR_ODD 	= new Color(200, 200, 200);
	private final static Color TRANSFO_SEL_COLOR	= new Color(160, 160, 200);
	private final static Color PANEL_COLOR_EVEN 	= new Color(170, 170, 170);
	private final static Color PANEL_COLOR_ODD 		= new Color(180, 180, 180);
	private final static Color PANEL_SEL_COLOR		= new Color(140, 140, 190);
	private final static Color BACKGROUND_COLOR		= new Color(100, 100, 100);
	private final static Color TEXT_COLOR			= Color.BLACK;
	private final static Color KEYFRAME_FILL_COLOR	= Color.YELLOW;
	private final static Color KEYFRAME_STROKE_COLOR= Color.BLACK;
	private final static Color KEYFRAME_DRAG_COLOR	= new Color(255, 125, 0);
	private final static Color CURSOR_COLOR			= Color.RED;
	private final static Color CACHE_COLOR			= Color.GREEN;
	private final static Color HEADER_COLOR			= new Color(200, 200, 200);
	private final static Color LABELS_PANEL_COLOR	= new Color(120, 120, 120);
	
	public TimelineComponent(FlameAnimation.Builder builder, CacheManager cache) {
		m_animBuilder = builder;
		m_cache = cache;
		
		setPreferredSize(new Dimension(500, 200));
		createBehaviour();
		
		m_cache.addListener(new CacheManager.Listener(){

			@Override
			public void onCacheChange(CacheManager cache) {
				repaint();
			}
			
		});
	}
	
	@Override
	protected void paintComponent(final Graphics g){
		
		// Build the animation first
		FlameAnimation anim = m_animBuilder.build();
		
		Graphics2D g0 = (Graphics2D) g;
		m_unitLength = ((double)getWidth() - LABELS_PANEL_WIDTH)/anim.getDuration();
		
		g0.setColor(BACKGROUND_COLOR);
		g0.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g0.setColor(LABELS_PANEL_COLOR);
		g0.fillRect(0, 0, LABELS_PANEL_WIDTH, getHeight());
		
		drawHeader(g0, anim);
		
		// Draws the animations
		for(int i = 0 ; i < anim.transformationsCount() ; i++){
			drawTransform(g0, i, anim.getTransformation(i));
		}
		
		// Draws the cursor
		double cursorPos = LABELS_PANEL_WIDTH + (double)m_time/anim.getDuration() * (getWidth() - LABELS_PANEL_WIDTH);
		g0.setColor(CURSOR_COLOR);
		g0.draw( new Line2D.Double(cursorPos, 0, cursorPos, getHeight()));
	}
	
	private void drawHeader(final Graphics2D g0, FlameAnimation anim){
		double duration = anim.getDuration() / FlameAnimation.FRAME_RATE;
		double blockLength = (getWidth() - LABELS_PANEL_WIDTH)/ duration;
		
		g0.setColor(HEADER_COLOR);
		g0.fillRect(LABELS_PANEL_WIDTH, 0, (this.getWidth() - LABELS_PANEL_WIDTH), HEADER_HEIGHT);
		
		g0.setColor(CACHE_COLOR);
		for(int i = 0 ; i < anim.getDuration() ; i++){
			if(m_cache.available(i)){
				g0.fillRect(LABELS_PANEL_WIDTH + (int)(i*m_unitLength), 17, (int)Math.ceil(m_unitLength), 3);
			}
		}
		
		g0.setColor(Color.BLACK);
		for(int i = 0 ; i < duration ; i++){
			g0.draw( new Line2D.Double(LABELS_PANEL_WIDTH + blockLength * i, 0, LABELS_PANEL_WIDTH + blockLength * i, BIG_GRADU_HEIGHT));
		}
		for(int i = 0 ; i < 4*duration ; i++){
			g0.draw( new Line2D.Double(LABELS_PANEL_WIDTH + blockLength/4 * i, 0, LABELS_PANEL_WIDTH + blockLength/4 * i, SMALL_GRADU_HEIGHT));
		}
	}
	
	private void drawKeyFrame(final Graphics2D g0, int time, int transfoId, Color baseColor){
		double x = LABELS_PANEL_WIDTH + time* m_unitLength;
		double y = HEADER_HEIGHT + TRANSFORM_HEIGHT*transfoId + TRANSFORM_HEIGHT/2 - KEYFRAME_RADIUS;
		
		g0.setColor(baseColor);
		g0.fill( new Ellipse2D.Double(x - KEYFRAME_RADIUS, y, KEYFRAME_RADIUS*2, KEYFRAME_RADIUS*2));
		
		if(time == m_time){
			g0.setColor(KEYFRAME_STROKE_COLOR);
			g0.draw( new Ellipse2D.Double(x - KEYFRAME_RADIUS, y, KEYFRAME_RADIUS*2, KEYFRAME_RADIUS*2));
		}
	}
	
	private <E> void drawAnimation(final Graphics2D g0, Animation<E> anim, int row, String label, boolean selected){
		// Fills the strip background
				if(selected){
					g0.setColor(TRANSFO_SEL_COLOR);
				} else {
					g0.setColor((row % 2 == 0 ) ? TRANSFO_COLOR_EVEN : TRANSFO_COLOR_ODD);
				}
				g0.fillRect(LABELS_PANEL_WIDTH, HEADER_HEIGHT + TRANSFORM_HEIGHT*row, getWidth() - LABELS_PANEL_WIDTH, TRANSFORM_HEIGHT);
				
				// Draws keyframes
				for(Animation.KeyFrame<E> k : anim.keyFrames()){
					drawKeyFrame(g0, k.time(), row, KEYFRAME_FILL_COLOR);
				}
				// Draws the dragged keyframe last so that it is on top
				if(m_selectedKeyframeTID == row && m_selectedKeyframeTime != -1){
					drawKeyFrame(g0, m_selectedKeyframeTime, row, KEYFRAME_DRAG_COLOR);
				}
				
				if(selected){
					g0.setColor(PANEL_SEL_COLOR);
				} else {
					g0.setColor((row % 2 == 0 ) ? PANEL_COLOR_EVEN : PANEL_COLOR_ODD);
				}
				g0.fillRect(0, HEADER_HEIGHT + TRANSFORM_HEIGHT*row, LABELS_PANEL_WIDTH, TRANSFORM_HEIGHT);
				
				g0.setColor(TEXT_COLOR);
				g0.drawString(label, 5, HEADER_HEIGHT + TRANSFORM_HEIGHT*row + TRANSFORM_HEIGHT - 5);
	}
	
	private void drawTransform(final Graphics2D g0, int id, Animation<FlameTransformation> transfo){
		
		this.<FlameTransformation>drawAnimation(g0, transfo, id, "Transfo. "+(id+1), m_selectedTransformationId == id);

	}
	
	public void setTime(int time){
		m_time = time;
		repaint();
	}
	
	public void setSelectedTransformationIndex(int id){
		m_selectedTransformationId = id;
		repaint();
	}
	
	private void changeTime(int t){
		if(t < 0) t = 0;
		if(t > m_animBuilder.getDuration()) t = m_animBuilder.getDuration();
		
		m_time = t;
		for(Listener l : m_listeners){
			l.onTimeChange(m_time);
		}
	}
	
	
	private void createBehaviour(){
		
		addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent evt) {
				int newTime = posToTime(evt.getX() + (int)m_unitLength/2);
				if(newTime < 0) newTime = 0;
				else if(newTime > m_animBuilder.getDuration()) newTime = m_animBuilder.getDuration();
				
				if(m_draggingCursor){
					changeTime(newTime);
				} else if(m_draggingKeyframe){
					Animation.Builder<FlameTransformation> tr = 
							new Animation.Builder<FlameTransformation>(m_animBuilder.getTransformation(m_selectedTransformationId));
					
					Animation.KeyFrame<FlameTransformation> key = getKeyframeForTime(tr, m_selectedKeyframeTime);
					//Checks if there's already a key at this place
					Animation.KeyFrame<FlameTransformation> curKey = getKeyframeForTime(tr, newTime);
					
					if(key != null && curKey == null){
						tr.removeKeyAtTime(m_selectedKeyframeTime);
						tr.set(key.get(), newTime);
						m_selectedKeyframeTime = newTime;
						m_selectedKeyframeTID  = m_selectedTransformationId;
						
						m_animBuilder.setTransformation(m_selectedTransformationId, tr.build());
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
				if(evt.getY() < HEADER_HEIGHT && evt.getX() > LABELS_PANEL_WIDTH){
					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		
		addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent evt) {
				if(selectTransfo(evt.getX() + (int)m_unitLength/2, evt.getY())){ // click on transfo
					if(evt.getClickCount() == 2){ // Double click
						Animation.Builder<FlameTransformation> tr = 
								new Animation.Builder<FlameTransformation>(m_animBuilder.getTransformation(m_selectedTransformationId));
						int time = (int)((double)(evt.getX() + (int)m_unitLength/2 - LABELS_PANEL_WIDTH)/(getWidth() - LABELS_PANEL_WIDTH) * m_animBuilder.getDuration());
						
						if(getKeyframeForTime(tr, time) == null){
							tr.set(new AnimableTransformation(tr.get(time)), time); // Sets a key at time t with current config
							m_animBuilder.setTransformation(m_selectedTransformationId, tr.build());
							m_selectedKeyframeTime = m_time;
							m_selectedKeyframeTID  = m_selectedTransformationId;
						}
					}
					else if(selectKeyframe(evt.getX(), evt.getY())){
						changeTime(m_selectedKeyframeTime);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				// Clicks on the header
				if(evt.getY() < HEADER_HEIGHT){
					changeTime(posToTime(evt.getX() + (int)m_unitLength/2));
					setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
					m_draggingCursor = true;
				// Clicks on keyframe
				} else if(selectKeyframe(evt.getX(), evt.getY())){
					m_draggingKeyframe = true;
				// Clicks on animation
				} else {
					selectTransfo(evt.getX() + (int)m_unitLength/2, evt.getY());
					if(evt.getX() > LABELS_PANEL_WIDTH)
						changeTime(posToTime(evt.getX() + (int)m_unitLength/2));
				}
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				m_draggingCursor = false;
				m_draggingKeyframe = false;
			}
			
		});
	}
	
	private int posToTime(int x){
		return (int)((double)(x - LABELS_PANEL_WIDTH)/ (getWidth()- LABELS_PANEL_WIDTH) * m_animBuilder.getDuration());
	}
	
	private boolean selectTransfo(int x, int y){
		if(y < HEADER_HEIGHT) return false;
		
		int selId = (y - HEADER_HEIGHT)/TRANSFORM_HEIGHT;
		
		if(selId < m_animBuilder.transformationsCount()){
			notifyTransformationSelected(selId);
			return true;
		}
		
		return false;
	}
	
	private boolean selectKeyframe(int x, int y){
		
		int selectedTransform = -1;
		
		for(int i = 0 ; i < m_animBuilder.transformationsCount() ; i++){
			
			for(Animation.KeyFrame<FlameTransformation> k : m_animBuilder.getTransformation(i).keyFrames()){
				if(Math.abs(x - k.time()*m_unitLength - LABELS_PANEL_WIDTH) < KEYFRAME_RADIUS 
						&& Math.abs(y - (HEADER_HEIGHT + TRANSFORM_HEIGHT*i +TRANSFORM_HEIGHT/2)) < KEYFRAME_RADIUS){
					m_selectedKeyframeTime = k.time();
					m_selectedKeyframeTID = i;
					selectedTransform = i;
				}
			}
			
		}
		
		if(selectedTransform != -1){
			notifyTransformationSelected(selectedTransform);
			
			return true;
		}
		return false;
	}
	
	private Animation.KeyFrame<FlameTransformation> getKeyframeForTime(Animation.Builder<FlameTransformation> tr, int time){
		
		Animation.KeyFrame<FlameTransformation> key = null;
		for(Animation.KeyFrame<FlameTransformation> k : tr.keyFrames()){
			if(k.time() == time){
				key = k;
				break;
			}
		}
		return key;
	}
	
	private void notifyTransformationSelected(int id){
		for(Listener l : m_listeners){
			l.onTransformationSelected(id);
		}
	}
	
	public void deleteKeyframe(){
		if(m_selectedKeyframeTime != -1 && m_selectedKeyframeTID != -1){
			
			Animation.Builder<FlameTransformation> transfoBuilder = 
					new Animation.Builder<FlameTransformation>(m_animBuilder.getTransformation(m_selectedKeyframeTID));
			
			if(transfoBuilder.getKeyframesCount() <= 1) return;
			
			transfoBuilder.removeKeyAtTime(m_selectedKeyframeTime);
			
			m_animBuilder.setTransformation(m_selectedKeyframeTID, transfoBuilder.build());
			
			m_selectedKeyframeTime = -1;
			m_selectedKeyframeTID  = -1;
		}
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
		public void onPaletteSelected();
	}
}
