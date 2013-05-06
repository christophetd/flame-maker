package ch.epfl.flamemaker.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FlameFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		if(file.isDirectory()) { 
	    	 return true; 
	    }
		return file.getName().toLowerCase().endsWith(".flame");
	}

	@Override
	public String getDescription() {
		return "Fichier de fractale Flame (.flame)";
	}

}