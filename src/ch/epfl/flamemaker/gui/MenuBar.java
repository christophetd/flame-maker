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

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import ch.epfl.flamemaker.FlameSet;
import ch.epfl.flamemaker.SerializableFlameSet;
import ch.epfl.flamemaker.file.FlameFile;
import ch.epfl.flamemaker.file.FlameFileFilter;
import ch.epfl.flamemaker.file.InvalidFlameFileException;
import ch.epfl.flamemaker.flame.FlameFactory;
import ch.epfl.flamemaker.flame.Presets;

/**
 * Classe mod�lisant la barre de menus de la fen�tre principale du programme.
 */
public class MenuBar {

	/**
	 * Le chemin actuel du fichier ouvert
	 */
	private static String currentFilePath = null;

	/**
	 * G�n�re statiquement la barre de menus dans la JFrame pass�e en param�tre
	 * 
	 * @param window
	 *            La JFrame dans laquelle construire la barre de menus (en
	 *            l'occurrence, la fen�tre principale du GUI)
	 * @param set
	 *            Le FlameSet contenant les informations de la fractale
	 */
	public static JMenuBar build(final JFrame window, final FlameSet set) {
		
		final JMenuBar menuBar = new JMenuBar();

		/* Menu 'Fichier' */
		final JMenu fileMenu = new JMenu("Fichier");
		final JMenuItem openMenuItem = new JMenuItem("Ouvrir");
		final JMenuItem newMenuItem = new JMenuItem("Nouveau");
		final JMenu newFromMenuItem = new JMenu("Nouveau � partir d'un mod�le");
		final JMenuItem saveMenuItem = new JMenuItem("Enregistrer");
		final JMenuItem saveAsMenuItem = new JMenuItem("Enregistrer sous");
		final JMenuItem exportMenuItem = new JMenuItem("Exporter");
		final JMenuItem closeMenuItem = new JMenuItem("Quitter");

		// On remplit le sous-menu de "Nouveau � partir d'un mod�le" avec les
		// fractales pr�-d�finies existantes
		// TODO : reenable this
		/*for (final Presets p : Presets.values()) {
			JMenuItem item = new JMenuItem(p.displayableName());
			newFromMenuItem.add(item);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					MenuBar.setCurrentFilePath(null);
					set.loadPreset(p);
				}
			});
		}*/

		fileMenu.add(openMenuItem);
		fileMenu.add(newMenuItem);
		fileMenu.add(newFromMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(exportMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);
		
		/* menu qualit� */
		JMenu previewQualityMenu = new JMenu("Qualit�");
		ButtonGroup previewQualityBG = new ButtonGroup();
		
		for(double q = 2 ; q >= 0.25 ; q /= 2){
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(""+q);
			
			if(q == 1){
				item.setSelected(true);
			}
			previewQualityBG.add(item);
			previewQualityMenu.add(item);
			
			final double quality = q;
			// Lorsque cette technique de rendu est s�lectionn�e, on l'active
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					set.setQualityFactor(quality);
				}

			});
		}

		/* Menu 'Aide' */
		JMenu helpMenu = new JMenu("Aide");
		JMenuItem documentationMenuItem = new JMenuItem("Documentation");
		documentationMenuItem.setEnabled(false); // Pas de documentation disponible actuellement

		JMenuItem aboutMenuItem = new JMenuItem("� propos");

		helpMenu.add(documentationMenuItem);
		helpMenu.add(aboutMenuItem);

		/* Menu 'calcul'. Permet de choisir la technique utilis�e pour rendre la fractale */
		JMenu computeMenu = new JMenu("Calcul");
		ButtonGroup computeBG = new ButtonGroup();

		// On parcourt toutes les strat�gies existantes, d�sactive celle(s) non
		// support�e(s) et active par d�faut la premi�re support�e que l'on
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
			// Lorsque cette technique de rendu est s�lectionn�e, on l'active
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					set.getBuilder().getComputeStrategy().disable();
					strategy.enable();
					set.getBuilder().setComputeStrategy(strategy);
				}

			});
		}

		// On ajoute finalement tous les menus d�finis dans la barre de menus
		//menuBar.add(fileMenu);
		menuBar.add(computeMenu);
		menuBar.add(previewQualityMenu);
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

		// Lorsque l'utilisateur s�lectionne le menu "Fichir > Nouveau", on remplace la
		// fractale existante par une nouvelle, vide
		// TODO : re-enable this
		/*newMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				set.loadPreset(Presets.EMPTY_PRESET);
			}

		});*/
		
		// Affiche quelques informations � propos du programme
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								window,
								"Ce programme a �t� d�velopp� par Hadrien Milano <hadrien.milano@epfl.ch> et Christophe Tafani-Dereeper <christophe.tafani-dereeper@epfl.ch> dans le cadre d'un projet de semestre");
			}
		});
		
		// Fonctionnalit� d'ouverture d'un fichier de fractale flame
		openMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// On tente de r�cup�rer le r�pertoire utilisateur par d�faut
				String userHome = System.getProperty("user.home");
				if (userHome == null) {
					userHome = "";
				}

				JFileChooser fileChooser = new JFileChooser(new File(userHome));
				fileChooser.addChoosableFileFilter(new FlameFileFilter(
						FlameFile.FLAME_FILE_EXTENSION, "Fichier de fractale Flame"));

				// Lorsque l'utilisateur a choisi un fichier � ouvrir
				if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile()
							.getAbsolutePath();
					try {
						SerializableFlameSet newSet = FlameFile
								.getSerializableFlameSetFromFile(filePath);
						// TODO : re-enable this
						// set.importDataFrom(newSet);

//						set.setAll(newSet);
//						// Une fois la liste des transformations r�cup�r�e, on supprime
//						// celle de la fractale actuelle et la remplace par la nouvelle
//						int size = transformationsListModel.getSize();
//						
//						for (int i = size - 1; i >= 0; i--) {
//							transformationsListModel.removeTransformation(i);
//						}
//						for (int i = 0; i < ; i++) {
//							transformationsListModel
//									.addTransformation(newTransformations
//									.get(i));
//						}
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
										"Le fichier n'a pas pu �tre ouvert dans FlameMaker, sûrement parce qu'il est corrompu.",
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
		 * Fonctionnalit� de sauvegarde dans un fichier .flame
		 * Si le ficher actuel est d�fini (par ouverture ou un pr�c�dent appel
		 * � "enregistrer"/"enregistrer sous", la fractale est enregistr�e dedans
		 * Sinon, le m�me comportement que la fonction "Enregistrer sous" est effectu�, 
		 * pour que l'utilisateur puisse choisir dans quel fichier enregistrer sa fractale 
		*/
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFilePath != null) {
					try {
						FlameFile.saveToFile(new SerializableFlameSet(set), currentFilePath);
					}
					catch(FileNotFoundException ex) {
						// Exception lanc�e si le fichier ne peut �tre cr��, par exemple
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

		// Fonctionnalit� "Enregistrer sous"
		saveAsMenuItem.addActionListener(new MenuBar.SaveAsActionListener(window, set));

		// Fonctionnalit� "exporter"
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
	 * La classe impl�mentant la fonctionnalit� "Enregistrer sous".
	 * Notons que cela n'a pas �t� fait dans une classe anonyme comme
	 * pour les autres fonctionnalit�s afin que cette classe puisse �tre
	 * utilis�e depuis deux endroits diff�rents du code
	 */
	private static class SaveAsActionListener implements ActionListener {

		/**
		 * La fen�tre principale du GUI
		 * (utilis�e pour sp�cifier le parent du FileChooser et
		 * pouvoir modifier le titre de la fen�tre lorsque le fichier
		 * a �t� enregistr�)
		 */
		final private JFrame window;
		
		/**
		 *	Le set contenant les informations relatives � la fractale 
		 */
		final private FlameSet set;

		/**
		 * Le constructeur de la classe
		 * @param window		La fen�tre principale du GUI
		 * @param flameBuilder	Le constructeur de la fractale
		 */
		public SaveAsActionListener(JFrame window,
				FlameSet set) {
			this.window = window;
			this.set = set;
		}

		public void actionPerformed(ActionEvent e) {
			// On tente de r�cup�rer le r�pertoire utilisateur par d�faut
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

				// S'il n'a pas indiqu� l'extension dans le nom, on la rajoute
				if (!filePath.endsWith(FlameFile.FLAME_FILE_EXTENSION)) {
					filePath = filePath.concat(FlameFile.FLAME_FILE_EXTENSION);
				}

				// Si le fichier existe d�j�, on demande une confirmation
				if ((new File(filePath).exists())) {
					int confirmValue = JOptionPane.showConfirmDialog(window,
							"Ce fichier existe d�j�. Voulez-vous l'�craser ?",
							"Le fichier existe d�j�",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (confirmValue != JOptionPane.OK_OPTION) {
						return;
					}
				}
				currentFilePath = filePath;
				window.setTitle("FlameMaker - " + filePath);
				
				try {
					FlameFile.saveToFile(new SerializableFlameSet(set), filePath);
				}
				catch (FileNotFoundException e1) {
					// Cette exception est lanc�e si le fichier ne peut pas �tre cr��, par exemple
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
				"La fractale n'a pas pu �tre enregistr�e. V�rifiez que vous disposez des droits n�cessaires � la cr�ation du fichier",
				"Erreur lors de l'enregistrement",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public static void setCurrentFilePath(String filepath) {
		currentFilePath = filepath;
	}

}
