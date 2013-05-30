/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class FlameMaker {
	
	public static void main(String[] args) {
		
		// Essaye de configurer un look and feel agréable sur toutes les plateformes
		// (celui de windows étant particulièrement moche...)
		configLookAndFeel();
		
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	//try {
	            		new FlameMakerGUI().start();
	            	//}
	            	/*catch(RuntimeException e){
	            		JOptionPane.showMessageDialog(null,
	            			    "Une erreur s'est produite dans flame maker. \n"+e.getMessage(),
	            			    "Erreur dans le programme",
	            			    JOptionPane.ERROR_MESSAGE);
	            	}*/
	            }
	    });
	}
	
	/**
	 * Utilise le look "Nimbus" s'il est installé sur la machine
	 */
	public static void configLookAndFeel(){
		// On itère sur tout les looks installés
		for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
			// Si c'est le look nimbus
			if(info.getName().equals("Nimbus")){
				try {
					// On l'utilise
					UIManager.setLookAndFeel(info.getClassName());
					// Et on peut arrèter d'itérer
					break;
				} catch (Exception e) {
					System.out.println("Erreur de définition du look and feel");
					e.printStackTrace();
				}
			}
		}

	}
}
