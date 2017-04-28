package module.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPattern {

	private String [] parts = {"Source Name", "Comment[BioSD_SAMPLE]", "Characteristics[developmental stage]", "Characteristics[sex]"};
	private String patternText = "\\[[\\p{Print}\\p{Space}]+\\]";

	public TestPattern() {
		Pattern pattern = Pattern.compile(patternText);

		for (String part : parts) {
			Matcher matcher = pattern.matcher(part);
			boolean isPatternFound = matcher.find();
			System.out.print(part);
			if (isPatternFound) {
				System.out.println(" \t\t ---> \t " + matcher.group() + "\t" + isPatternFound);
			}
			else {
				System.out.println(" \t\t ---> \t " + isPatternFound);
			}
			
		}
	}

	public static void main(String[] args) {
		new TestPattern();
	}

}
