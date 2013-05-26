/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.file;

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

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;

/**
 *	Classe permettant d'enregistrer ou de récupérer d'un fichier une
 *	liste de transformations FlameTransformation
 */
public class FlameFile {
	
	/**
	 * L'extension des fichiers de sauvegarde
	 */
	final static public String FLAME_FILE_EXTENSION = ".flame";
	
	/**
	 * Retourne la liste des transformations contenue dans un fichier
	 * 
	 * @param filePath	Le fichier duquel récupérer les transformations
	 * @return	ArrayList<FlameTransformation> La liste des transformations contenu dans le fichier
	 * @throws IOException, ClassNotFoundException Si une erreur se produit durant la désérialisation ou la lecture du fichier
	 * @throws InvalidFlameFileException Si le fichier ouvert est invalide
	 */
	@SuppressWarnings("rawtypes")
	public static ArrayList<FlameTransformation> getTransformationsFromFile(String filePath) throws IOException, ClassNotFoundException, InvalidFlameFileException {
		if(!(new File(filePath)).exists()) {
			throw new FileNotFoundException();
		}
		// On récupère l'objet sérialisé contenu dans le fichier, en le décompressant au passage
		FileInputStream inputFile = new FileInputStream(filePath);
		GZIPInputStream gzip = new GZIPInputStream(inputFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(gzip);
		Object o = objectInputStream.readObject();
		objectInputStream.close();
		
		
		// Si ce qui était enregistré dans le fichier était bel et bien une liste de transformations
		if(o instanceof ArrayList && ((ArrayList) o).get(0) instanceof FlameTransformation) {
			ArrayList<FlameTransformation> transformationsList = (ArrayList<FlameTransformation>) o;
			return transformationsList;
		}
		// Si l'objet désérialisé n'est pas du type ObservableFlameBuilder, le fichier était sûrement invalide
		else {
			throw new InvalidFlameFileException();
		}
	}
	
	/**
	 * Sauvegarde la liste des transformations d'une fractale dans un fichier
	 * 
	 * @param flameBuilder	Le bâtisseur de la fractale à sauvegarder
	 * @param filePath		Le fichier dans lequel enregistrer les transformations
	 */
	public static void saveToFile(ObservableFlameBuilder flameBuilder, String filePath) throws FileNotFoundException, IOException {
		FileOutputStream outputFile;
		GZIPOutputStream gzip;
		ObjectOutputStream objectOutputStream;
		
		// On sérialise la liste des transformations de la fractale dans un fichier, en le compressant au passage
		
		// Etape 1 : construire cette liste
		ArrayList<FlameTransformation> transformationsList = new ArrayList<FlameTransformation>();
		for(int i = 0; i < flameBuilder.transformationsCount(); i++) {
			transformationsList.add(flameBuilder.getTransformation(i));
		}
		
		
		// Etape 2 : l'enregistrer dans un fichier
		outputFile = new FileOutputStream(filePath);
		gzip = new GZIPOutputStream(outputFile);
		objectOutputStream = new ObjectOutputStream(gzip);
		objectOutputStream.writeObject(transformationsList);
		gzip.finish();
		outputFile.close();
	}
}
