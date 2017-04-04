package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import dao.ClOntologyCategoryDao;
import dao.ClOntologyKeywordDao;
import model.entity.ClOntologyCategory;
import model.entity.ClOntologyKeyword;
import module.interoperability.matcher.MatcherAbstract;
import module.interoperability.matcher.MatcherFactory;

public class OntologyService {

	/** ================================================================================= */
	/** ATTRIBUTES
	/** ================================================================================= */

	private static String separator = ":";
	private MatcherFactory matcherFactory;
	private ClOntologyCategoryDao ontologyCategoryDao;
	private ClOntologyKeywordDao ontologyKeywordDao;
	private List<ClOntologyKeyword> listOntologyKeywords;
	private ClOntologyCategory categoryOther;
	private ClOntologyCategory categoryNotRecognized;
	private ClOntologyCategory categoryCellLine;

	private String [] listCellLinePatterns = {"[A-Z]{1,5}[-]?[A-Z0-9]{1,5}[-]?([0-9]{1,5})++[A-Z]?"};

	// mapOntologyCategories: summary of recognized categories for one GSM
	private Map <ClOntologyCategory, Set<String>> mapOntologyCategories;

	// mapOntologyObjects: summary of recognized objects for one GSM
	private Map <String, List<Object>> mapOntologyObjects;

	/** ================================================================================= */
	/** CONSTRUCTOR
	/** ================================================================================= */

	public OntologyService(Session session) {

		// ===== Service =====
		matcherFactory = new MatcherFactory(session);

		// ===== DAO =====
		ontologyCategoryDao = new ClOntologyCategoryDao(session);
		ontologyKeywordDao = new ClOntologyKeywordDao(session);

		// ===== Load ontology =====
		listOntologyKeywords = ontologyKeywordDao.listAssembledKeywords();
		categoryOther = ontologyCategoryDao.find("other");
		categoryNotRecognized = ontologyCategoryDao.find("not recognized");
		categoryCellLine = ontologyCategoryDao.find("cell_line");
	}

	/** ================================================================================= */
	/** GETTERS
	/** ================================================================================= */


	public Map<ClOntologyCategory, Set<String>> getMapOntologyCategories() {
		return mapOntologyCategories;
	}

	public void setMapOntologyCategories(Map<ClOntologyCategory, Set<String>> mapOntologyCategories) {
		this.mapOntologyCategories = mapOntologyCategories;
	}

	public Map<String, List<Object>> getMapOntologyObjects() {
		return mapOntologyObjects;
	}

	public void setMapOntologyObjects(Map<String, List<Object>> mapOntologyObjects) {
		this.mapOntologyObjects = mapOntologyObjects;
	}

	/** ================================================================================= */
	/** METHODS
	/** ================================================================================= */


	@SuppressWarnings({ "unchecked" })
	public Map <String, List<Object>> recognizeOntologyObjects (List<String> listEntries) {

		mapOntologyCategories = this.recognizeOntologyCategories(listEntries);

		mapOntologyObjects = new HashMap<String, List<Object>>();

		for (ClOntologyCategory ontologyCategory: mapOntologyCategories.keySet()) {

			List <String> listLines = new ArrayList<String>();
			listLines.addAll(mapOntologyCategories.get(ontologyCategory));

			MatcherAbstract matcher = matcherFactory.getMatcher(ontologyCategory, listLines);

			if (matcher!=null) {
				
				List<Object> listObjects = (List<Object>) matcher.match();
				
				if (mapOntologyObjects.get(ontologyCategory.getIdCategory())==null) {
					mapOntologyObjects.put(ontologyCategory.getIdCategory(), new ArrayList<Object>());
				}
				mapOntologyObjects.get(ontologyCategory.getIdCategory()).addAll(listObjects);
			}
		}

		return mapOntologyObjects;
	}


	/** ================================================================================= */

	public Map <ClOntologyCategory, Set<String>> recognizeOntologyCategories (List<String> listEntries) {

		mapOntologyCategories = new HashMap <ClOntologyCategory, Set<String>>();

		for (int i=0; i<listEntries.size(); i++) {

			String line = listEntries.get(i);	

			if (line!=null) {

				String lineLowCase = line.toLowerCase().replaceAll("\\p{Punct}", " ").trim().replaceAll(" +", " ");

				// ===== Search cell line pattern =====
				boolean isPatternFound = false;
				int p=0;
				while (!isPatternFound && p<listCellLinePatterns.length) {
					String patternText = listCellLinePatterns[p];
					Pattern pattern = Pattern.compile(patternText);
					Matcher matcher = pattern.matcher(line);
					isPatternFound= matcher.find();
					p++;
				}
				if (isPatternFound) {
					// Update a summary of recognized categories
					this.updateMapOntologyCategories(categoryCellLine, line);
				}


				// ===== Search in a list of keywords =====
				boolean isKeywordFound = false;
				boolean isPrimaryCategory = false;

				for (int j=0; j<listOntologyKeywords.size(); j++) {

					ClOntologyKeyword ontologyKeyword = listOntologyKeywords.get(j);

					// Search category by keyword
					String keyword = ontologyKeyword.getId().getIdKeyword();
					isKeywordFound = lineLowCase.contains(keyword);
					
					if (isKeywordFound) {

						ClOntologyCategory ontologyCategory = ontologyKeyword.getClOntologyCategory();
						isPrimaryCategory = true;

						// Update a summary of recognized categories
						this.updateMapOntologyCategories(ontologyCategory, line);
					}
				}

				// ===== Attribute "other" category (if not recognized as a primary category) =====
				boolean isOtherCategory = line.toLowerCase().contains(separator) 
						&& !line.contains("NA")
						&& !line.contains("ND");

				if  (!isPrimaryCategory && isOtherCategory && !isPatternFound) {
					this.updateMapOntologyCategories(categoryOther, line);
				}

				// ===== Not recognized attribute =====
				if  (!isPrimaryCategory && !isOtherCategory && !isPatternFound) {
					this.updateMapOntologyCategories(categoryNotRecognized, line);
				}
			}
		}

		return mapOntologyCategories;

	}

	/** ================================================================================= */

	private void updateMapOntologyCategories (ClOntologyCategory ontologyCategory, String line) {
		// Update a summary of recognized categories : mapInput

		Set<String> setLines = mapOntologyCategories.get(ontologyCategory);
		if (setLines==null) {
			mapOntologyCategories.put(ontologyCategory, new HashSet<String> ());
		}
		mapOntologyCategories.get(ontologyCategory).add(line);
	}


	/** ================================================================================= */

	public String toString () {

		String result = "Recognized categories:" + "\n";
		for (ClOntologyCategory ontologyCategory: mapOntologyCategories.keySet()) {
			Set<String> setText = mapOntologyCategories.get(ontologyCategory);
			List<Object> listObjects = mapOntologyObjects.get(ontologyCategory.getIdCategory());
			result = result + "  - " + ontologyCategory.getIdCategory() + ":\t"  + setText + " -->\t" + listObjects + "\n"; 
		}
		return result;
	}

	/** ================================================================================= */

}
