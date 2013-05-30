/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.event.FocusAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;

import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.Variations;

/**
 * Classe représentant le composant de modification des poids des variations
 */
@SuppressWarnings("serial")
public class WeightsModificationComponent extends JComponent {

	/**
	 * Le bâtisseur de la fractale
	 */
	final private ObservableFlameBuilder flameBuilder;

	/**
	 * La transformation actuellement sélectionnée
	 */
	private int selectedTransformationIndex;

	/**
	 * La liste des champs de texte contenant les poids des variations
	 */
	final private ArrayList<JFormattedTextField> fields = new ArrayList<JFormattedTextField>();

	/**
	 * Construit le composant
	 * 
	 * @param flameBuilder
	 *            Le bâtisseur de la fractale
	 */
	public WeightsModificationComponent(
			final ObservableFlameBuilder flameBuilder) {
		this.flameBuilder = flameBuilder;

		GroupLayout weightsGroup = new GroupLayout(this);
		this.setLayout(weightsGroup);

		SequentialGroup H = weightsGroup.createSequentialGroup();
		weightsGroup.setHorizontalGroup(H);
		SequentialGroup V = weightsGroup.createSequentialGroup();
		weightsGroup.setVerticalGroup(V);

		/*
		 * On crée les groupes verticaux et horizontaux nécessaires. Autant de
		 * groupes verticaux que de variations, sur 2 lignes.
		 */
		ArrayList<ParallelGroup> verticalGroups = new ArrayList<ParallelGroup>();
		ParallelGroup currentGroup;
		for (int i = 0; i < Variations.values().length ; i++) {
			currentGroup = weightsGroup.createParallelGroup(GroupLayout.Alignment.TRAILING);
			verticalGroups.add(currentGroup);
			H.addGroup(currentGroup);
		}
		ArrayList<ParallelGroup> horizontalGroups = new ArrayList<ParallelGroup>();
		for (int i = 0; i < 2; i++) {
			currentGroup = weightsGroup.createParallelGroup(GroupLayout.Alignment.BASELINE);
			horizontalGroups.add(currentGroup);
			V.addGroup(currentGroup);
		}

		/*
		 * On crée les champs de texte et les étiquettes associées.
		 */
		int h = 0, v = 0;
		for (Variations variation : Variations.values()) {
			final JLabel label = new JLabel(variation.printableName());
			final JFormattedTextField formattedTextField = buildFormattedTextField();
			formattedTextField.setInputVerifier(new WeightInputVerifier());
			fields.add(formattedTextField);

			final Variations fVariation = variation;
			
			// On ajoute un eventListener pour écouter lorsque la valeur du
			// champ de texte change
			formattedTextField.addPropertyChangeListener("value",
					new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							/*
							 * Quand le champ est modifié, on récupère la
							 * transformation associée et on modifie son poids
							 * grâce à la méthode setVariationWeight du
							 * bâtisseur
							 */

							double newWeight = ((Number) formattedTextField
									.getValue()).doubleValue();

							if (flameBuilder.getTransformation(selectedTransformationIndex).weight(fVariation) != newWeight ) {
								flameBuilder.setVariationWeight(
										selectedTransformationIndex,
										fVariation, newWeight);
							}
						}
					});

			/*
			 * On ajoute l'étiquette et le champ de texte aux groupes
			 * horizontaux et verticaux correspondant
			 */
			horizontalGroups.get(h).addComponent(label);
			verticalGroups.get(v).addComponent(label);

			horizontalGroups.get(h).addComponent(formattedTextField);
			verticalGroups.get(v + 1).addComponent(formattedTextField);

			v += 2;

			/*
			 * Si y'a eu 6 éléments ajoutés (2*3 variations, 1 label et 1 champ
			 * de texte par variation), on passe à la ligne suivante
			 */
			if (v % 6 == 0) {
				h++;
				v = 0;
			}

			// On ajoute un espace pour bien distinguer les différents champs
			H.addPreferredGap(ComponentPlacement.UNRELATED);
		}

	}

	/**
	 * Méthode appelée lorsque la transformation sélectionnée change
	 * 
	 * @param id
	 *            L'id de la nouvelle transformation sélectionnée
	 */
	public void setSelectedTransformationIndex(int id) {
		if (id != -1) {
			this.selectedTransformationIndex = id;

			/*
			 * On récupère le tableau des poids de la nouvelle transformation,
			 * et on remplit nos champs de texte avec
			 */
			double[] newWeights = flameBuilder.getTransformation(id).weights();
			int i = 0;
			for (double weight : newWeights) {
				fields.get(i).setValue(weight);
				i++;
			}
		}
	}

	/**
	 * Crée un champ de texte formatté, évite la duplication de code.
	 * 
	 * @return JFormattedTextField Le champ de texte formatté construit
	 */
	private JFormattedTextField buildFormattedTextField() {
		final JFormattedTextField field = new JFormattedTextField(
				new DecimalFormat("#0.##"));
		field.setValue(0);
		field.setColumns(3);

		/*
		 * On fait en sorte que les valeurs des champs se sélectionnent au focus
		 * Note : On doit "emballer" le selectAll() dans un invokeLater, sinon
		 * le formattage du champ annule la sélection
		 */
		field.addFocusListener(new FocusAdapter() {
			public void focusGained(java.awt.event.FocusEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						field.selectAll();
					}
				});
			}
		});

		return field;
	}

	/**
	 * Verificateur pour les inputs des poids Un poids est considéré comme
	 * valide s'il est compris entre 0 et 1 inclus
	 */
	private class WeightInputVerifier extends InputVerifier {

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
				 * champ est valide si le poids est compris entre 0 et 1
				 */
				if (value.doubleValue() < 0 || value.doubleValue() > 1) {
					tf.setValue(tf.getValue());
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
