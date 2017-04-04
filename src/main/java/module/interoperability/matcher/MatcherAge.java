package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

public class MatcherAge extends MatcherAbstract {

	public MatcherAge(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<Double> match() {

		List<Double> list = new ArrayList<Double>();
		Set<Double> set = new HashSet<Double>();

		List<String> listAgeLines =  new ArrayList<String>();

		// ===== Check if the category correspond to 'age' (not  'stage' or 'passage') ===== 

		// System.out.println("listLines = " + listLines);

		for (int l=0; l<listLines.size(); l++) {
			String line = listLines.get(l).toLowerCase();
			boolean isAge = (line.startsWith("age") || line.contains(" age ") ||  line.contains(" age:") 
					|| line.endsWith(" age") || line.contains("dpc") || line.contains("old")) && !line.contains("(bin)");
			// System.out.println("line = " + line + ", isAge = " + isAge);
			if (isAge) {
				listAgeLines.add(line);
			}
		}

		listLines.clear();
		listLines.addAll(listAgeLines);

		// System.out.println("listLines = " + listLines);

		// ===== Try to recognize age =====


		for (int p=0; p<listAgePatterns.length; p++) {

			String patternText = listAgePatterns[p];
			Pattern pattern = Pattern.compile(patternText);

			for (int l=0; l<listLines.size(); l++) {

				String line = listLines.get(l);
				
				String value = this.extractValue(listLines.get(l));	
				// String value = line.split("age:")[1];

				if (value!=null) {
					Matcher matcher = pattern.matcher(value);
					boolean isPatternFound= matcher.find();
					if (isPatternFound) {

						String ageString =  matcher.group();

						// === Minus : two possibilities : two positive ages or one negative age 

						String[] ageParts = ageString.split("-"); // possibly 2 ages: ageMin-ageMax


						if (ageParts.length>1 && (ageParts[0]==null || ageParts[0].isEmpty())) {
							ageParts = new String[1];
							ageParts[0] = value;
						}


						for (int j=0; j<ageParts.length; j++) {
							try {
								Double number = Double.parseDouble(ageParts[j]);

								// ==== Special cases =====
								if (line.toLowerCase().contains("week") || line.toLowerCase().contains("pcw")) {
									// Conversion from weeks to years
									Double numberOfDays = number * 7;
									Double numberOfDaysInAYear = 365.0;
									Double numberOfYears = numberOfDays/numberOfDaysInAYear;
									number = round(numberOfYears, 2);
								}
								if (line.toLowerCase().contains("month")) {
									// Conversion from weeks to years
									Double numberOfMOnthsInAYear = 12.0;
									Double numberOfYears = number/numberOfMOnthsInAYear;
									number = round(numberOfYears, 2);
								}
								if (line.toLowerCase().contains("day") || line.toLowerCase().contains("dpc")) {
									// Conversion from weeks to years
									Double numberOfDaysInAYear = 365.0;
									Double numberOfYears = number/numberOfDaysInAYear;
									number = round(numberOfYears, 2);
								}
								set.add(number);

							}
							catch (NumberFormatException e) {
								// nothing to do
							}
						}
					}
				}
			}
		}

		list.addAll(set);

		Collections.sort(list);

		return list;
	}




}
