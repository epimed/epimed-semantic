package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClTreatmentMethod;

public class DispatcherTreatment extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherTreatment(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		List<Object> listTreatments = mapOntologyObjects.get("treatment");

		if (listTreatments!=null && !listTreatments.isEmpty()) {

			String treatmentString = "";
			for (int i=0; i<listTreatments.size(); i++) {
				ClTreatmentMethod t = (ClTreatmentMethod) listTreatments.get(i);
				treatmentString = treatmentString + t.getName();
				if (i<listTreatments.size()-1) {
					treatmentString = treatmentString + ", ";
				}
			}

			if (treatmentString!=null && !treatmentString.isEmpty()) {
				doc.put("treatment", treatmentString);
			}
		}

	}

	/** ================================================================================= */



}
