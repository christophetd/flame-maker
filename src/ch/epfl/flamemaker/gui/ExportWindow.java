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
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ch.epfl.flamemaker.file.FlameFileFilter;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameSet;
import ch.epfl.flamemaker.flame.FlameUtils;

@SuppressWarnings("serial")
public class ExportWindow extends JFrame implements Flame.Listener {

	/**
	 *	La densité maximale que l'utilisateur peut renseigner. 
	 */
	private static final int MAX_DENSITY_VALUE = 1000;
	
	/**
	 * La taille maximale que l'utilisateur peut renseigner.
	 */
	private static final int MAX_SIZE = 99999;
	
	/**
	 * Les formats d'exportation disponibles.
	 * Ceux-ci sont disponibles quelle que soit la plateforme 
	 * d'exécution. 
	 */
	final static public String[] AVAILABLE_FORMATS = new String[] {
		"jpg", 
		"png", 
		"bmp", 
		"gif"
	};

	
	/**
	 * La barre de progression et les boutons d'exportation et d'annulation.
	 * Tous trois sont stockés dans des attributs afin d'être accessibles depuis
	 * plusieurs méthodes.
	 */
	final private JProgressBar m_progressBar;
	final private JButton m_cancelButton;
	final private JButton m_exportButton;
	
	/**
	 *	L'extension dans laquelle réaliser l'exportation 
	 */
	private String m_extension;
	
	/**
	 *	Le FlameSet (contenant entre autres la couleur de fond et la
	 *	palette) à utiliser pour l'exportation 
	 */
	final private FlameSet m_flameSet;
	
	/**
	 *	Le fichier dans lequel exporter la fractale 
	 */
	private File m_fileToSave;
	
	/**
	 *	L'instant auquel a débuté le calcul de la fractale.
	 *	Utilisé pour estimer l'avancement de la progression. 
	 */
	private long m_beginComputeTime;
	
