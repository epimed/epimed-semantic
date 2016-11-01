package module.interoperability.matcher;



import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import model.pojo.ClTnm;

public class MatcherTnm extends MatcherAbstract {

	public MatcherTnm(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClTnm> match() {

		List<ClTnm> list = new ArrayList<ClTnm>();
		ClTnm tnm = new ClTnm();

		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			String value = this.extractValue(listLines.get(l));

			// ===== T =====
			if (line.toLowerCase().contains(" t:")) {
				tnm.setT(value);
			}

			// ===== N =====
			if (line.toLowerCase().contains(" n:")) {
				tnm.setN(value);
			}

			// ===== M =====
			if (line.toLowerCase().contains(" m:")) {
				tnm.setM(value);
			}

			// ===== Stage =====
			if (line.toLowerCase().contains("stage:")) {
				tnm.setStage(value);
			}

			// ===== Grade =====
			if (line.toLowerCase().contains("grade:")) {
				tnm.setGrade(value);
			}
		}

		list.add(tnm);

		return list;
	}



	/** ================================================================================= */

}
