package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

public class MatcherHistologyType extends MatcherAbstract {

	public MatcherHistologyType(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<String> match() {

		List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();

		for (int l=0; l<listLines.size(); l++) {

			String value = this.extractValue(listLines.get(l));

			if (value!=null) {
				set.add(value);
			}

		}

		if (set!=null && set.size()>1) {
			list.add(set.toString().replaceAll("[\\[\\]]", ""));
		}
		else {
			list.addAll(set);
		}

		return list;
	}

}
