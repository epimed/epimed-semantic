package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import model.entity.ClCollectionMethod;
import model.entity.ViewOntologyDictionary;


public class MatcherCollectionMethod extends MatcherAbstract {

	public MatcherCollectionMethod(Session session, List<String> listLines) {
		super(session, listLines);
	}

	public List<ClCollectionMethod> match() {

		// System.out.println("=== " + this.getClass().getName() + " ===");

		List<ClCollectionMethod> list = new ArrayList<ClCollectionMethod>();
		Set<ClCollectionMethod> set = new HashSet<ClCollectionMethod>();

		for (int p=0; p<listIsolatedCellsPattern.length; p++) {

			String patternText = listIsolatedCellsPattern[p];
			Pattern pattern = Pattern.compile(patternText);

			for (int l=0; l<listLines.size(); l++) {

				String line = listLines.get(l);
				String value = this.extractValue(line);

				// ===== Xenograft  =====
				if (value.toLowerCase().contains("xenograft")) {
					set.add(session.get(ClCollectionMethod.class, "xenograft"));
				}

				// ===== Embryonic stem cells ======
				else if (value.toLowerCase().contains("embryonic stem cell")) {
					set.add(session.get(ClCollectionMethod.class, "isolated cells"));
				}

				else {

					// ===== Cell line =====
					if (value.toLowerCase().contains("cell line") || value.toLowerCase().contains("cell culture")) {
						set.add(session.get(ClCollectionMethod.class, "cell line"));
					}

					// ===== Isolated cells ======
					if (value.toLowerCase().contains("isolated") || 
							value.toLowerCase().contains("sorted") ||
							value.toLowerCase().contains("endothelial cells")) {
						set.add(session.get(ClCollectionMethod.class, "isolated cells"));
					}

					if (set.isEmpty()) {
						Matcher matcher = pattern.matcher(value);
						boolean isPatternFound= matcher.find();
						if (isPatternFound) {
							set.add(session.get(ClCollectionMethod.class, "isolated cells"));
						}	
					}

					if (set.isEmpty()) {
						// === Search for a particular cell in the dictionary ===
						List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(line, "collection_method");

						// System.out.println("listDictionaryTerms " + listDictionaryTerms);
						
						// For matched terms, search corresponding id
						for (int i=0; i<listDictionaryTerms.size(); i++) {
							String idCollectionMethod = listDictionaryTerms.get(i).getId().getIdReference();
							set.add(session.get(ClCollectionMethod.class, idCollectionMethod));
						}
					}

				}

			}
		}

		list.addAll(set);


		return list;
	}

}
