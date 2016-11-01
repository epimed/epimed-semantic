package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

public class MatcherOther extends MatcherAbstract {

	public MatcherOther(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<Map <String, String>> match() {

		List<Map <String, String>> list = new ArrayList<Map <String, String>>();
		Map <String, String> map = new HashMap <String, String> ();

		String patternText = "(\\p{Upper})+\\p{Space}+[\\p{Digit}.,]+";
		Pattern pattern = Pattern.compile(patternText);

		for (int l=0; l<listLines.size(); l++) {

			String line= listLines.get(l);

			// ===== Line contains separator ":" =====
			if (line.contains(separator)) {
				fillMap(map, line, separator);
			}

			// ===== No separator, probably "space" ======
			// Example: TSC 0.0005
			else {
				Matcher matcher = pattern.matcher(line);
				boolean isPatternFound= matcher.find();
				if (isPatternFound) {
					fillMap(map, matcher.group(), " ");
				}
				matcher.reset();
			}
		}


		list.add(map);

		return list;
	}

	/** =========================================================================================== */

	public void fillMap(Map <String, String> map, String line, String sep) {
		
		String [] parts =  line.split(sep);

		// At least 2 parts : key and value
		if (parts.length>1) {
			String key = parts[parts.length-2].trim();
			String value = parts[parts.length-1].trim();

			map.put(key, value);
		}
	}

	/** =========================================================================================== */

}
