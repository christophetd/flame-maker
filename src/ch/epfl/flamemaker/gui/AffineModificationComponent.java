package ch.epfl.flamemaker.gui;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class AffineModificationComponent extends JComponent{

	public AffineModificationComponent(){
		
		GroupLayout affineGroup = new GroupLayout(this);
		
		this.setLayout(affineGroup);
		
		// Mise en page de l'édition de la partie affine
		SequentialGroup H = affineGroup.createSequentialGroup();
		affineGroup.setHorizontalGroup(H);
		SequentialGroup V = affineGroup.createSequentialGroup();
		affineGroup.setVerticalGroup(V);
		
		ParallelGroup H1 = affineGroup.createParallelGroup();
		H.addGroup(H1);
		ParallelGroup H2 = affineGroup.createParallelGroup();
		H.addGroup(H2);
		ParallelGroup H3 = affineGroup.createParallelGroup();
		H.addGroup(H3);
		ParallelGroup H4 = affineGroup.createParallelGroup();
		H.addGroup(H4);
		ParallelGroup H5 = affineGroup.createParallelGroup();
		H.addGroup(H5);
		ParallelGroup H6 = affineGroup.createParallelGroup();
		H.addGroup(H6);
		
		ParallelGroup V1 = affineGroup.createParallelGroup();
		V.addGroup(V1);
		ParallelGroup V2 = affineGroup.createParallelGroup();
		V.addGroup(V2);
		ParallelGroup V3 = affineGroup.createParallelGroup();
		V.addGroup(V3);
		ParallelGroup V4 = affineGroup.createParallelGroup();
		V.addGroup(V4);
		
		
		// Ligne "translation"
		JLabel translationLabel = new JLabel("Translation");
		H1.addComponent(translationLabel);
		V1.addComponent(translationLabel);
		
		JFormattedTextField translationFactor = buildFormattedTextField();
		H2.addComponent(translationFactor);
		V1.addComponent(translationFactor);
		
		JButton translationLeftButton = new JButton("←");
		H3.addComponent(translationLeftButton);
		V1.addComponent(translationLeftButton);
		
		JButton translationRightButton = new JButton("→");
		H4.addComponent(translationRightButton);
		V1.addComponent(translationRightButton);
		
		JButton translationUpButton = new JButton("↑");
		H5.addComponent(translationUpButton);
		V1.addComponent(translationUpButton);
		
		JButton translationDownButton = new JButton("↓");
		H6.addComponent(translationDownButton);
		V1.addComponent(translationDownButton);
		
		
		// Ligne "Rotation"
		JLabel rotationLabel = new JLabel("Rotation");
		H1.addComponent(rotationLabel);
		V2.addComponent(rotationLabel);
		
		JFormattedTextField rotationFactor = buildFormattedTextField();
		H2.addComponent(rotationFactor);
		V2.addComponent(rotationFactor);
		
		JButton rotationPositiveButton = new JButton("↺");
		H3.addComponent(rotationPositiveButton);
		V2.addComponent(rotationPositiveButton);
		
		JButton rotationNegativeButton = new JButton("↻");
		H4.addComponent(rotationNegativeButton);
		V2.addComponent(rotationNegativeButton);
		
		
		// Ligne "Dilatation"
		JLabel dilatationLabel = new JLabel("Dilatation");
		H1.addComponent(dilatationLabel);
		V3.addComponent(dilatationLabel);
		
		JFormattedTextField dilatationFactor = buildFormattedTextField();
		dilatationFactor.setInputVerifier(new DilatationInputVerifier());
		
		H2.addComponent(dilatationFactor);
		V3.addComponent(dilatationFactor);
		
		JButton dilatationHPlusButton = new JButton("+ ↔");
		H3.addComponent(dilatationHPlusButton);
		V3.addComponent(dilatationHPlusButton);
		
		JButton dilatationHMinusButton = new JButton("- ↔");
		H4.addComponent(dilatationHMinusButton);
		V3.addComponent(dilatationHMinusButton);
		
		JButton dilatationVPlusButton = new JButton("+ ↕");
		H5.addComponent(dilatationVPlusButton);
		V3.addComponent(dilatationVPlusButton);
		
		JButton dilatationVMinusButton = new JButton("- ↕");
		H6.addComponent(dilatationVMinusButton);
		V3.addComponent(dilatationVMinusButton);
		
		
		// Ligne "Transvection"
		JLabel transvectionLabel = new JLabel("Transvection");
		H1.addComponent(transvectionLabel);
		V4.addComponent(transvectionLabel);
		
		JFormattedTextField transvectionFactor = buildFormattedTextField();
		H2.addComponent(transvectionFactor);
		V4.addComponent(transvectionFactor);
		
		JButton transvectionLeftButton = new JButton("←");
		H3.addComponent(transvectionLeftButton);
		V4.addComponent(transvectionLeftButton);
		
		JButton transvectionRightButton = new JButton("→");
		H4.addComponent(transvectionRightButton);
		V4.addComponent(transvectionRightButton);
		
		JButton transvectionUpButton = new JButton("↑");
		H5.addComponent(transvectionUpButton);
		V4.addComponent(transvectionUpButton);
		
		JButton transvectionDownButton = new JButton("↓");
		H6.addComponent(transvectionDownButton);
		V4.addComponent(transvectionDownButton);
		

		// ← → ↑ ↓ ⟲ ⟳ ↔ ↕
	}
	
	/**
	 * Construit un textField servant à entrer le poids de la modification
	 * ( Evite la duplication de code sur cette tache répétitive)
	 * @return
	 */
	private JFormattedTextField buildFormattedTextField(){
		JFormattedTextField field = new JFormattedTextField(new DecimalFormat("#0.##"));
		field.setValue(1);
		field.setColumns(3);
		
		return field;
	}
	
	/**
	 * Verificateur pour l'input de dilatation
	 */
	private class DilatationInputVerifier extends InputVerifier{

		@Override
		public boolean verify(JComponent input){
			JFormattedTextField tf = (JFormattedTextField) input;
			
			try {
				String text = tf.getText();
				
				// Récupère la valeur dans un double dans tous les cas
				double value = ((Number) tf.getFormatter().stringToValue(text)).doubleValue();
				
			} catch (ParseException e) {
				/* 
				 * On n'a rien à gérer ici car le texte est automatiquement remplacé par 
				 * la valeur précédente.
				 */
			}
			return true;
		}
		
	}
}
