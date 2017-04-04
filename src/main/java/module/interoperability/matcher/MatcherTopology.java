package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import dao.ClTopologyDao;
import model.entity.ClTopology;
import model.entity.ViewOntologyDictionary;


public class MatcherTopology extends MatcherAbstract {

	public MatcherTopology(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClTopology> match() {

		List<ClTopology> list = new ArrayList<ClTopology>();
		List<String> listId = new ArrayList<String>();
		// Set<ClTopology> set = new HashSet<ClTopology>();
		Set<String> setId = new HashSet<String>();
		ClTopologyDao topologyDao = new ClTopologyDao(session);

		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);

			if (!line.toLowerCase().contains("primary site") && !line.toLowerCase().contains(" - ") 
					&& !line.toLowerCase().contains("symptoms") && !line.toLowerCase().contains("metastatic site")
					&&!line.toLowerCase().contains("non-")) {

				List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "topology");

				// For matched terms, search corresponding topologies
				for (int i=0; i<listDictionaryTerms.size(); i++) {
					String idTopology = listDictionaryTerms.get(i).getId().getIdReference();
					setId.add(idTopology);
				}
			}
		}


		listId.addAll(setId);
		Collections.sort(listId);

		// Take the most detailed topology found = topology with the smallest ID

		if (!listId.isEmpty()) {
			list.add(topologyDao.find(listId.get(0)));
		}


		/*
		for (String id : listId) {
			list.add(topologyDao.find(id));
		}
		 */


		return list;
	}

}
