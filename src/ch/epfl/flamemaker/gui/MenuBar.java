/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import ch.epfl.flamemaker.file.FlameFile;
import ch.epfl.flamemaker.file.FlameFileFilter;
import ch.epfl.flamemaker.file.InvalidFlameFileException;
import ch.epfl.flamemaker.flame.FlameFactory;
import ch.epfl.flamemaker.flame.FlameSet;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.Presets;

/**
 * Classe modélisant la barre de menus de la fenêtre principale du programme.
 */
public class MenuBar {

	/**
	 * Le chemin actuel du fichier ouvert
	 */
	private static String currentFilePath = null;

	/**
	 * Génère statiquement la barre de menus dans la JFrame passée en paramètre
	 * 
	 * @param window
	 *            La JFrame dans laquelle construire la barre de menus (en
	 *            l'occurrence, la fenêtre principale du GUI)
	 * @param set
	 *            Le FlameSet contenant les informations de la fractale
	 * @param transformationsListModel
	 *            Le modèle de la liste des transformations
	 */
	public static JMenuBar build(final JFrame window, final FlameSet set,
			final TransformationsListModel transformationsListModel) {
		
		final JMenuBar menuBar = new JMenuBar();

		/* Menu 'Fichier' */
		final JMenu fileMenu = new JMenu("Fichier");
		final JMenuItem openMenuItem = new JMenuItem("Ouvrir");
		final JMenuItem newMenuItem = new JMenuItem("Nouveau");
		final JMenu newFromMenuItem = new JMenu("Nouveau à partir d'un modèle");
		final JMenuItem saveMenuItem = new JMenuItem("Enregistrer");
		final JMenuItem saveAsMenuItem = new JMenuItem("Enregistrer sous");
		final JMenuItem exportMenuItem = new JMenuItem("Exporter");
		final JMenuItem closeMenuItem = new JMenuItem("Quitter");

		// On remplit le sous-menu de "Nouveau à partir d'un modèle" avec les
		// fractales pré-définies existantes
		for (final Presets p : Presets.values()) {
			JMenuItem item = new JMenuItem(p.displayableName());
			newFromMenuItem.add(item);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					MenuBar.setCurrentFilePath(null);
					set.loadPreset(p);
				}
			});
		}

		fileMenu.add(openMenuItem);
		fileMenu.add(newMenuItem);
		fileMenu.add(newFromMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(exportMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);

		/* Menu 'Aide' */
		JMenu helpMenu = new JMenu("Aide");
		JMenuItem documentationMenuItem = new JMenuItem("Documentation");
		documentationMenuItem.setEnabled(false); // Pas de documentation disponible actuellement

		JMenuItem aboutMenuItem = new JMenuItem("À propos");

		helpMenu.add(documentationMenuItem);
		helpMenu.add(aboutMenuItem);

		/* Menu 'calcul'. Permet de choisir la technique utilisée pour rendre la fractale */
		JMenu computeMenu = new JMenu("Calcul");
		ButtonGroup computeBG = new ButtonGroup();

		// On parcourt toutes les stratégies existantes, désactive celle(s) non
		// supportée(s) et active par défaut la première supportée que l'on
		// trouve
		boolean hasSelectedStrategy = false;
		for (FlameFactory fs : FlameFactory.ALL_FACTORIES) {
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(fs.name());
			if (!fs.isSupported())
				item.setEnabled(false);
			else if (!hasSelectedStrategy) {
				item.setSelected(true);
			}

			computeBG.add(item);
			computeMenu.add(item);

			final FlameFactory strategy = fs;
			// Lorsque cette technique de rendu est sélectionnée, on l'active
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					set.getBuilder().getComputeStrategy().disable();
					strategy.enable();
					set.getBuilder().setComputeStrategy(strategy);
				}

			});
		}

		// On ajoute finalement tous les menus définis dans la barre de menus
		menuBar.add(fileMenu);
		menuBar.add(computeMenu);
		menuBar.add(helpMenu);

		/* Raccourcis clavier */
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				KeyEvent.CTRL_DOWN_MASK));
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				KeyEvent.CTRL_DOWN_MASK));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_DOWN_MASK));
		exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				KeyEvent.CTRL_DOWN_MASK));
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
				KeyEvent.CTRL_DOWN_MASK));

		/* Comportements */

		// Lorsque l'utilisateur sélectionne le menu "Fichir > Nouveau", on remplace la
		// fractale existante par une nouvelle, vide
		newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				set.loadPreset(Presets.EMPTY_PRESET);
			}

		});
		
		// Affiche quelques informations à propos du programme
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								window,
								"Ce programme a été développé par Hadrien Milano <hadrien.milano@epfl.ch> et Christophe Tafani-Dereeper <christophe.tafani-dereeper@epfl.ch> dans le cadre d'un projet de semestre");
			}
		});
		
		// Fonctionnalité d'ouverture d'un fichier de fractale flame
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// On tente de récupérer le répertoire utilisateur par défaut
				String userHome = System.getProperty("user.home");
				if (userHome == null) {
					userHome = "";
				}

				JFileChooser fileChooser = new JFileChooser(new File(userHome));
				fileChooser.addChoosableFileFilter(new FlameFileFilter(
						FlameFile.FLAME_FILE_EXTENSION, "Fichier de fractale Flame"));

				// Lorsque l'utilisateur a choisi un fichier à ouvrir
				if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile()
							.getAbsolutePath();
					try {
						ArrayList<FlameTransformation> newTransformations = FlameFile
								.getTransformationsFromFile(filePath);

						// Une fois la liste des transformations récupérée, on supprime
						// celle de la fractale actuelle et la remplace par la nouvelle
						int size = transformationsListModel.getSize();
						for (int i = size - 1; i >= 0; i--) {
							transformationsListModel.removeTransformation(i);
						}
						for (int i = 0; i < newTransformations.size(); i++) {
							transformationsListModel
									.addTransformation(newTransformations
									.get(i));
						}
						currentFilePath = filePath;
						window.setTitle("FlameMaker - " + filePath);
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(window,
								"Le fichier est introuvable",
								"Erreur lors de l'ouverture",
								JOptionPane.ERROR_MESSAGE);
					} catch (InvalidFlameFileException fex) {
						JOptionPane
								.showMessageDialog(
										window,
										"Le fichier n'a pas pu être ouvert dans FlameMaker, sûrement parce qu'il est corrompu.",
										"Erreur lors de l'ouverture",
										JOptionPane.ERROR_MESSAGE);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch(IOException ioex) {
						ioex.printStackTrace();
					}
				}
			}
		});

		/*
		 * Fonctionnalité de sauvegarde dans un fichier .flame
		 * Si le ficher actuel est défini (par ouverture ou un précédent appel
		 * à "enregistrer"/"enregistrer sous", la fractale est enregistrée dedans
		 * Sinon, le même comportement que la fonction "Enregistrer sous" est effectué, 
		 * pour que l'utilisateur puisse choisir dans quel fichier enregistrer sa fractale 
		*/
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFilePath != null) {
					try {
						FlameFile.saveToFile(set.getBuilder(), currentFilePath);
					}
					catch(FileNotFoundException ex) {
						// Exception lancée si le fichier ne peut être créé, par exemple
						showSaveErrorDialog(window);
					}
					catch(IOException ioex) {
						ioex.printStackTrace();
					}
				} else {
					saveAsMenuItem.doClick();
				}
			}
		});

		// Fonctionnalité "Enregistrer sous"
		saveAsMenuItem.addActionListener(new MenuBar.SaveAsActionListener(window, set.getBuilder()));

		// Fonctionnalité "exporter"
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ExportWindow(set);
			}
		});

		// Element "quitter"
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirmValue = JOptionPane
						.showConfirmDialog(window,
								"Voulez-vous vraiment quitter ?", "Quitter",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (confirmValue == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
		
		return menuBar;

	}

	/**
	 * La classe implémentant la fonctionnalité "Enregistrer sous".
	 * Notons que cela n'a pas été fait dans une classe anonyme comme
	 * pour les autres fonctionnalités afin que cette classe puisse être
	 * utilisée depuis deux endroits différents du code
	 */
	private static class SaveAsActionListener implements ActionListener {

		/**
		 * La fenêtre principale du GUI
		 * (utilisée pour spécifier le parent du FileChooser et
		 * pouvoir modifier le titre de la fenêtre lorsque le fichier
		 * a été enregistré)
		 */
		final private JFrame window;
		
		/**
		 *	Le constructeur de la fractale 
		 */
		final private ObservableFlameBuilder flameBuilder;

		/**
		 * Le constructeur de la classe
		 * @param window		La fenêtre principale du GUI
		 * @param flameBuilder	Le constructeur de la fractale
		 */
		public SaveAsActionListener(JFrame window,
				ObservableFlameBuilder flameBuilder) {
			this.window = window;
			this.flameBuilder = flameBuilder;
		}

		public void actionPerformed(ActionEvent e) {
			// On tente de récupérer le répertoire utilisateur par défaut
			String userHome = System.getProperty("user.home");
			if (userHome == null) {
				userHome = "";
			}
			
			JFileChooser fileChooser = new JFileChooser(new File(userHome));
			fileChooser.addChoosableFileFilter(new FlameFileFilter(FlameFile.FLAME_FILE_EXTENSION,
					"Fichier de fractale Flame"));

			// Lorsque l'utilisateur a choisi le fichier dans lequel enregistrer
			// sa fractale
			if (fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
				String filePath = fileChooser.getSelectedFile()
						.getAbsolutePath();

				// S'il n'a pas indiqué l'extension dans le nom, on la rajoute
				if (!filePath.endsWith(FlameFile.FLAME_FILE_EXTENSION)) {
					filePath = filePath.concat(FlameFile.FLAME_FILE_EXTENSION);
				}

				// Si le fichier existe déjà, on demande une confirmation
				if ((new File(filePath).exists())) {
					int confirmValue = JOptionPane.showConfirmDialog(window,
							"Ce fichier existe déjà. Voulez-vous l'écraser ?",
							"Le fichier existe déjà",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (confirmValue != JOptionPane.OK_OPTION) {
						return;
					}
				}
				currentFilePath = filePath;
				window.setTitle("FlameMaker - " + filePath);
				
				try {
					FlameFile.saveToFile(flameBuilder, filePath);
				}
				catch (FileNotFoundException e1) {
					// Cette exception est lancée si le fichier ne peut pas être créé, par exemple
					showSaveErrorDialog(window);
				} 
				catch(IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private static void showSaveErrorDialog(JFrame window) {
		JOptionPane
		.showMessageDialog(
				window,
				"La fractale n'a pas pu être enregistrée. Vérifiez que vous disposez des droits nécessaires à la création du fichier",
				"Erreur lors de l'enregistrement",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public static void setCurrentFilePath(String filepath) {
		currentFilePath = filepath;
	}

}
