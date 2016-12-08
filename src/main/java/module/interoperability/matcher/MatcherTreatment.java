package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import dao.ClTreatmentMethodDao;
import model.entity.ClTreatmentMethod;
import model.entity.ViewOntologyDictionary;

public class MatcherTreatment extends MatcherAbstract {




	public MatcherTreatment(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClTreatmentMethod> match() {

		List<ClTreatmentMethod> list = new ArrayList<ClTreatmentMethod>();
		Set<String> setId = new HashSet<String>();

		ClTreatmentMethodDao treatmentDao = new ClTreatmentMethodDao (session);

		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			String value = this.extractValue(listLines.get(l));

			if (value!=null) {

				List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "treatment");

				// For matched terms, search corresponding terms
				for (int i=0; i<listDictionaryTerms.size(); i++) {
					String idTreatment = listDictionaryTerms.get(i).getId().getIdReference();
					if (idTreatment!=null) {
						setId.add(idTreatment);
					}
				}
			}
		}

		for (String id : setId) {
			ClTreatmentMethod t = treatmentDao.findById(Integer.parseInt(id));
			if (t!=null) {
				list.add(t);
			}
			
		}

		return list;
	}

}
