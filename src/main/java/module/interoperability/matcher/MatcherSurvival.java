package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import model.pojo.ClSurvival;


public class MatcherSurvival extends MatcherAbstract {

	public MatcherSurvival(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClSurvival> match() {

		List<ClSurvival> list = new ArrayList<ClSurvival>();
		boolean isFound = false;
		ClSurvival survival = new ClSurvival();



		String patternText = "[0-9]+";
		Pattern pattern = Pattern.compile(patternText);


		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			String value = this.extractValue(listLines.get(l));

			// ===== Dead / Alive =====
			if (line.toLowerCase().contains("dead") || line.toLowerCase().contains("alive")) {
				
				isFound = true;
				if (value!=null && value.contains("1")) {
					survival.setDead(true);
				}
				if (value!=null && value.contains("0")) {
					survival.setDead(false);
				}	
				
			}
			
			// ===== Relapse =====

			if (line.toLowerCase().contains("relapse")) {
				if (value.toLowerCase().contains("yes")) {
					survival.setRelapsed(true);
					isFound = true;
				}
			}
			

			// ===== Overall survival ======
			if (line.toLowerCase().contains("overall")) {

				isFound = true;
				
				Matcher matcher = pattern.matcher(value);
				boolean isPatternFound= matcher.find();
				
				if (isPatternFound) {
					String osString =  matcher.group();
					try {
						Double number = Double.parseDouble(osString);
						
						// ==== Special cases =====
						if (line.toLowerCase().contains("days")) {
							// Conversion from days to months
							Double numberofMonths = number / 30;
							number = round(numberofMonths, 2);
						}
						
						survival.setOsMonths(number);
						
					}
					catch (NumberFormatException e) {
						// nothing to do
					}	
				}

			}

		}

		if (isFound && !survival.isEmptySurvival()) {
			list.add(survival);
		}

		// System.out.println(this.getClass().getName() + " " + list.toString());
		
		return list;
	}
	
}
