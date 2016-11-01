package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.pojo.ClSurvival;


public class DispatcherSurvival extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherSurvival(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		ClSurvival survival = (ClSurvival) makeChoice(mapOntologyObjects, "survival");

		if (survival!=null && !survival.isEmptySurvival()) {
			doc.put("dfs_months", survival.getDfsMonths());
			doc.put("os_months", survival.getOsMonths());
			doc.put("relapsed", survival.getRelapsed());
			doc.put("dead", survival.getDead());
		}

	}

	/** ================================================================================= */



}
