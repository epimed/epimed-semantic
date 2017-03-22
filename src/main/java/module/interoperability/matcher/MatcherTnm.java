package module.interoperability.matcher;



import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import model.pojo.ClTnm;

public class MatcherTnm extends MatcherAbstract {

	public MatcherTnm(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClTnm> match() {

		// System.out.println("=== " + this.getClass().getName() + " ===");


		List<ClTnm> list = new ArrayList<ClTnm>();
		ClTnm tnm = new ClTnm();


		String patternText = "[TNM][0-9][a-z]*";
		Pattern pattern = Pattern.compile(patternText);


		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			String value = this.extractValue(line);

			Matcher matcher = pattern.matcher(line);
			boolean isPatternFound= matcher.find();

			while (isPatternFound) {
				String part = matcher.group();

				isPatternFound= matcher.find();

				if (part.startsWith("T")) {
					tnm.setT(part);
				}
				if (part.contains("N")) {
					tnm.setN(part);
				}
				if (part.startsWith("M")) {
					tnm.setM(part);
				}
			}
			
			if (line.toLowerCase().contains("stage:") && value.toLowerCase()!=null 
					&& !value.toLowerCase().contains("n/a")
					&& !value.toLowerCase().contains("-")) {
				tnm.setStage(value);
			}
			
			if (line.toLowerCase().contains("grade:") && value.toLowerCase()!=null 
					&& !value.toLowerCase().contains("n/a")
					&& !value.toLowerCase().contains("-")) {
				tnm.setGrade(value);
			}

		}

		list.add(tnm);

		return list;

	}
}