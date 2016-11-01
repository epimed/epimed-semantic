package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

public class MatcherHistologySubtype extends MatcherAbstract {

	public MatcherHistologySubtype(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<String> match() {

		List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();

		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			String value = this.extractValue(listLines.get(l));

			if (value!=null) {

				if (line.toLowerCase().contains("primary site")) {
					set.add(line);
				}
				else {
					set.add(value);
				}
			}

		}

		list.addAll(set);

		return list;
	}

}
