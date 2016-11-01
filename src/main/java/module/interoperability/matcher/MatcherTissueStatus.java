package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import dao.ClTissueStatusDao;
import model.entity.ClTissueStatus;
import model.entity.ViewOntologyDictionary;

public class MatcherTissueStatus extends MatcherAbstract {
	public MatcherTissueStatus(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClTissueStatus> match() {

		List<ClTissueStatus> list = new ArrayList<ClTissueStatus>();
		Set<ClTissueStatus> set = new HashSet<ClTissueStatus>();
		Set<String> setId = new HashSet<String>();
		List<String> listId = new ArrayList<String>();

		ClTissueStatusDao tissueStatusDao = new ClTissueStatusDao(session);

		boolean isFound = false;

		// ===== Embryonic stem cell =====
		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);

			if (line.contains("embryonic stem cell")) {
				setId.add("1"); // Normal
			}
		}
		isFound = !setId.isEmpty();


		// ===== Dictionary =====
		if (!isFound) {
			for (int l=0; l<listLines.size(); l++) {

				String line = listLines.get(l);

				List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "tissue_status");

				// For matched terms, search corresponding objects
				for (int i=0; i<listDictionaryTerms.size(); i++) {
					String id = listDictionaryTerms.get(i).getId().getIdReference();
					setId.add(id);
				}
			}
		}
		isFound = !setId.isEmpty();

		listId.addAll(setId);
		Collections.sort(listId);

		for (int i=0; i<listId.size(); i++) {
			list.add(tissueStatusDao.find(Integer.parseInt(listId.get(i))));
		}

		list.addAll(set);
		return list;
	}

}
