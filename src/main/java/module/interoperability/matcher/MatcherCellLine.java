package module.interoperability.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;

import dao.ClCellLineDao;
import model.entity.ClCellLine;
import model.entity.ViewOntologyDictionary;
import service.CellosaurusService;


public class MatcherCellLine extends MatcherAbstract {

	ClCellLineDao cellLineDao;

	public MatcherCellLine(Session session, List<String> listLines) {
		super(session, listLines);
		cellLineDao = new ClCellLineDao(session);
	}

	public List<ClCellLine> match() {
		
		// System.out.println("=== " + this.getClass().getName() + " ===");


		List<ClCellLine> list = new ArrayList<ClCellLine>();
		Set<String> set = new HashSet<String>();
		Set<String> setId = new HashSet<String>();
		List<String> listId = new ArrayList<String>();

		Set<ClCellLine> setCellLines = new HashSet<ClCellLine>();


		for (int p=0; p<listCellLinePatterns.length; p++) {

			String patternText = listCellLinePatterns[p];
			Pattern pattern = Pattern.compile(patternText);

			for (int l=0; l<listLines.size(); l++) {
				Matcher matcher = pattern.matcher(listLines.get(l));
				boolean isPatternFound= matcher.find();
				while (isPatternFound) {
					if (!matcher.group().startsWith("RPMI1640") && 
							!matcher.group().startsWith("ATCC")) {
						set.add(matcher.group());
					}
					isPatternFound= matcher.find();
				}	
				
				// System.out.println("\t --> " + pattern + " " + listLines.get(l) + "\t-->\t" + "found=" + isPatternFound + ", extract=" + (isPatternFound ? matcher.group() : null));
			}
		}

		// System.out.println("\t --> Found patterns: " + set);

		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			String nameCellLine = iter.next();
			List<ViewOntologyDictionary> listDictionaryTerms = findDictionaryMatches(getCellLineCode(nameCellLine), "cell_line");

			// System.out.println("\t --> listDictionaryTerms: " + listDictionaryTerms);
			
			for (int i=0; i<listDictionaryTerms.size(); i++) {
				String id = listDictionaryTerms.get(i).getId().getIdReference();
				setId.add(id);
			}
		}
		listId.addAll(setId);
		Collections.sort(listId);

		// ===== From database =====
		for (int i=0; i<listId.size(); i++) {
			list.add(cellLineDao.find(listId.get(i)));
		}	


		// ====== From Cellosaurus =====
		if (list==null || list.isEmpty()) {
			iter = set.iterator();
			while (iter.hasNext()) {
				String nameCellLine = iter.next();
				System.out.println("\t --> Unknown cell line: " + nameCellLine);
				CellosaurusService cellosaurusService = new CellosaurusService(session);
				ClCellLine cl = cellosaurusService.findCellLine(nameCellLine);
				System.out.println("\t --> Found in Cellosaurus: " + cl);
				if (cl!=null) {
					setCellLines.add(cl);	
				}
			}
			list.addAll(setCellLines);
		}

		return list;
	}


	public String getCellLineCode(String name) {
		String result  = name.replaceAll("\\p{Punct}", "").replaceAll("\\p{Space}", "").toLowerCase();
		return result = result.substring(0, Math.min(result.length(), 20));
	}

}
