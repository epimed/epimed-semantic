package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.pojo.ClTnm;

public class DispatcherTnm extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherTnm(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {
		
		ClTnm tnm =  (ClTnm) makeChoice(mapOntologyObjects, "tnm");

		doc.put("t", tnm.getT());
		doc.put("n", tnm.getN());
		doc.put("m", tnm.getM());
		doc.put("tnm_stage", tnm.getStage());
		doc.put("tnm_grade", tnm.getGrade());
		
	}

	/** ================================================================================= */



}
