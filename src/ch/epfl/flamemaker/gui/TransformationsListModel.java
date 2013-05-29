/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import javax.swing.AbstractListModel;

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;

/**
 *	Le modèle de la liste des transformations
 */
@SuppressWarnings("serial")
public class TransformationsListModel extends AbstractListModel {

	/**
	 * Le bâtisseur de fractale
	 */
	private ObservableFlameBuilder m_builder;

	/**
	 * Constructeur du modèle de liste
	 * 
	 * @param flameBuilder	Le bâtisseur
	 */
	public TransformationsListModel(ObservableFlameBuilder flameBuilder) {
		m_builder = flameBuilder;
	}

	/**
	 * Retourne le nombre d'éléments dans la liste, 
	 * en l'occurrence le nombre de transformations
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return m_builder.transformationsCount();
	}

	/**
	 * Retourne l'élément à un index donné, 
	 * en l'occurrence une chaîne de caractères
	 * contenant le numéro de la transformation
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index) {
		return "Transformation n°" + (index + 1);
	}

	/**
	 * Ajoute une transformation à la liste
	 * 
	 * @param transfo	La transformation à ajouter
	 */
	public void addTransformation(FlameTransformation transfo) {
		m_builder.addTransformation(transfo);
		int currentIndex = getSize() - 1;
		this.fireIntervalAdded(this, currentIndex, currentIndex);
	}

	/**
	 * Supprime une transformation de la liste
	 * 
	 * @param index		L'index de la transformation à supprimer
	 */
	public void removeTransformation(int index) {
		m_builder.removeTransformation(index);
		this.fireIntervalRemoved(this, index, index);
	}
}