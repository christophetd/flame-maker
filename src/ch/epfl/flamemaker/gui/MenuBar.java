package ch.epfl.flamemaker.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
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

import ch.epfl.flamemaker.concurrent.FlameSet;
import ch.epfl.flamemaker.concurrent.FlameStrategy;
import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Preset;

public class MenuBar {

	private static String currentFilePath = null;
	
	public static void build(final JFrame window, final FlameSet set, final TransformationsListModel transformationsListModel) {
		/* Menu bar */
		final JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Fichier");
		JMenuItem openMenuItem = new JMenuItem("Ouvrir");
		JMenuItem newMenuItem = new JMenuItem("Nouveau");
		JMenu newFromMenuItem = new JMenu("Nouveau à partir d'un modèle");
		JMenuItem saveMenuItem = new JMenuItem("Enregistrer");
		final JMenuItem saveAsMenuItem = new JMenuItem("Enregistrer sous");
		JMenuItem exportMenuItem =  new JMenuItem("Exporter");
		JMenuItem closeMenuItem = new JMenuItem("Quitter");
		
		for(final Preset p : Preset.ALL_PRESETS){
			JMenuItem item = new JMenuItem(p.name());
			newFromMenuItem.add(item);
			item.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					set.loadPreset(p);
				}
				
			});
		}
		
		newMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				set.loadPreset(Preset.EMPTY_PRESET);
			}
			
		});
		
		fileMenu.add(openMenuItem);
		fileMenu.add(newMenuItem);
		fileMenu.add(newFromMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(exportMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);
		
		JMenu helpMenu = new JMenu("Aide");
		JMenuItem documentationMenuItem = new JMenuItem("Documentation [todo]");
		JMenuItem aboutMenuItem = new JMenuItem("À propos");
		
		helpMenu.add(documentationMenuItem);
		helpMenu.add(aboutMenuItem);
		
		JMenu computeMenu = new JMenu("Calcul");
		ButtonGroup computeBG = new ButtonGroup();
		
		boolean hasSelectedStrategy = false;
		for(FlameStrategy fs : FlameStrategy.ALL_STARTEGIES){
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(fs.name());
			if(!fs.isSupported())
				item.setEnabled(false);
			else if(!hasSelectedStrategy){
				item.setSelected(true);
			}
			
			computeBG.add(item);
			computeMenu.add(item);
			
			final FlameStrategy strategy = fs;
			item.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					set.getBuilder().getComputeStrategy().deactivate();
					strategy.activate();
					set.getBuilder().setComputeStrategy(strategy);
				}
				
			});
		}
		
		menuBar.add(fileMenu);
		menuBar.add(computeMenu);
		menuBar.add(helpMenu);
		
		window.setJMenuBar(menuBar);
		
		/* Raccourcis clavier */
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O,
				KeyEvent.CTRL_DOWN_MASK 
		));
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N,
				KeyEvent.CTRL_DOWN_MASK 
		));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_S,
				KeyEvent.CTRL_DOWN_MASK 
		));
		exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E,
				KeyEvent.CTRL_DOWN_MASK 
		));
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_W,
				KeyEvent.CTRL_DOWN_MASK 
		));
		
		/* Comportements */
		documentationMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					  Desktop desktop = java.awt.Desktop.getDesktop();
					  URI oURL = new URI("http://github.com/christophetd/flame-maker/wiki/Documentation");
					  desktop.browse(oURL);
				} 
				catch (Exception ex) {
					  ex.printStackTrace();
				}
			}
		});
		
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(window, "Ce programme a été développé par Hadrien Milano <hadrien.milano@epfl.ch> et Christophe Tafani-Dereeper <christophe.tafani-dereeper@epfl.ch> dans le cadre d'un projet de semestre");				
			}
		});
		openMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty( "user.home" )));	
				fileChooser.addChoosableFileFilter(new FlameFileFilter(".flame", "Fichier de fractale Flame (.flame)"));
				
				// Lorsque l'utilisateur a choisi un fichier à ouvrir
				if(fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					try {
						ArrayList<FlameTransformation> newTransformations = FlameFile.getTransformationsFromFile(filePath);
						
						int size = transformationsListModel.getSize();
						for(int i = size-1; i >= 0; i--) {
							transformationsListModel.removeTransformation(i);
						}
						for(int i = 0; i < newTransformations.size(); i++) {
							transformationsListModel.addTransformation(newTransformations.get(i));
						}
						currentFilePath = filePath;
						window.setTitle("FlameMaker - "+filePath);
					} 
					catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(window, "Le fichier est introuvable", "Erreur lors de l'ouverture", JOptionPane.ERROR_MESSAGE);
					} 
					catch(IOException ioex) {
						JOptionPane.showMessageDialog(window, "Le fichier n'a pas pu être ouvert dans FlameMaker. Peut-être est-il corrompu ?", "Erreur lors de l'ouverture", JOptionPane.ERROR_MESSAGE);
					} 
					catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}        
		});
		
		saveMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(currentFilePath != null) {
					FlameFile.saveToFile(set.getBuilder(), currentFilePath);
				}
				else {
					saveAsMenuItem.doClick();
				} 
			}        
		});
		
		saveAsMenuItem.addActionListener(new MenuBar.SaveAsActionListener(window, set.getBuilder()));
		
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ExportWindow(set);
			}
		});
		
		closeMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int confirmValue = JOptionPane.showConfirmDialog(window, "Voulez-vous vraiment quitter ?", "Quitter", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(confirmValue == JOptionPane.OK_OPTION) {
						System.exit(0);
				}
			}        
		});

	}
	
	private static class SaveAsActionListener implements ActionListener {
		
			final private JFrame window;
			final private ObservableFlameBuilder flameBuilder;
			
			public SaveAsActionListener(JFrame window, ObservableFlameBuilder flameBuilder) {
				this.window = window;
				this.flameBuilder = flameBuilder;
			}
		
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty( "user.home" )));	
				fileChooser.addChoosableFileFilter(new FlameFileFilter(".flame", "Fichier de fractale Flame (.flame)"));
				
				// Lorsque l'utilisateur a choisi le fichier dans lequel enregistrer sa fractale
				if(fileChooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath();
					
					// S'il n'a pas indiqué l'extension dans le nom, on la rajoute
					if(!filePath.endsWith(".flame")) {
						filePath = filePath.concat(".flame");
					}
					
					// Si le fichier existe déjà, on demande une confirmation
					if((new File(filePath).exists())) {
						int confirmValue = JOptionPane.showConfirmDialog(window, "Ce fichier existe déjà. Voulez-vous l'écraser ?", "Le fichier existe déjà", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if(confirmValue != JOptionPane.OK_OPTION) {
							return;
						}
					}
					currentFilePath = filePath;
					window.setTitle("FlameMaker - "+filePath);
					FlameFile.saveToFile(flameBuilder, filePath);
				}
			}
		}
		
	}
