/**
 * EpiMed - Information system for bioinformatics developments in the field of epigenetics
 * 
 * This software is a computer program which performs the data management 
 * for EpiMed platform of the Institute for Advances Biosciences (IAB)
 *
 * Copyright University of Grenoble Alps (UGA)
 * GNU GENERAL PUBLIC LICENSE
 * Please check LICENSE file
 *
 * Author: Ekaterina Flin 
 *
 */
package module.interoperability.matcher;

import java.util.List;

import org.hibernate.Session;

import dao.ClOntologyDictionaryDao;
import model.entity.ViewOntologyDictionary;


public abstract class MatcherAbstract {

	protected static String separator =":";

	protected String [] listEthnicGroupPatterns = {"[A-Z][a-zA-z]{1,}ian"};
	protected String [] listExposurePatterns = {"[0-9]++[\\p{Space}]?(h|day[s]?)", "[0-9]++-?[0-9]*+"};
	
	protected String [] listCellLinePatterns = {};
	// protected String [] listCellLinePatterns = {"lymphomaburkittsDaudi", "LymphomaRaji", "[A-Z]{2,}[-]{1}[0-9]+"};
	
	protected String [] listIsolatedCellsPattern = {"[A-Z]{1,5}[0-9]{1,5}[\\+|\\-]", "[a-zA-Z]{1,5}[0-9]{1,5}(neg|pos|NEG|POS)", "PMNs",
			"(P|p)romyelocytes", "(h)?ESC(s)?(-derived EC)?(s)?"};
	
	protected String [] listAgePatterns = {"[-]?[0-9]++-?[.]?[0-9]*+"};
	// protected String [] listAgePatterns = {"[0-9]{2,}"};


	protected List<String> listLines;
	protected Session session;

	/** ==========================================================================*/

	public MatcherAbstract(Session session, List<String> listLines) {
		super();
		this.listLines = listLines;
		this.session = session;
	}

	/** ==========================================================================*/

	public abstract List<?> match();

	/** ==========================================================================*/

	public String extractValue (String line) {

		line = line.replace("_", " ");

		if (line.contains(separator)) {
			String [] parts =  line.split(separator);
			if (!line.endsWith(separator)) {
				return parts[parts.length-1].trim();
			}
			else {
				return null;
			}
		}
		else {
			return line;
		}
	}

	/** ==========================================================================*/

	public List<ViewOntologyDictionary> findDictionaryMatches(String line, String idCategory) {

		List<ViewOntologyDictionary> listDictionaryTerms = null;
		ClOntologyDictionaryDao dictionaryDao = new ClOntologyDictionaryDao(session);

		line = line.toLowerCase().replaceAll("\\p{Punct}", " ");
		line = line.replaceAll(" +", " ");
		String [] parts = line.toLowerCase().split(" ");


		int len = parts.length;
		int i=parts.length-1;
		boolean isFound = false;

		while (!isFound && i>=0) {

			int nbTerms= len-i;
			String[] terms = new String[nbTerms];

			for (int j=0; j<nbTerms; j++) {
				String text="";

				for (int k=j; k<i+j+1; k++) {
					text = text + parts[k] + " ";
				}
				terms[j] = text.trim();
			}


			listDictionaryTerms = dictionaryDao.listView(terms, idCategory);
			isFound = listDictionaryTerms!=null && !listDictionaryTerms.isEmpty();

			// System.out.println(i + " category=" + idCategory + " nbTerms=" + nbTerms + " " + Arrays.toString(terms));
			// System.out.println("listDictionaryTerms=" + listDictionaryTerms);

			i--;
		}


		return listDictionaryTerms;
	}
	
	
	/** ==========================================================
	 * 
	 * @param value
	 * @param scale
	 * @return
	 */
	public static double round (double value, int scale) {
		return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
	}

	/** ==========================================================================*/

	public String toString() {
		return this.getClass().getName();
	}

	/** ==========================================================================*/


}
