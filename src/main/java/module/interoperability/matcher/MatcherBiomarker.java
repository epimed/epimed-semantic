package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import model.entity.ClBiomarker;
import model.entity.ViewOntologyDictionary;

public class MatcherBiomarker extends MatcherAbstract {

	Map <String, Set<String>> mapSet = new HashMap <String, Set<String>> ();
	
	public MatcherBiomarker(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<Map <String, String>> match() {

		List<Map <String, String>> list = new ArrayList<Map <String, String>>();
		Map <String, String> map = new HashMap <String, String> ();
		
		

		for (int l=0; l<listLines.size(); l++) {

			String line= listLines.get(l).toLowerCase();
			String value = this.extractValue(listLines.get(l));	

			List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "biomarker");

			ClBiomarker biomarker = null; 
			if (listDictionaryTerms!=null && !listDictionaryTerms.isEmpty()) {
				biomarker = session.get(ClBiomarker.class, listDictionaryTerms.get(0).getId().getIdReference());
			}

			if (biomarker!=null && value!=null) {
				value = value.toLowerCase().trim();
				value = this.normalizeValue(value);
				if (value!=null) {
					this.addToMapSet(biomarker.getBiomarker(), value);
				}
			}
		}

		
		for (Map.Entry<String, Set<String>> entry : mapSet.entrySet()) {
		    String key = entry.getKey();
		    Set<String> set = entry.getValue();
		    	map.put(key, set.toString().replaceAll("[\\[\\]]", ""));
		}
		
		list.add(map);

		return list;
	}

	/** ======================================================= */
	
	public void addToMapSet(String key, String value) {
		if (mapSet.get(key)==null) {
			mapSet.put(key, new HashSet<String>());
		}
		mapSet.get(key).add(value);
	}

	/** ======================================================= */

	public String normalizeValue(String value) {

		// === Specific ===

		if (value.contains("p53-")) {
			return "MUT";
		}
		if (value.contains("p53+") || value.contains("wt")) {
			return "WT";
		}

		// === General ===

		if (value.contains("?") || value.contains("--") || value.contains("na")) {
			return null;
		}
		
		if (value.contains("pos") || value.equals("+") || value.equals("1") || value.contains("y") || value.contains("yes")) {
			return "positive";
		}
		if (value.contains("neg") || value.equals("-") || value.equals("0") || value.contains("n") || value.contains("no")) {
			return "negative";
		}
		

		return null;

	}

}