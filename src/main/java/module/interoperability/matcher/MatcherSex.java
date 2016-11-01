package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

public class MatcherSex extends MatcherAbstract {

	public MatcherSex(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<String> match() {

		List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();

		for (int l=0; l<listLines.size(); l++) {
	
			String value = this.extractValue(listLines.get(l));

			if (value!=null) {

				if ( value.toLowerCase().equals("m") 
						|| ( value.toLowerCase().contains("male") && !value.toLowerCase().contains("female") )
						|| value.toLowerCase().contains("boy")) {
					set.add("M");
				}


				if ( value.toLowerCase().equals("f") || value.toLowerCase().contains("female") || value.toLowerCase().contains("girl")) {
					set.add("F");
				}
			}


		}

		list.addAll(set);

		return list;
	}

}
