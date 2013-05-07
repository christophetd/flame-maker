package ch.epfl.flamemaker.gui;

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

import javax.swing.JOptionPane;

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder;

public class FlameFile {
	public static ArrayList<FlameTransformation> getTransformationsFromFile(String filePath) throws IOException, ClassNotFoundException {
		if(!(new File(filePath)).exists()) {
			throw new FileNotFoundException();
		}
			// On récupère l'objet sérialisé contenu dans le fichier, en le décompressant au passage
			FileInputStream inputFile = new FileInputStream(filePath);
			GZIPInputStream gzip = new GZIPInputStream(inputFile);
			ObjectInputStream objectInputStream = new ObjectInputStream(gzip);
			Object o = objectInputStream.readObject();
			objectInputStream.close();
			
			if(o instanceof ArrayList) {
				ArrayList<FlameTransformation> transformationsList = (ArrayList) o;
				return transformationsList;
			}
			// Si l'objet désérialisé n'est pas du type ObservableFlameBuilder, le fichier était sûrement invalide
			else {
				throw new IOException();
			}
	}
	
	public static void saveToFile(ObservableFlameBuilder flameBuilder, String filePath) {
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
		try {
			outputFile = new FileOutputStream(filePath);
			gzip = new GZIPOutputStream(outputFile);
			objectOutputStream = new ObjectOutputStream(gzip);
			objectOutputStream.writeObject(transformationsList);
			gzip.finish();
			outputFile.close();
		} catch (FileNotFoundException e1) {
			System.out.println("File not found");
		} catch(IOException e1) {
			System.out.println("Une erreur est survenue");
			e1.printStackTrace();
		}
	}
}
