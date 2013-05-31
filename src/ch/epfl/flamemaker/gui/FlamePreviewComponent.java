/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import ch.epfl.flamemaker.FlameSet;
import ch.epfl.flamemaker.anim.CacheManager;
import ch.epfl.flamemaker.anim.FlameAnimation;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameUtils;
import ch.epfl.flamemaker.geometry2d.ObservableRectangle;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Ce component dessine la fractale définie par les paramètres du GUI
 */
@SuppressWarnings("serial")
public class FlamePreviewComponent extends JComponent {
	
	static public final double ZOOM_FACTOR = 1.1;
	
	private Rectangle m_lastFrame;
	
	// Cadre redimentionné pour atteindre le ratio du composant
	private Rectangle m_realFrame;
	
	private Rectangle m_drawingRect;
	
	private boolean m_dragging;
	private boolean m_displayProgress;
	
	private Integer m_progress = -1;
	
	private FlameSet m_set;
	
	private boolean m_preventRecompute;
	
	private Flame m_flame;
	
	private Integer m_time;
	
	// Keeps track of the time at which last compute was started
	private int m_lastTime;
	
	private BufferedImage m_image;
	
	private final CacheManager m_cache;
	
	private int m_lastHeight = 0,
				m_lastWidth = 0;
	
	// Coordonnées du mousePressed
	private int m_mouseX;
	private int m_mouseY;

	/**
	 * Constructeur, initialise les arguments.
	 * @param set ensemble des propriétés de la fractale à dessiner
	 */
	public FlamePreviewComponent(FlameSet set, CacheManager cache){
		
		m_cache = cache;
		m_set = set;
		m_time = 0;
		
		m_lastFrame = set.getFrame().toRectangle();
		
		set.getBuilder().addListener(new FlameAnimation.Builder.Listener(){

			@Override
			public void onFlameBuilderChange(FlameAnimation.Builder b) {
				m_cache.clear();
				recompute();
			}
			
		});
		
		// Observateur pour les movements de la souris 
		addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent evt) {
				m_set.getFrame().setCenter(new Point(
						m_set.getFrame().center().x() + (m_mouseX - evt.getX())*m_realFrame.width()/getWidth(),
						m_set.getFrame().center().y() - (m_mouseY - evt.getY())*m_realFrame.height()/getHeight()
				));
				
				m_mouseX = evt.getX();
				m_mouseY = evt.getY();
			}


