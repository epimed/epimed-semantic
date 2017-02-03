package module.interoperability.dispatcher;


import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;


public class DispatcherExposure extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherExposure(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		List<Object> list  = mapOntologyObjects.get("exposure");

		if (list!=null && !list.isEmpty()) {
			doc.put("exposure", list.toString().replaceAll("[\\[\\]]", ""));
		}
		else {
			doc.put("exposure", null);
		}

	}
}
