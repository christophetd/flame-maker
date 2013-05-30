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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ch.epfl.flamemaker.SerializableFlameSet;

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
	public static SerializableFlameSet getSerializableFlameSetFromFile(String filePath) throws IOException, ClassNotFoundException, InvalidFlameFileException {
		if(!(new File(filePath)).exists()) {
			throw new FileNotFoundException();
		}
		
		FileInputStream inputFile = new FileInputStream(filePath);
		GZIPInputStream gzip = new GZIPInputStream(inputFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(gzip);
		
		Object o = objectInputStream.readObject();
		
		objectInputStream.close();
		
		if(!(o instanceof SerializableFlameSet)) throw new InvalidFlameFileException();
		
		return (SerializableFlameSet) o;
	}
	
	/**
	 * Sauvegarde la liste des transformations d'une fractale dans un fichier
	 * 
	 * @param flameBuilder	Le bâtisseur de la fractale à sauvegarder
	 * @param filePath		Le fichier dans lequel enregistrer les transformations
	 */
	public static void saveToFile(SerializableFlameSet set, String filePath) throws FileNotFoundException, IOException {
		FileOutputStream fileOutput = new FileOutputStream(filePath);
		GZIPOutputStream gzip = new GZIPOutputStream(fileOutput);
		ObjectOutputStream objectOutput = new ObjectOutputStream(gzip);
		
		objectOutput.writeObject(set);
		
		gzip.finish();
		fileOutput.close();
	}
}
