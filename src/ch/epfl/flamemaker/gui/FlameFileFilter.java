package ch.epfl.flamemaker.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FlameFileFilter extends FileFilter {
	private String[] extensions;
	private String description;
	
	public FlameFileFilter(String[] extensions, String description) {
		this.extensions = extensions;
		this.description = description;
	}
	
	public FlameFileFilter(String extension, String description) {
		this(new String[] { extension }, description);
	}
	
	@Override
	public boolean accept(File file) {
		if(file.isDirectory()) { 
	    	 return true; 
	    }
		String filename = file.getName().toLowerCase();
		for(String extension: extensions) {
			if(filename.endsWith(extension.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return description;
	}

}