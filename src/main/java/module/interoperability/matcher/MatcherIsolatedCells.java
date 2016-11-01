package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

public class MatcherIsolatedCells extends MatcherAbstract {

	public MatcherIsolatedCells(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<String> match() {

		List<String> list = new ArrayList<String>();
		Set<String> set = new HashSet<String>();


		for (int p=0; p<listIsolatedCellsPattern.length; p++) {

			String patternText = listIsolatedCellsPattern[p];
			Pattern pattern = Pattern.compile(patternText);

			for (int l=0; l<listLines.size(); l++) {

				// ===== Pattern recognition =====

				String value = listLines.get(l);
				Matcher matcher = pattern.matcher(value);
				boolean isPatternFound= matcher.find();
				if (isPatternFound) {
					String cellName = matcher.group().replaceAll("pos","+").replaceAll("neg", "-");
					cellName = cellName.replaceAll("PMNS", "PMNs");
					set.add(cellName);
				}
				
				if (value.toLowerCase().contains("endothelial cells")) {
					set.add("ECs");
				}
			}
		}

		list.addAll(set);
		
		return list;
	}

}
