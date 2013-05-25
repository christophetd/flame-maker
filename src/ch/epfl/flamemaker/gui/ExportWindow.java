/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameSet;

@SuppressWarnings("serial")
public class ExportWindow extends JFrame implements Flame.Listener {

	private static final int MAX_DENSITY_VALUE = 1000;
	
	final private JProgressBar m_progressBar;
	final private JButton m_cancelButton;
	final private JButton m_exportButton;
	 private File m_fileToSave;
	private String m_extension;
	final private Color m_bgColor;
	final private Palette m_palette;
	
	final static public String[] AVAILABLE_FORMATS = new String[] {
		"jpg", 
		"png", 
		"bmp", 
		"gif"
	};

	private long m_beginComputeTime;
	
	public ExportWindow(final FlameSet set) {
		m_palette = set.getPalette();
		m_bgColor = set.getBackgroundColor();

		setPreferredSize(new Dimension(400, 200));
		setTitle("Exporter la fractale");
		setLocationRelativeTo(null);
		setVisible(true);

		final Container contentPane = getContentPane();
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		JPanel formatPanel = new JPanel();
		formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.LINE_AXIS));
		final JComboBox formatsList = new JComboBox(ExportWindow.AVAILABLE_FORMATS);
		formatsList.setMaximumSize(new Dimension(1000, formatsList.getPreferredSize().height));
		formatPanel.add(new JLabel("Format : "));
		formatPanel.add(formatsList);
		
		JPanel dimensionPanel = new JPanel();
		dimensionPanel.setLayout(new BoxLayout(dimensionPanel, BoxLayout.LINE_AXIS));
		dimensionPanel.add(new JLabel("Dimensions (px) : "));
		final JFormattedTextField widthField = buildFormattedTextField();
		widthField.setMaximumSize(new Dimension(1000, widthField.getPreferredSize().height));
		final JFormattedTextField heightField = buildFormattedTextField();
		heightField.setText("");
		heightField.setMaximumSize(new Dimension(1000, heightField.getPreferredSize().height));
		dimensionPanel.add(widthField);
		dimensionPanel.add(new JLabel(" x "));
		dimensionPanel.add(heightField);
		
		JPanel densityPanel = new JPanel();
		densityPanel.setLayout(new BoxLayout(densityPanel, BoxLayout.LINE_AXIS));
		JLabel densityLabel = new JLabel("Densité (détails de l'image, "+MAX_DENSITY_VALUE+" maximum) : ");
		final JFormattedTextField densityField = buildFormattedTextField();
		densityField.setMaximumSize(new Dimension(1000, densityField.getPreferredSize().height));
		densityField.setValue(50);
		densityField.setToolTipText("Si vous spécifiez une densité de plus de 50, le temps de rendu risque d'être plus long");
		densityPanel.add(densityLabel);
		densityPanel.add(densityField);
		
		
		JPanel exportPanel = new JPanel();
		exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.LINE_AXIS));
		m_exportButton = new JButton("Exporter");
		m_cancelButton = new JButton("Annuler");
		m_cancelButton.setEnabled(false);
		exportPanel.add(m_exportButton);
		exportPanel.add(m_cancelButton);
		
		m_progressBar = new JProgressBar(0, 100);
		m_progressBar.setEnabled(false);
		m_progressBar.setStringPainted(true);
		
		// Pour que l'on puisse accéder à la fenêtre depuis les classes anonymes qui suivent
		final Flame.Listener flameListener = this;
		final JFrame window = this;
		
		m_exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(((Number)densityField.getValue()).intValue() > MAX_DENSITY_VALUE) {
					densityField.setValue(MAX_DENSITY_VALUE);
				}
				
				m_extension = formatsList.getSelectedItem().toString();
				
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
				fileChooser.setFileFilter(new FlameFileFilter(m_extension, "Fichiers image"));
				if(fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					
					if(!filePath.endsWith("."+m_extension)) {
						filePath = filePath.concat("."+m_extension);
					}
					final String path = filePath;
					
					m_fileToSave = new File(path);
					
					if(m_fileToSave.exists()) {
						int confirmValue = JOptionPane.showConfirmDialog(window, "Ce fichier existe déjà. Voulez-vous l'écraser ?", "Le fichier existe déjà", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(confirmValue != JOptionPane.OK_OPTION) {
							return;
						}
					}
					
					m_progressBar.setString("0 %");
					
					final Flame flame = set.getBuilder().build();
					flame.addListener(flameListener);
					
					m_cancelButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flame.removeListener(flameListener);
							flame.abort();
							m_progressBar.setValue(0);
							m_progressBar.setEnabled(false);
							m_progressBar.setString("Annulé");
							m_cancelButton.setEnabled(false);
							m_exportButton.setEnabled(true);
						}
					});
					
					int width = ((Number) widthField.getValue()).intValue();
					int height = ((Number)heightField.getValue()).intValue();
					
					
					
					flame.compute(set.getFrame().toRectangle().expandToAspectRatio((double)width/height)
							, width, height, ((Number)densityField.getValue()).intValue());
				}
			}
		});
		
		contentPane.add(formatPanel);
		contentPane.add(dimensionPanel);
		contentPane.add(densityPanel);
		contentPane.add(exportPanel);
		contentPane.add(m_progressBar);
		
		pack();
	}

	@Override
	public void onComputeProgress(int percent) {
		System.out.println(percent);
		if(!m_progressBar.isEnabled() && !m_cancelButton.isEnabled()) {
			m_progressBar.setEnabled(true);
			m_cancelButton.setEnabled(true);
			m_exportButton.setEnabled(false);
			m_beginComputeTime = System.currentTimeMillis();
		}
		else {
			long timeElapsed = System.currentTimeMillis()-m_beginComputeTime; 
			long remaining = (timeElapsed*100)/percent - timeElapsed + 1000;

			String formattedRemaining = String.format("environ %d minutes et %d secondes restantes", 
				    TimeUnit.MILLISECONDS.toMinutes(remaining),
				    TimeUnit.MILLISECONDS.toSeconds(remaining) - 
				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remaining))
				);
			m_progressBar.setValue(percent);
			m_progressBar.setString(percent+" % ("+formattedRemaining+")");	
		}
		
	}
	
	@Override
	public void onComputeDone(FlameAccumulator accumulator) {
		m_progressBar.setString("Génération de l'image");
		BufferedImage tmpImage = new BufferedImage(accumulator.width(), accumulator.height(), BufferedImage.TYPE_INT_RGB);
		for(int x = 0 ; x < accumulator.width() ; x++){
			for(int y = 0 ; y < accumulator.height() ; y++){
				// On met à jour la couleur du pixel courant
				tmpImage.setRGB(x, accumulator.height() - y -1, accumulator.color(m_palette, m_bgColor, x, y).asPackedRGB());
			}
		}
		try {
			ImageIO.write(tmpImage, m_extension, m_fileToSave);
			
			if(Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(m_fileToSave);
			}
		} catch(IOException ioex) {
			ioex.printStackTrace();
		}
		
		m_progressBar.setValue(100);
		m_progressBar.setString("Terminé");
		m_progressBar.setEnabled(false);
		m_cancelButton.setEnabled(false);
		m_exportButton.setEnabled(true);
	}
	
	
	private JFormattedTextField buildFormattedTextField(){
		final JFormattedTextField field = new JFormattedTextField(new DecimalFormat("####"));
		field.setValue(500);
		field.setColumns(5);
		
		return field;
	}

}
