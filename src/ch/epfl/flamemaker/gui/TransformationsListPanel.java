/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;

/**
 *	Classe représentant le panneau contenant la liste
 *	des transformations
 */
@SuppressWarnings("serial")
public class TransformationsListPanel extends JPanel {

	/**
	 *	Les observateurs 
	 */
	private List<Listener> m_listeners = new LinkedList<Listener>();

	/**
	 * Le modèle de la liste des transformations
	 */
	private TransformationsListModel m_listModel;

	/**
	 * Le composant JList de la liste des transformations
	 */
	private JList m_transformationsList;

	/**
	 * Le bâtisseur de fractale
	 */
	final private ObservableFlameBuilder m_flameBuilder;
	
	/**
	 * Construit le panneau contenant la liste des
	 * transformations à partir du bâtisseur de fractale
	 * 
	 * @param flameBuilder	Le bâtisseur
	 */
	public TransformationsListPanel(ObservableFlameBuilder flameBuilder) {
		m_flameBuilder = flameBuilder;
		
		JPanel transformationsEditButtons = buildTransformationsEditButtonsPanel();

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Transformations"));

		m_listModel = new TransformationsListModel(flameBuilder);

		m_transformationsList = new JList(m_listModel);

		m_transformationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_transformationsList.setVisibleRowCount(5);
		m_transformationsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				notifyTransformationSelected(m_transformationsList
						.getSelectedIndex());
			}
		});

		JScrollPane transformationsPane = new JScrollPane(m_transformationsList);

		this.add(transformationsPane, BorderLayout.CENTER);
		this.add(transformationsEditButtons, BorderLayout.PAGE_END);

		// Par défaut, la première transformation est sélectionnée
		m_transformationsList.setSelectedIndex(0);
	}

	/**
	 * Crée le panneau contenant les boutons "Ajouter" et "Supprimer"
	 * 
	 * @return	Le panneau en question
	 */
	private JPanel buildTransformationsEditButtonsPanel() {
		JPanel transformationsEditButtons = new JPanel();
		transformationsEditButtons.setLayout(new GridLayout(1, 2));

		// Bouton 'supprimer'
		final JButton deleteTransformationButton = new JButton("Supprimer");
		
		// Comportement au clic
		deleteTransformationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = m_transformationsList.getSelectedIndex();

				// Si une transformation est sélectionnée, on la supprime
				if (selectedIndex != -1) {
					m_listModel.removeTransformation(selectedIndex);
					m_transformationsList.setSelectedIndex(Math.max(0,
							--selectedIndex));
				}
				
				// S'il ne reste qu'une transformation, on désactive le bouton supprimer
				if (m_flameBuilder.transformationsCount() == 1) {
					deleteTransformationButton.setEnabled(false);
				}
			}
		});

		// Bouton "ajouter"
		JButton addTransformationButton = new JButton("Ajouter");

		// Comportement au clic
		addTransformationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// On ajoute la transformation identité au bâtisseur
				m_listModel.addTransformation(new FlameTransformation(
						AffineTransformation.IDENTITY, new double[] { 1, 0, 0,
								0, 0, 0 }));

				int newHighlightedIndex = m_flameBuilder.transformationsCount() - 1;

				// On notifie les observateurs qu'une nouvelle transformation est sélectionnée
				notifyTransformationSelected(newHighlightedIndex);

				// Si le bouton supprimer était désactivé et qu'on a bien plus d'une transformation
				// on le réactive
				if (!deleteTransformationButton.isEnabled()
						&& m_flameBuilder.transformationsCount() > 1) {
					deleteTransformationButton.setEnabled(true);
				}
			}
		});
		
		transformationsEditButtons.add(addTransformationButton);
		transformationsEditButtons.add(deleteTransformationButton);
		
		return transformationsEditButtons;
	}

	/**
	 * @return	Le modèle de liste
	 */
	public TransformationsListModel getListModel() {
		return m_listModel;
	}

	/**
	 * Sélectionne un nouvel index dans la liste des transformations
	 * 
	 * @param id	L'index à sélectionner
	 */
	public void setSelectedTransformationIndex(int id) {
		m_transformationsList.setSelectedIndex(id);
	}

	/**
	 * Ajoute un observateur
	 * 
	 * @param l	L'observateur à ajouter
	 */
	public void addListener(Listener l) {
		m_listeners.add(l);
	}

	/**
	 * Supprime un observateur
	 * 
	 * @param l	L'observateur à supprimer
	 */
	public void removeListener(Listener l) {
		m_listeners.remove(l);
	}

	/**
	 * Notifie les observateurs qu'une nouvelle transformation
	 * a été sélectionnée
	 * 
	 * @param id	L'id de la nouvelle transformation sélectionnée
	 */
	private void notifyTransformationSelected(int id) {
		for (Listener l : m_listeners) {
			l.onTransformationSelected(id);
		}
	}

	/**
	 *	L'interface que les observateurs doivent implémenter
	 */
	public interface Listener {
		public void onTransformationSelected(int transfoId);
	}
}
