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
package module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.HibernateUtil;
import config.MongoUtil;
import model.entity.ClOntologyCategory;
import module.interoperability.dispatcher.DispatcherException;
import module.interoperability.dispatcher.DispatcherFactory;
import service.OntologyService;


public class AnalyseGeo extends BaseModule {

	private String gseNumber = "GSE61304";
	
	private boolean commit = true;

	// === Categories to update ===
	// private String [] categories = {"biomarker"};
	private String [] categories = {"pathology", "collection_method", "patient", "topology", "morphology", "biopatho", "tissue_stage", "tissue_status",
	 		"survival", "tnm", "exposure", "treatment", "biomarker"};

	// === Initialization ===
	private Map <String, Set<String>> mapNotRecognized = new HashMap <String, Set<String>>();
	private Map <String, Set<String>> mapRecognized = new HashMap <String, Set<String>>();
	private Map <String, Set<String>> mapRecognizedSeveral = new HashMap <String, Set<String>>();


	@SuppressWarnings({ "unchecked", "unused" })
	public AnalyseGeo () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");
		List<Document> listDocuments = collection
				.find(Filters.in("series", gseNumber))
				// .find(Filters.and(Filters.in("series", gseNumber), Filters.eq("analyzed", false)))
				.into(new ArrayList<Document>());



		// ===== Service =====
		OntologyService ontologyService = new OntologyService(session);
		DispatcherFactory dispatcherFactory = new DispatcherFactory(session);


		// ===== Analyse ======

		for (int i=0; i<listDocuments.size(); i++) {
		// for (int i=0; i<1; i++) {
			Document doc = listDocuments.get(i);
			Document expGroup = (Document) doc.get("exp_group");

			String gsmNumber = doc.getString("_id");

			List<String> listEntries = new ArrayList<String>();
			List<String> parameters = new ArrayList<String>();


			String title = (String) expGroup.get("sample_title");
			String source = (String) expGroup.get("sample_source");
			listEntries.add(title);
			listEntries.add(source);

			Map<String, Object> mapParameters = (Map<String, Object>) doc.get("parameters");
			parameters.addAll(mapParameters.keySet());
			parameters.remove("id_sample");
			parameters.remove("extract_protocol");

			for (int j=0; j<parameters.size(); j++) {
					listEntries.add(parameters.get(j) + ": " + mapParameters.get(parameters.get(j)));
			}

			// === Clear already filled fields (only if necessary) ===
			// this.clear(expGroup);

			Map <String, List<Object>> mapOntologyObjects = ontologyService.recognizeOntologyObjects(listEntries);
			// Map <ClOntologyCategory, Set<String>> mapOntologyCategories = ontologyService.getMapOntologyCategories();
			// this.generateSummary(ontologyService, mapOntologyCategories, mapOntologyObjects);


			System.out.println("------------------------------------------------------------");
			System.out.println(i + " " + gsmNumber + " " + listEntries);
			System.out.println(ontologyService.toString());

			// ===== Create mapping objects and making links =====


			try {


				// === Dispatcher ===
				for (int j=0; j<categories.length; j++) {

					dispatcherFactory.getObject(expGroup, mapOntologyObjects, categories[j]);

					System.out.print(categories[j]);
					if (expGroup.getString(categories[j])!=null) {
						System.out.print(" " + expGroup.getString(categories[j]) + "\n");
					}
					else {
						System.out.print("\n");
					}

				}

				System.out.println(expGroup);

				// Update Mongo document
				doc.put("exp_group", expGroup);
				doc.put("analyzed", true);
				if (commit) {
					UpdateResult updateResult = collection.updateOne(Filters.eq("_id", gsmNumber), new Document("$set", doc));
				}


			} catch (DispatcherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}



		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}

	/** ===================================================================================== */

	public void generateSummary(OntologyService ontologyService, Map <ClOntologyCategory, Set<String>> mapOntologyCategories, Map <String, List<Object>> mapOntologyObjects) {

		// ===== Generate summary of recognized categories ======

		System.out.println(ontologyService.toString());

		for (ClOntologyCategory ontologyCategory: mapOntologyCategories.keySet()) {
			Set<String> setText = mapOntologyCategories.get(ontologyCategory);
			List<Object> listObjects = mapOntologyObjects.get(ontologyCategory.getIdCategory());
			addToSummary(ontologyCategory.getIdCategory(), setText, listObjects);
		}				


		// ===== Display =====
		System.out.println("\n============= REPORT =============");
		System.out.println(gseNumber);

		System.out.println("\nNot recognized values: ");
		System.out.println("----------------------");
		displayMap(mapNotRecognized); 


		System.out.println();
		System.out.println("\nRecognized values with several choices: ");
		System.out.println("---------------------------------------");
		displayMap(mapRecognizedSeveral); 


		System.out.println();
		System.out.println("\nRecognized values: ");
		System.out.println("------------------");
		displayMap(mapRecognized); 

	}

	/** ====================================================================================== */

	public void addToSummary(String category, Set<String> setText, List<Object> listObjects) {

		if (listObjects==null || listObjects.isEmpty()) {
			// Text was not automatically recognized
			addToMap(mapNotRecognized, category, setText.toString());
		}
		else {
			if (listObjects.size()==1) {
				// Recognition successful
				addToMap(mapRecognized, category, setText.toString() + " ---> " + listObjects.toString());
			}
			if (listObjects.size()>1) {
				// Recognition successful but several options are possible
				addToMap(mapRecognizedSeveral, category, setText.toString() + " ---> " + listObjects.toString());
			}
		}
	}

	/** ====================================================================================== */

	private void addToMap(Map <String, Set<String>> map, String category, String text) {
		if (map.get(category) == null); {
			map.put(category, new HashSet<String>());
		}
		map.get(category).add(text);
	}

	/** ====================================================================================== */

	@SuppressWarnings("rawtypes")
	public void displayMap(Map <String, Set<String>> map) {

		for (String category: map.keySet()) {

			Set<String> set = map.get(category);

			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				System.out.println("  - " + category + " " + iter.next());
			}
		}
	}

	/** ======================================================================================*/

	public List<String> splitEntries(List<String> listEntries) {
		List<String> listSplittedEntries = new ArrayList<String>();

		for (String line : listEntries) {
			String [] lines =  line.split("[;,]");
			for (int l=0; l<lines.length; l++) {
				listSplittedEntries.add(lines[l].trim());
			}
		}

		return listSplittedEntries;
	}


	/** =============================================================== */

	private void clear(Document doc) {
		doc.put("id_pathology", null);
		doc.put("pathology", null);
		doc.put("collection_method", null);
		doc.put("id_topology", null);
		doc.put("topology", null);
		doc.put("id_topology_group", null);
		doc.put("topology_group", null);
		doc.put("id_morphology", null);
		doc.put("morphology", null);
		doc.put("histology_type", null);
		doc.put("histology_subtype", null);
		doc.put("t", null);
		doc.put("n", null);
		doc.put("m", null);
		doc.put("tnm_stage", null);
		doc.put("tnm_grade", null);
		doc.put("dfs_months", null);
		doc.put("os_months", null);
		doc.put("relapsed", null);
		doc.put("dead", null);
		doc.put("treatment", null);
		doc.put("exposure", null);
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new AnalyseGeo();
	}

	/** ============================================================== */

}
