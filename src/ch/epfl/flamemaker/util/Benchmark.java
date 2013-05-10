package ch.epfl.flamemaker.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Permet d'effectuer des relevés de temps d'execution et de les exporter en JSON. <br />
 * Cette classe s'utilise avec le fichier Benchmark.html pour visualiser graphiquement les relevés.<br />
 * (Initialiement développée pour optimiser la stratégie de calcul OpenCL)
 * <br/><br/>
 * Utilisation :<br/>
 * <pre>
 * Benchmark b = new Benchmark();
 * b.startCapture("Etape 1");
 * // Faire des choses en rapport avec l'étape 1
 * b.nextSection("Etape 2");
 * // Faire des choses en rapport avec l'étape 2
 * b.startCapture("Etape 1");
 * // D'autres choses en rapport avec l'étape 1
 * 
 * // Fin, on affiche les données collectées
 * b.stopCapture();
 * System.out.println(b.getResultsJSON());
 * </pre>
 * 
 * Copier coller le résultat dans la page Benchmark.html pour les afficher.
 * @author Hadrien
 *
 */
public class Benchmark {
	
	private long m_currentTime;
	private long m_beginTime;
	
	private long m_totalTime;
	
	private List<Element> m_results;
	
	private String m_currentSection;
	
	/**
	 * Commence la capture du temps sur la section name
	 * 
	 * @param name
	 * 		Nom de la première section de la capture
	 */
	public void startCapture(String name){
		 m_beginTime = System.nanoTime()/1000;
		 m_totalTime = m_currentTime = 0;
		 m_results = new LinkedList<Element>();
		 
		 m_currentSection = name;
	}
	
	/**
	 * Arrête la séquence de capture
	 */
	public void stopCapture(){
		nextSection("blank");
	}
	
	/**
	 * Passe à la section suivante.
	 * @param name
	 * 		Nom de la prochaine section
	 */
	public void nextSection(String name){
		
		long newTime = System.nanoTime()/1000 - m_beginTime;
		
		m_results.add(new Element(
				m_currentSection,
				m_currentTime,
				newTime));
		
		m_totalTime += newTime - m_currentTime;
		m_currentTime = newTime;
		m_currentSection = name;
	}
	
	/**
	 * Exporte les résultats au format JSON.<br/>
	 * Utiliser cette chaîne de caractère pour visualiser à l'aide de l'outil Benchmark.html
	 * @return Une chaîne au format JSON contenant les données de la capture
	 */
	public String getResultsJSON(){
		Map<String, Long> cumulTimes = new HashMap<String, Long>();
		
		String result = "{\n\"totalTime\": "+m_totalTime+",\n\"times\" : [\n";
		
		Iterator<Element> it = m_results.iterator();
		
		while(it.hasNext()){
			
			Element e = it.next();
			
			Long c = cumulTimes.get(e.name);
			if(c == null) c = (long) 0;
			
			c += e.endTime - e.startTime;
			
			cumulTimes.put(e.name, c);
			
			result += "  {\"name\" : \""+e.name+"\", \"s\" : "+e.startTime+", \"e\" : "+e.endTime+"}";
			
			if(it.hasNext())
				result += ",";
			result += "\n";
		}
		
		result += "],\n\"cumul\" : {\n";
		
		Iterator<Entry<String, Long>> it2 = cumulTimes.entrySet().iterator();
		
		while(it2.hasNext()){
			Entry<String, Long> e = it2.next();
			
			result += "  \""+e.getKey()+"\" : "+e.getValue();
			
			if(it2.hasNext())
				result += ",";
			result += "\n";
		}
		
		return result+"}}";
	}
	
	private static class Element{
		public String name;
		public long startTime;
		public long endTime;
		
		public Element(String name, long startTime, long endTime){
			this.name = name;
			this.startTime = startTime;
			this.endTime = endTime;
		}
	}

}
