package ch.epfl.flamemaker.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;

public class MenuBar {

	private static String currentFilePath = null;
	
	public static void build(final JFrame window, final ObservableFlameBuilder flameBuilder, 
			final TransformationsListModel transformationsListModel) {
		/* Menu bar */
		final JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Fichier");
		JMenuItem openMenuItem = new JMenuItem("Ouvrir");
		JMenuItem saveMenuItem = new JMenuItem("Enregistrer");
		final JMenuItem saveAsMenuItem = new JMenuItem("Enregistrer sous");
		JMenuItem exportMenuItem =  new JMenuItem("Exporter");
		JMenuItem closeMenuItem = new JMenuItem("Quitter");
		
		fileMenu.add(openMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.add(exportMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);
		
		menuBar.add(fileMenu);
		window.setJMenuBar(menuBar);
		
		/* Raccourcis clavier */
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O,
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
		openMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(new File("."));	
				fileChooser.addChoosableFileFilter(new FlameFileFilter());
				
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
					FlameFile.saveToFile(flameBuilder, currentFilePath);
				}
				else {
					saveAsMenuItem.doClick();
				} 
			}        
		});
		
		saveAsMenuItem.addActionListener(new MenuBar.SaveAsActionListener(window, flameBuilder));
		
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
				JFileChooser fileChooser = new JFileChooser(new File("."));	
				fileChooser.addChoosableFileFilter(new FlameFileFilter());
				
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
					
					FlameFile.saveToFile(flameBuilder, filePath);
				}
			}
		}
		
	}
