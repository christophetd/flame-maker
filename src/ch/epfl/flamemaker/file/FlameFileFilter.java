/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.file;

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
		StringBuilder finalDescription = new StringBuilder();
		finalDescription.append(description);
		finalDescription.append(" (");
		for(int i = 0; i < extensions.length; i++) {
			finalDescription.append(extensions[i]);
			if(i != extensions.length - 1) {
				finalDescription.append(", ");
			}
		}
		finalDescription.append(")");
	
		return finalDescription.toString();
			
	}

}