	public ExportWindow(final FlameSet set) {
		m_flameSet = set;

		setPreferredSize(new Dimension(400, 200));
		setTitle("Exporter la fractale");
		setLocationRelativeTo(null);
		setVisible(true);

		final Container contentPane = getContentPane();
		
		/*
		 * La fenêtre a un BoxLayout dirigé vers le bas. Chaque ligne de composants
		 * est un JPanel avec un BoxLayout dirigé vers la droite.
		 */
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		// Panel du choix de format
		JPanel formatPanel = new JPanel();
		formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.LINE_AXIS));
		final JComboBox formatsList = new JComboBox(ExportWindow.AVAILABLE_FORMATS);
		formatsList.setMaximumSize(new Dimension(1000, formatsList.getPreferredSize().height));
		formatPanel.add(new JLabel("Format : "));
		formatPanel.add(formatsList);
		
		// Panel de la dimension
		JPanel dimensionPanel = new JPanel();
		dimensionPanel.setLayout(new BoxLayout(dimensionPanel, BoxLayout.LINE_AXIS));
		dimensionPanel.add(new JLabel("Dimensions (px) : "));
		final JFormattedTextField widthField = buildFormattedTextField();
		widthField.setInputVerifier(new MaxIntInputVerifier(MAX_SIZE));
		widthField.setMaximumSize(new Dimension(60, widthField.getPreferredSize().height));
		final JFormattedTextField heightField = buildFormattedTextField();
		heightField.setMaximumSize(new Dimension(60, heightField.getPreferredSize().height));
		heightField.setInputVerifier(new MaxIntInputVerifier(MAX_SIZE));
		dimensionPanel.add(widthField);
		dimensionPanel.add(new JLabel(" x "));
		dimensionPanel.add(heightField);
		
		// Panel du choix de la densité
		JPanel densityPanel = new JPanel();
		densityPanel.setLayout(new BoxLayout(densityPanel, BoxLayout.LINE_AXIS));
		JLabel densityLabel = new JLabel("Densité :");
		final JFormattedTextField densityField = buildFormattedTextField();
		densityField.setInputVerifier(new MaxIntInputVerifier(MAX_DENSITY_VALUE));
		densityField.setMaximumSize(new Dimension(50, densityField.getPreferredSize().height));
		densityField.setValue(50);
		densityField.setToolTipText("Si vous spécifiez une densité de plus de 50, le temps de rendu sera plus long");
		densityPanel.add(densityLabel);
		densityPanel.add(densityField);
		
		// Panel contenant les boutons d'exportation et d'annulations
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
		
		// Au clic du bouton d'exportation
		m_exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				// Si la densité indiquée est trop grande, on la réduit
				if(((Number)densityField.getValue()).intValue() > MAX_DENSITY_VALUE) {
					densityField.setValue(MAX_DENSITY_VALUE);
				}
				
				// On récupère l'extension
				m_extension = formatsList.getSelectedItem().toString();
				
				// On affiche un fileChooser pour l'extension choisie
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
				fileChooser.setFileFilter(new FlameFileFilter(m_extension, "Fichiers image"));
				if(fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
					// On récupère le fichier choisi
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					
					// Si l'extension n'a pas été spécifiée, on la rajoute automatiquement
					if(!filePath.endsWith("."+m_extension)) {
						filePath = filePath.concat("."+m_extension);
					}
										
					m_fileToSave = new File(filePath);
					
					// Si le fichier existe déjà, on demande confirmation avant de l'écraser
					if(m_fileToSave.exists()) {
						int confirmValue = JOptionPane.showConfirmDialog(window, "Ce fichier existe déjà. Voulez-vous l'écraser ?", "Le fichier existe déjà", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(confirmValue != JOptionPane.OK_OPTION) {
							return;
						}
					}
					
					m_progressBar.setString("0 %");
					
					// On construit l'objet Flame et on s'enregistre comme 
					// observateur, pour être notifié de l'avancement du calcul
					final Flame flame = set.getBuilder().build();
					flame.addListener(flameListener);
					
					// Au clic du bouton d'annulation, on annule l'exportation
					m_cancelButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							flame.destroy();
							
							/*
							 * On désactive la barre de progression et le bouton 
							 * d'annulation, et on active le bouton d'exportation
							 */
							m_progressBar.setEnabled(false);
							m_cancelButton.setEnabled(false);
							m_exportButton.setEnabled(true);
							m_progressBar.setString("Annulé");
						}
					});
					
					// On récupère la largeur et la hauteur d'exportation spécifiées
					int width = ((Number) widthField.getValue()).intValue();
					int height = ((Number)heightField.getValue()).intValue();

					// Et on lance le calcul de la fractale
					flame.compute(set.getFrame().toRectangle().expandToAspectRatio((double)width/height)
							, width, height, ((Number)densityField.getValue()).intValue());
				}
			}
		});
		
		// On ajoute les différents panels à la fenêtre
		contentPane.add(formatPanel);
		contentPane.add(dimensionPanel);
		contentPane.add(densityPanel);
		contentPane.add(exportPanel);
		contentPane.add(m_progressBar);
		
		pack();
	}

	/* (non-Javadoc)
	 * @see ch.epfl.flamemaker.flame.Flame.Listener#onComputeProgress(int)
	 */
	@Override
	public void onComputeProgress(int percent) {
		/*
		 * Si le rendu n'a pas encore commencé, on active la barre de
		 * progression et le bouton d'annulation, et on désactive le
		 * bouton d'exportation. On définit aussi le temps auquel a débuté
		 * le calcul.
		 */
		if(!m_progressBar.isEnabled() && !m_cancelButton.isEnabled()) {
			m_progressBar.setEnabled(true);
			m_cancelButton.setEnabled(true);
			m_exportButton.setEnabled(false);
			m_beginComputeTime = System.currentTimeMillis();
		}
		else {
			// On calcul combien de temps s'est écoulé, et on estime le temps restant
			long timeElapsed = System.currentTimeMillis()-m_beginComputeTime; 
			long remaining = (timeElapsed*100)/percent - timeElapsed + 1000;
			
			long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
			String minutesRemainingFormatted = 
					remainingMinutes == 0 
				? 	"" 
				:	String.format("%d minutes et ", remainingMinutes);
					
			String formattedRemaining = String.format("environ "+minutesRemainingFormatted+"%d secondes restantes", 
				    TimeUnit.MILLISECONDS.toSeconds(remaining) - 
				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remaining))
				);
			m_progressBar.setValue(percent);
			m_progressBar.setString(percent+" % ("+formattedRemaining+")");	
		}
		
	}
	
	/*
	 * voir Flame.Listener#onComputeDone
	 */
	@Override
	public void onComputeDone(FlameAccumulator accumulator) {
		/*
		 * Quand la fractale a été calculée, il reste encore à générer la
		 * BufferedImage et à l'écrire dans le fichier.
		 */
		m_progressBar.setString("Génération de l'image");
		BufferedImage tmpImage = FlameUtils.generateBufferedImage(accumulator, m_flameSet);
		try {
			ImageIO.write(tmpImage, m_extension, m_fileToSave);
			
			/*
			 * Si l'environnement d'exécution le supporte, on ouvre le fichier image 
			 * nouvellement créé à l'aide du programme par défaut. 
			 */
			if(Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(m_fileToSave);
			}
		} catch(IOException ioex) {
			ioex.printStackTrace();
		}
		
		/*
		 * L'exportation est terminée ; on met la barre de progression à 100% en
		 * la désactivant au passage, on désactive le bouton d'annulation
		 * et active le bouton d'exportation.
		 */
		m_progressBar.setValue(100);
		m_progressBar.setString("Terminé");
		m_progressBar.setEnabled(false);
		m_cancelButton.setEnabled(false);
		m_exportButton.setEnabled(true);
	}
	
	// TODO : javadoc
	@Override
	public void onComputeError(String msg) {
		JOptionPane.showMessageDialog(null,
			    "La fractale ne peut pas être calculée avec ces paramètres. \n" +
			    "Essayez une taille ou une densité plus petite où choisissez une autre méthode de calcul (menu calcul)\n\n" +
			    "Informations sur l'erreur : \n"+msg,
			    "Erreur de calcul",
			    JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Construit un champ de texte formatté. Evite la duplication de code.
	 * @return Le champ de texte formatté
	 */
	private JFormattedTextField buildFormattedTextField(){
		final JFormattedTextField field = new JFormattedTextField(new DecimalFormat("######"));
		field.setValue(500);
		field.setColumns(5);
		
		return field;
	}
	
	/**
	 *	Classe modélisant un vérificateur pour les JFormattedTextField dont
	 *	la valeur doit être un entier strictement positif.
	 */
	private class MaxIntInputVerifier extends InputVerifier {
		private int max;
		
		public MaxIntInputVerifier(int maxValue){
			this.max = maxValue;
		}
		
		@Override
		public boolean verify(JComponent input) {
			JFormattedTextField tf = (JFormattedTextField) input;

			try {
				String text = tf.getText();

				AbstractFormatter formatter = tf.getFormatter();

				Number value = (Number) formatter.stringToValue(text);

				/*
				 * On n'utilise pas setText, mais setValue à la place car
				 * setText pose des problèmes puisque swing a la riche idée de
				 * traduire les nombres (remplacer les "." par des ",")... Le
				 * champ est valide si la valeur est strictement positive
				 */
				if (value.doubleValue() <= 0){
					tf.setValue(tf.getValue());
				} else if (value.doubleValue() > max) {
					tf.setValue(max);
				} else {
					tf.setValue(value);
				}

			} catch (ParseException e) {
				/*
				 * On n'a rien à gérer ici car le texte est automatiquement
				 * remplacé par la valeur précédente.
				 */
			}
			return true;
		}

	}
}
