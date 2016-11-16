package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import model.entity.ViewOntologyDictionary;


public class MatcherExposure extends MatcherAbstract {

	

	public MatcherExposure(Session session, List<String> listLines) {
		super(session, listLines);
	}


	public List<String> match() {


		List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();


		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			
			// System.out.println(line);

			boolean isExposed = !line.toLowerCase().contains(": no") 
					&& !line.toLowerCase().contains(": na")
					&& !line.toLowerCase().contains(": nd")
					&& !line.toLowerCase().contains(": negative")
					&& !line.toLowerCase().contains(": non")
					;

			if (isExposed) {

				// ===== Substances =====
				List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "exposure");

				for (int i=0; i<listDictionaryTerms.size(); i++) {
					String id = listDictionaryTerms.get(i).getId().getIdReference();
					set.add(id);
				}
			}
		}

		list.addAll(set);

		return list;

	}
}