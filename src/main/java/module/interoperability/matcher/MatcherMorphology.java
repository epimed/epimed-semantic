package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import dao.ClMorphologyDao;
import model.entity.ClMorphology;
import model.entity.ViewOntologyDictionary;

public class MatcherMorphology extends MatcherAbstract {

	public MatcherMorphology(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClMorphology> match() {

		List<ClMorphology> list = new ArrayList<ClMorphology>();
		List<String> listId = new ArrayList<String>();
		// Set<ClMorphology> set = new HashSet<ClMorphology>();
		Set<String> setId = new HashSet<String>();
		ClMorphologyDao morphologyDao = new ClMorphologyDao(session);

		boolean isHealthy = false; 

		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);


			List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "morphology");

			// Detect healthy tissue
			if (line.toLowerCase().contains("non-leukemia") || line.toLowerCase().contains("healthy") ) {
				isHealthy = true;
			}

			// For matched terms, search corresponding morphologies
			for (int i=0; i<listDictionaryTerms.size(); i++) {
					String idMorphology = listDictionaryTerms.get(i).getId().getIdReference();
					setId.add(idMorphology);
			}
		}

		listId.addAll(setId);
		Collections.sort(listId);

		// System.out.println(listId);

		// Take the most detailed morphology found = morphology with the biggest ID

		if (!listId.isEmpty() && !isHealthy) {
			list.add(morphologyDao.find(listId.get(listId.size()-1)));

		}


		/*
		for (String id : listId) {
			list.add(morphologyDao.find(id));
		}
		 */

		return list;
	}

}
