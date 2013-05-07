package ch.epfl.flamemaker.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ExportWindow extends JFrame {
	
	public ExportWindow() {
		
		setPreferredSize(new Dimension(200, 200));
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Exporter la fractale");
		setLocationRelativeTo(null);
		setVisible(true);
		
		final Container contentPane = getContentPane();
		
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		
		//filePathField.setMaximumSize(new Dimension(1000, filePathField.getPreferredSize().height)); 
		
		final String[] formats = new String[] {
			"jpg", "png", "bmp"
		};

		
		
		JPanel formatPanel = new JPanel();
		formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.LINE_AXIS));
		JComboBox formatsList = new JComboBox(formats);
		formatsList.setMaximumSize(new Dimension(1000, formatsList.getPreferredSize().height));
		formatPanel.add(new JLabel("Format : "));
		formatPanel.add(formatsList);
		
		JPanel dimensionPanel = new JPanel();
		dimensionPanel.setLayout(new BoxLayout(dimensionPanel, BoxLayout.LINE_AXIS));
		dimensionPanel.add(new JLabel("Dimensions (px) : "));
		JFormattedTextField weightField = new JFormattedTextField("500");
		weightField.setMaximumSize(new Dimension(1000, weightField.getPreferredSize().height));
		JFormattedTextField heightField = new JFormattedTextField("500");
		heightField.setMaximumSize(new Dimension(1000, heightField.getPreferredSize().height));
		dimensionPanel.add(weightField);
		dimensionPanel.add(new JLabel(" x "));
		dimensionPanel.add(heightField);
		
		JPanel exportPanel = new JPanel();
		exportPanel.setLayout(new BoxLayout(exportPanel, BoxLayout.LINE_AXIS));
		JButton exportButton = new JButton("Exporter");
		exportPanel.add(exportButton);
	
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home")));
				fileChooser.setFileFilter(new FlameFileFilter(formats, "Fichiers image "));
				if(fileChooser.showSaveDialog(contentPane) == JFileChooser.APPROVE_OPTION) {
					System.out.println("Exporting");
				}
			}
		});
		
		contentPane.add(formatPanel);
		contentPane.add(dimensionPanel);
		contentPane.add(exportPanel);
		
		pack();
	}
}
