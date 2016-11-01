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
 * Author: Ekaterina Bourova-Flin 
 *
 */
package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.hibernate.Session;

import model.bind.Cellosaurus;
import model.entity.ClCellLine;
import model.entity.ClCellLineAlias;
import model.entity.ClMorphology;
import model.entity.ClTopology;
import module.BaseModule;

public class CellosaurusService extends BaseModule {

	Map<String, Cellosaurus> mapCellosaurus = new HashMap<String, Cellosaurus>();
	OntologyService ontologyService;
	Session session;

	public CellosaurusService(Session session) {

		this.session=session;
		this.ontologyService = new OntologyService(session);

		// ===== Read morphology file =====
		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "cellosaurus.txt";
		BaseService baseService = new BaseService(inputfile);
		Scanner scan = new Scanner(baseService.getText().toString()); 


		// ===== Skip header =====
		String line = scan.nextLine();
		while (scan.hasNextLine() && ! line.equals("//")){
			line = scan.nextLine();
		}


		// ===== Read data =====
		int l=0;
		List<String> listText = new ArrayList<String>();
		List<Cellosaurus> listCellosaurus =  new ArrayList<Cellosaurus>();

		boolean isHomoSapiens = false;
		boolean isCancerCellLine = false;
		while (scan.hasNextLine()){
			line = scan.nextLine();

			if (line.equals("//")) {
				// End of an object, binding
				boolean isSave = isHomoSapiens && isCancerCellLine;
				if (isSave) {
					Cellosaurus cello = new Cellosaurus(listText);
					listCellosaurus.add(cello);
					mapCellosaurus.put(getCellLineCode(cello.getId()),  cello);
					if (cello.getListAlias()!=null) {
						for (int j=0; j<cello.getListAlias().size(); j++) {
							mapCellosaurus.put(getCellLineCode(cello.getListAlias().get(j)),  cello);
						}
					}
				}

				// Initialization
				listText.clear();
				isHomoSapiens = false;
				isCancerCellLine = false;
				l++;
			}
			else {
				// Reading object attributes
				if (line.contains("Homo sapiens")) {
					isHomoSapiens = true;
				}
				if (line.contains("Cancer cell line") 
						|| line.contains("Transformed cell line")
						|| line.contains("immortalized cell line")
						|| line.contains("stem cell")
						) {
					isCancerCellLine = true;
				}
				listText.add(line);
			}
		}

		scan.close();
		// System.out.println(listCellosaurus.size() + " entries loaded with " + mapCellosaurus.size() + " cell lines.");

	}

	/** ================================================================ */

	public ClCellLine findCellLine(String cellLineName) {

		ClCellLine cellLine = null;
		boolean isFoundInDatabase = false;
		boolean isAliasFound = false;

		String code = this.getCellLineCode(cellLineName);	
		Cellosaurus cello = mapCellosaurus.get(code);
		
		if (cello!=null) {

			System.out.println(cello);

			cellLine = new ClCellLine();
			cellLine.setIdCellLine(cellLineName);
			cellLine.setSex(cello.getSex());
			cellLine.setAtcc(cello.getAtcc());


			// ===== Search for ID alias =====
			String oldId = cellLine.getIdCellLine(); 
			String newId = cello.getId().trim().replaceAll("\\p{Space}", "-").toUpperCase();
			ClCellLine clDatabase = null;
			if (!oldId.equals(newId) && !newId.contains(" ") && !newId.contains("[") && newId.length()<20) {

				isAliasFound = true;

				cellLine.setIdCellLine(newId);
				clDatabase = session.get(ClCellLine.class, newId);
				isFoundInDatabase = clDatabase!=null;
				System.out.println("Cell line " + oldId + " is an alias of " + newId + ". Search in the database for " + newId +": " + clDatabase);
			}

			// ===== The cell line is already in database =====
			if (isFoundInDatabase) {
				cellLine = clDatabase;	
			}

			// ===== The cell line is new, not found in the database =====
			if (!isFoundInDatabase) {

				if (cello.getHistologyType()!=null) {

					// === Init ===
					List<String> listEntries = new ArrayList<String>();
					listEntries.clear();
					listEntries.add(cello.getHistologyType());

					Map <String, List<Object>> mapOntologyObjects = ontologyService.recognizeOntologyObjects(listEntries);
					System.out.println(ontologyService.toString());

					// Topology
					List<Object> listTopology = mapOntologyObjects.get("topology");
					if (listTopology!=null && !listTopology.isEmpty()) {
						cellLine.setClTopology((ClTopology)listTopology.get(0)); 
					}

					// Morphology
					List<Object> listMorphology = mapOntologyObjects.get("morphology");
					if (listMorphology!=null && !listMorphology.isEmpty()) {
						ClMorphology newMorpho = (ClMorphology)listMorphology.get(0);
						cellLine.setClMorphology(newMorpho); 
						cellLine.setHistologyType(cello.getHistologyType());
					}
				}
				
				// ===== Save Cell Line =====	
				System.out.println("Save cell line in the database " + cellLine);
				session.merge(cellLine);
				session.flush();

			}

			// ===== Save Cell Line Alias =====	
			if (isAliasFound) {
				ClCellLineAlias cellLineAlias = new ClCellLineAlias();
				cellLineAlias.setIdCellLineAlias(oldId);
				cellLineAlias.setClCellLine(cellLine);
				System.out.println("Save cell line alias in the database " + cellLineAlias);
				session.merge(cellLineAlias);
				session.flush();	
			}

		}

		return cellLine;
	}

	/** ================================================================ */

	private String getCellLineCode(String name) {
		return name.replaceAll("\\p{Punct}", "").replaceAll("\\p{Space}", "").toLowerCase();
	}

	/** ================================================================= */


}
