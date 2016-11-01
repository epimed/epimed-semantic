package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import dao.ClTissueStageDao;
import model.entity.ClTissueStage;
import model.entity.ViewOntologyDictionary;

public class MatcherTissueStage extends MatcherAbstract {

	String [] stages = {"fetal", "adult", "embryonic"};

	public MatcherTissueStage(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClTissueStage> match() {

		List<ClTissueStage> list = new ArrayList<ClTissueStage>();
		Set<String> setId = new HashSet<String>();
		List<String> listId = new ArrayList<String>();
		ClTissueStageDao tissueStageDao = new ClTissueStageDao(session);

		String patternText = "[0-9]+";
		Pattern pattern = Pattern.compile(patternText);

		for (int l=0; l<listLines.size(); l++) {

			String line = listLines.get(l);
			System.out.println(line);
			List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "tissue_stage");

			for (int i=0; i<listDictionaryTerms.size(); i++) {
				String id = listDictionaryTerms.get(i).getId().getIdReference();
				setId.add(id);
			}

			if (line.toLowerCase().contains("embr")) {
				setId.add("4");
			}


			// ====== Spacial cases =====
			// pcw - post conceptual week
			if (line.toLowerCase().contains("pcw")) {
				Matcher matcher = pattern.matcher(line);
				boolean isPatternFound= matcher.find();

				if (isPatternFound) {
					String pcwAge =  matcher.group();
					try {
						Double number = Double.parseDouble(pcwAge);

						// Embryon < 8 weeks
						if (number<8) {
							setId.add("4"); // embryonic
						}
						// Fetal >= 8 weeks
						else {
							setId.add("2"); // fetal
						}
					}
					catch (NumberFormatException e) {
						// nothing to do
					}	
				}

			}
		}

		listId.addAll(setId);
		Collections.sort(listId);		

		for (int i=0; i<listId.size(); i++) {
			list.add(tissueStageDao.find(Integer.parseInt(listId.get(i))));
		}

		return list;
	}

}
