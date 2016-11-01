package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import model.entity.ClPathology;
import model.entity.ViewOntologyDictionary;


public class MatcherPathology extends MatcherAbstract {

	public MatcherPathology(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClPathology> match() {

		List<ClPathology> list = new ArrayList<ClPathology>();
		List<String> listId = new ArrayList<String>();
		Set<String> setId = new HashSet<String>();

		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "pathology");;

			// For matched terms, search corresponding topologies
			for (int i=0; i<listDictionaryTerms.size(); i++) {
				String idPathology = listDictionaryTerms.get(i).getId().getIdReference();
				setId.add(idPathology);
			}
		}

		listId.addAll(setId);
		Collections.sort(listId);
		
		
		for (int i=0; i<listId.size(); i++) {
			list.add(session.get(ClPathology.class, listId.get(i).trim()));
		}

		return list;
	}

}
