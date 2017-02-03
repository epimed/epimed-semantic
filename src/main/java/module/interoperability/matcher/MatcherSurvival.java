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


		// String patternText = "[0-9]+";
		String patternText = "[0-9]+[\\.]*[0-9]*";
		Pattern pattern = Pattern.compile(patternText);


		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			String value = this.extractValue(listLines.get(l));

			// ===== Dead / Alive =====
			this.recognizeDeadAlive(line, value, isFound, survival);


			// ===== Relapse =====
			this.recognizeRelapse(line, value, isFound, survival);

			
			// ===== Overall survival OS / disease free survival DFS ======
			if (line.toLowerCase().contains("overall") || line.toLowerCase().contains("last_contact")) {
				isFound = true;
				survival.setOsMonths(this.recognizePeriod(line, value, pattern));
			}
			if (line.toLowerCase().contains("free survival") || line.toLowerCase().contains("last_clinical")) {
				isFound = true;
				survival.setDfsMonths(this.recognizePeriod(line, value, pattern));
			}

		}

		if (isFound && !survival.isEmptySurvival()) {
			list.add(survival);
		}

		// System.out.println(this.getClass().getName() + " " + list.toString());

		return list;
	}

	/** ==================================================================================================== */

	private boolean recognizeDeadAlive(String line, String value, boolean isFound, ClSurvival survival) {

		if (line.toLowerCase().contains("dead") 
				|| line.toLowerCase().contains("deceased") 
				|| line.toLowerCase().contains("alive")) {

			isFound = true;

			if (value!=null && value.contains("1") || value.toLowerCase().contains("dead") || value.toLowerCase().contains("deceased")) {
				survival.setDead(true);
			}

			if (value!=null && value.contains("0") || value.toLowerCase().contains("alive")) {
				survival.setDead(false);
			}	
		}

		return isFound;
	}

	/** ===================================================================================================== */

	private boolean recognizeRelapse(String line, String value, boolean isFound, ClSurvival survival) {

		if (line.toLowerCase().contains("relapse") || line.toLowerCase().contains("recurrence")) {
		
			if (value!=null && value.contains("1")) {
				survival.setRelapsed(true);
				isFound = true;
			}
			
			if (value!=null && value.contains("0")) {
				survival.setRelapsed(false);
				isFound = true;
			}
			
			if (value!=null && value.toLowerCase().contains("yes")) {
				survival.setRelapsed(true);
				isFound = true;
			}
			
			if (value!=null && value.toLowerCase().contains("no")) {
				survival.setRelapsed(false);
				isFound = true;
			}
			
		}
		return isFound;

	}

	/** ===================================================================================================== */

	private Double recognizePeriod(String line, String value, Pattern pattern) {

		Double number = null;
		
		Matcher matcher = pattern.matcher(value);
		boolean isPatternFound= matcher.find();

		if (isPatternFound) {
			String numberString =  matcher.group();
			
			// System.out.println("numberString=" + numberString);
			
			try {
				number = Double.parseDouble(numberString);

				// ==== Special cases =====
				if (line.toLowerCase().contains("days")) {
					// Conversion from days to months
					Double numberofMonths = number / 30;
					number = round(numberofMonths, 2);
				}

			}
			catch (NumberFormatException e) {
				// nothing to do
			}	
		}
		
		return number;

	}


}
