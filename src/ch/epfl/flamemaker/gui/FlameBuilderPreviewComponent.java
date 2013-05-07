package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import sun.awt.image.ToolkitImage;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.concurrent.Flame;
import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder;
import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder.Listener;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Ce component dessine la fractale définie par les paramètres du GUI
 */
@SuppressWarnings("serial")
public class FlameBuilderPreviewComponent extends JComponent implements Listener{
	
	// Palette de couleur avec laquelle dessiner la fractale
	private Palette m_palette;
	
	// Couleur de fond
	private Color m_bgColor;
	
	// Constructeur de la fractale à dessiner
	private ObservableFlameBuilder m_builder;
	
	// Cadre de la fractale à dessiner
	private Rectangle m_frame;
	
	// Densité du dessin
	private int m_density;
	
	private Flame m_flame;
	
	private FlameAccumulator m_accu;
	
	private BufferedImage m_image;
	
	private int m_lastHeight = 0,
				m_lastWidth = 0;

	/**
	 * Constructeur, initialise les arguments.
	 * @param builder Constructeur de la fractale à dessiner
	 * @param backgroundColor Couleur de fond
	 * @param palette Palette de couleur avec laquelle dessiner la fractale
	 * @param frame Cadre de la fractale à dessiner
	 * @param density Densité du dessin
	 */
	public FlameBuilderPreviewComponent(
			ObservableFlameBuilder builder,
			Color backgroundColor,
			Palette palette,
			Rectangle frame,
			int density){
		
		m_bgColor = backgroundColor;
		m_palette = palette;
		m_builder = builder;
		m_frame = frame;
		m_density = density;
		
		m_builder.addListener(this);
	}

	
	/**
	 * Méthode appellée pour rafraichir le dessin
	 */
	@Override
	protected void paintComponent(final Graphics g){
		
		if(m_accu != null && m_lastHeight == this.getHeight() && m_lastWidth == this.getWidth()){
			m_image = new BufferedImage(m_accu.width(), m_accu.height(), BufferedImage.TYPE_INT_RGB);
			
			for(int x = 0 ; x < m_accu.width() ; x++){
				for(int y = 0 ; y < m_accu.height() ; y++){
					// On met à jour la couleur du pixel courant
					m_image.setRGB(x, m_accu.height() - y -1, m_accu.color(m_palette, m_bgColor, x, y).asPackedRGB());
				}
			}
			
			//Et on dessine l'image sur l'objet de type Graphics passé en paramètre
			g.drawImage(m_image, 0, 0, null);
		} else {
			recompute();
			g.drawImage(m_image, 0, 0, this.getWidth(), this.getHeight(), null);

		}
	}
	
	private void recompute(){
		if(m_flame != null){
			m_flame.destroy();
			m_flame = null;
		}
		
		// On peut maintenant calculer la fractale avec les paramètres de taille
		m_flame = m_builder.build();
		m_flame.addListener(new Flame.Listener() {
			
			@Override
			public void onComputeProgress(int percent) {
				System.out.println("progress");
			}
			
			@Override
			public void onComputeDone(FlameAccumulator accumulator) {
				System.out.println("Rendering");
				m_accu = accumulator;
				repaint();
			}
		});
	
		Rectangle realFrame = m_frame.expandToAspectRatio((double)this.getWidth()/this.getHeight());
		
		m_lastHeight = this.getHeight();
		m_lastWidth = this.getWidth();
		
		m_flame.compute(realFrame, this.getWidth(), this.getHeight(), m_density);
	}
	
	/**
	 * Retourne la taille préférée (par défaut : 200x100)
	 */
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(200, 100);
	}


	@Override
	public void onFlameBuilderChange(ObservableFlameBuilder b) {
		recompute();
	}
}
