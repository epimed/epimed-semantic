package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

public class MatcherEthnicGroup extends MatcherAbstract {

	public MatcherEthnicGroup(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<String> match() {

		List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();

		for (int l=0; l<listLines.size(); l++) {

			String value = this.extractValue(listLines.get(l));

			if (value.length()==1) {
				set.add(value);
			}
			else {

				// === Ethnic group -ian ===
				if (value.toLowerCase().contains("ian")) {
					for (int p=0; p<listEthnicGroupPatterns.length; p++) {

						String patternText = listEthnicGroupPatterns[p];
						Pattern pattern = Pattern.compile(patternText);

						Matcher matcher = pattern.matcher(listLines.get(l));
						boolean isPatternFound= matcher.find();
						if (isPatternFound) {
							set.add(matcher.group());
						}
					}
				}

				// Other (for example, Hispanic or Latino)
				else {
					if (!value.toLowerCase().startsWith("other")) {
						set.add(value);
					}
				}

			}

		}

		list.addAll(set);

		return list;
	}

}
