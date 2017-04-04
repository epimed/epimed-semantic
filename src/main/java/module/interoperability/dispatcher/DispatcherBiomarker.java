package module.interoperability.dispatcher;


import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

@SuppressWarnings("unchecked")
public class DispatcherBiomarker extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherBiomarker(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		List<Object> list  = mapOntologyObjects.get("biomarker");

		if (list!=null) {
			for (int i=0; i<list.size(); i++) {

				Map <String, String> mapBiomarker = (Map<String, String>) list.get(i);

				for (Map.Entry<String, String> entry : mapBiomarker.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					doc.put(key, value);
				}

				if (doc.get("er")!=null && doc.get("pr")!=null && doc.get("her2")!=null) {
					if (doc.get("er").equals("negative") 
							&& doc.get("pr").equals("negative")
							&& doc.get("her2").equals("negative")) {
						doc.put("triple_negative", true);
					}
					else {
						doc.put("triple_negative", false);
					}
				}
			}

		}
	}
}