			@Override
			public void mouseMoved(MouseEvent arg0) {}
			
		});
		
		
		// Observateur pour les évènements de la souris (autres que le mouvement et la roulette)
		addMouseListener(new MouseListener(){


			// Les trois méthodes suivantes ne nous intéressent pas
			@Override
			public void mouseClicked(MouseEvent arg0) {}
			@Override
			public void mouseEntered(MouseEvent arg0) {}
			@Override
			public void mouseExited(MouseEvent arg0) {}


			@Override
			public void mousePressed(MouseEvent evt) {
				m_mouseX = evt.getX();
				m_mouseY = evt.getY();
				m_preventRecompute = true;
				m_dragging = true;
				
				if(m_flame != null){
					m_flame.destroy();
					m_flame = null;
				}
			}


			@Override
			public void mouseReleased(MouseEvent evt) {
				m_preventRecompute = false;
				
				m_set.getFrame().setCenter(new Point(
						m_set.getFrame().center().x() + (m_mouseX - evt.getX())*m_realFrame.width()/getWidth(),
						m_set.getFrame().center().y() - (m_mouseY - evt.getY())*m_realFrame.height()/getHeight()
				));
			}
			
		});
		
		//Observateur pour la roulette de la souris
		addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent evt) {
				if(evt.getWheelRotation() == 0) return;
				
				double factor = (evt.getWheelRotation() < 0) ? 1.0/ZOOM_FACTOR : ZOOM_FACTOR;
				
				m_dragging = true;
				m_set.getFrame().setSize(m_set.getFrame().width()*factor, m_set.getFrame().height()*factor);
			}
			
		});
		
		this.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		
		set.getFrame().addListener(new ObservableRectangle.Listener(){

			@Override
			public void onRectangleChange(ObservableRectangle rect) {
				repaint();
			}
			
		});
	}

	
	/**
	 * Méthode appellée pour rafraichir le dessin. Si le dessin stocké n'est pas à jour, elle demande un nouveau 
	 * calcul avec recompute() et redimentionne l'image pour un affichage temporaire.
	 */
	@Override
	protected void paintComponent(final Graphics g){
		super.paintComponent(g);
		
		// L'utilisateur a modifié ou modifie le cadre de vue
		if(m_dragging){
			int newWidth = (int)(m_lastFrame.width()/m_set.getFrame().width()*this.getWidth());
			int newHeight = (int)(m_lastFrame.height()/m_set.getFrame().height()*this.getHeight());
			
			/* Evite d'afficher la couleur par défaut dans les zones pas couvertes par l'image */
			g.setColor(java.awt.Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			m_drawingRect = new Rectangle(new Point(
					(m_lastFrame.center().x() - m_set.getFrame().center().x())*(this.getWidth()/m_realFrame.width()) + getWidth()/2, 
					(m_set.getFrame().center().y() - m_lastFrame.center().y())*(this.getHeight()/m_realFrame.height()) + getHeight()/2),
					newWidth,
					newHeight);
			
			g.drawImage(m_image, (int)m_drawingRect.left(), (int)m_drawingRect.bottom()
					, (int)m_drawingRect.width(), (int)m_drawingRect.height(), null);
			
			// Il a fini de modifier la vue
			if(!m_preventRecompute) {
				m_dragging = false;
				m_cache.clear();
				recompute();
			}
		// le composant a été redimentionné
		} else if(m_lastHeight != this.getHeight() || m_lastWidth != this.getWidth()){
			m_cache.clear();
			recompute();
			if(m_image != null){
				
				Rectangle rect = new Rectangle(new Point(0,0), this.getWidth(), this.getHeight())
						.expandToAspectRatio((double)m_image.getWidth()/m_image.getHeight());
						
				m_drawingRect = new Rectangle(new Point(
						(this.getWidth())/2,
						(this.getHeight())/2),
						rect.width(), rect.height());

				g.drawImage(m_image, (int)m_drawingRect.left(), (int)m_drawingRect.bottom()
						, (int)m_drawingRect.width(), (int)m_drawingRect.height(), null);
			}
		} else if(m_displayProgress) {
			g.setColor(java.awt.Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			if(m_drawingRect != null){
				g.drawImage(m_image, (int)m_drawingRect.left(), (int)m_drawingRect.bottom()
						, (int)m_drawingRect.width(), (int)m_drawingRect.height(), null);
			}
			
			synchronized(m_progress){
				drawProgressBar(g);
			}
		} else if(m_image != null) {
			g.drawImage(m_image, (int)m_drawingRect.left(), (int)m_drawingRect.bottom()
					, (int)m_drawingRect.width(), (int)m_drawingRect.height(), null);
		}
		
		// Sinon, on ne fais rien (ce cas n'arrive jamais)
	}
	
	/*
	 * Dessine une barre de progression en bas de l'image
	 */
	private void drawProgressBar(Graphics g){
		g.setColor(java.awt.Color.WHITE);
		
		int barWidth = getWidth() - 10 - 50;
		int fillWidth = (m_progress == 0) ? 0 : (barWidth-4)*m_progress/100;
		int barY = getHeight() - 10 - 10;
		
		g.drawString(m_progress+"%", getWidth() - 40, barY + 10);
		g.drawRect(10, barY, barWidth, 10);
		g.fillRect(12, barY+2, fillWidth, 7);
	}
	
	/**
	 * Demande un nouveau calcul de la fractale. 
	 * Cette méthode demande à son tour un nouveau dessin du composant quand le calcul est terminé.
	 */
	private void recompute(){
		// Protege contre des calculs inutiles
		if(this.getWidth() == 0 || this.getHeight() == 0)
			return;
		
		if(m_flame != null){
			m_flame.destroy();
			m_flame = null;
		}
		
		BufferedImage image = m_cache.getFrame(m_time);
		if(image != null){
			// This creates a strong reference to the current image which prevents it from GC. Awesome !
			m_image = image;
			m_displayProgress = false; // just in case
			repaint();
			return;
		}
		
		
		m_lastTime = m_time;
		// On peut maintenant calculer la fractale avec les paramètres de taille
		m_flame = m_set.getBuilder().build().getFlame(m_time);
		m_flame.addListener(new Flame.Listener() {
			
			@Override
			public void onComputeProgress(int percent) {
				synchronized(m_progress){
					m_progress = percent;
					m_displayProgress = true;
				}
				repaint();
			}
			
			@Override
			public void onComputeDone(FlameAccumulator accumulator) {
				synchronized(m_time){
					if(m_lastTime != m_time) return; // This end of compute comes too late
					
					m_displayProgress = false;
					
					m_lastFrame = m_set.getFrame().toRectangle();
					
					m_image = FlameUtils.generateBufferedImage(accumulator, m_set);
					m_cache.setFrame(m_time, m_image);
					
					m_drawingRect = new Rectangle(new Point(getWidth()/2, getHeight()/2), getWidth(), getHeight());
					
					repaint();
				}
			}

			@Override
			public void onComputeError(String msg) {
				JOptionPane.showMessageDialog(null, 
						"Une erreur s'est produite durant le calcul de la fractale. Essayez une méthode différente (menu calcul)." +
						"\n\n Informations sur l'erreur : \n"+msg, 
						"Erreur de calcul", JOptionPane.ERROR_MESSAGE);
			}
		});
	
		m_realFrame = m_set.getFrame().expandToAspectRatio((double)this.getWidth()/this.getHeight());
		
		m_lastHeight = this.getHeight();
		m_lastWidth = this.getWidth();
		
		m_flame.compute(m_realFrame, this.getWidth(), this.getHeight(), m_set.getDensity());
	}
	
	/**
	 * Retourne la taille préférée (par défaut : 200x100)
	 */
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(200, 100);
	}
	
	public void setTime(int time){
		synchronized(m_time){
			m_time = time;
			recompute();
		}
	}
}
