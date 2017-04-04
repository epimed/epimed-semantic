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

			// System.out.println("-----");
			// System.out.println("line=" + line + ", value=" + value);

			// ===== Dead / Alive =====
			boolean isFoundDeadAlive = this.recognizeDeadAlive(line, value, survival);
			isFound = isFound || isFoundDeadAlive;
			// System.out.println("Found dead/alive=" + isFoundDeadAlive);

			// ===== Relapse =====
			boolean isFoundRelapse = this.recognizeRelapse(line, value, survival);
			isFound = isFound || isFoundRelapse;

			// ===== Overall survival OS ======
			if (line.toLowerCase().contains("time overall survival")  
					|| line.toLowerCase().contains("time of followup")
					|| line.toLowerCase().contains("os")
					) {
				isFound = true;
				Double osMonths =  this.recognizePeriod(line, value, pattern);
				if (osMonths!=null) {
					survival.setOsMonths(osMonths);
				}
			}
			
			// ===== Disease free survival DFS ======
			if (line.toLowerCase().contains("time to local recurrence") 
					|| line.toLowerCase().contains("t rfs")
					|| line.toLowerCase().contains("mfs")
					|| line.toLowerCase().contains("dfs-month")
					) 
			{
				isFound = true;
				Double dfsMonths =  this.recognizePeriod(line, value, pattern);
				if (dfsMonths!=null) {
					survival.setDfsMonths(dfsMonths);
				}
			}

		}

		if (isFound && !survival.isEmptySurvival()) {
			list.add(survival);
		}

		// System.out.println(this.getClass().getName() + " " + list.toString());

		return list;
	}

	/** ==================================================================================================== */

	private boolean recognizeDeadAlive(String line, String value, ClSurvival survival) {

		boolean isFound = false;

		if (line.toLowerCase().contains("dead") 
				|| line.toLowerCase().contains("death") 
				|| line.toLowerCase().contains("deceased")
				|| line.toLowerCase().contains("alive")
				|| line.toLowerCase().contains("dod")
				|| line.toLowerCase().contains("overall survival event")
				) {

			isFound = true;

			if (value!=null && (value.contains("1") 
					|| value.toLowerCase().contains("dead") 
					|| value.toLowerCase().contains("deceased")
					|| value.toLowerCase().contains("yes")
					)) {
				survival.setDead(true);
			}

			if (value!=null && (value.contains("0") 
					|| value.toLowerCase().contains("alive")
					|| value.toLowerCase().contains("no")
					)) {
				survival.setDead(false);
			}	

		}

		return isFound;
	}

	/** ===================================================================================================== */

	private boolean recognizeRelapse(String line, String value, ClSurvival survival) {

		boolean isFound = false;

		if (line.toLowerCase().contains("relapse") 
				|| line.toLowerCase().contains("rfs")
				|| line.toLowerCase().contains("dfs-event")
				) {

			if (value!=null && value.contains("1")) {
				survival.setRelapsed(true);
				isFound = true;
			}

			if (value!=null && value.contains("0")) {
				survival.setRelapsed(false);
				isFound = true;
			}

			if (value!=null && value.toLowerCase().startsWith("y")) {
				survival.setRelapsed(true);
				isFound = true;
			}

			if (value!=null && value.toLowerCase().startsWith("n") && !value.toLowerCase().contains("na")) {
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

				if (line.toLowerCase().contains("yrs") || line.toLowerCase().contains("years")) {
					// Conversion from years to months
					Double numberofMonths = number * 12;
					number = round(numberofMonths, 2);
				}

			}
			catch (NumberFormatException e) {
				// nothing to do
			}	
		}

		if (number!=null && number==0.0){
			number = null;
		}
		
		return number;

	}


}
