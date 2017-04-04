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
package module.script.emtab365;

import java.util.ArrayList;
import java.util.List;

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
import model.entity.ClMorphology;
import model.entity.ClTopology;
import module.BaseModule;

public class UpdateSamplesEMTAB365 extends BaseModule {

	private String gseNumber = "E-MTAB-365";
	private boolean commit = true;

	public UpdateSamplesEMTAB365 () {

		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== INIT =====

		ClMorphology ductal = session.get(ClMorphology.class, "8500/3"); // 8500/3	Infiltrating duct carcinoma, NOS (C50._)
		ClMorphology lobular = session.get(ClMorphology.class, "8520/3"); // 8520/3	Lobular carcinoma, NOS (C50._)
		ClMorphology morphology = session.get(ClMorphology.class, "8010/3"); // Carcinoma

		ClTopology breast = session.get(ClTopology.class, "C50.9"); // Breast
		ClTopology blood = session.get(ClTopology.class, "C42.0"); // Blood
		ClTopology lymphNode = session.get(ClTopology.class, "C77.9"); // Lymph node


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		List<Document> listDocuments = collection
				.find(Filters.in("series", gseNumber))
				.into(new ArrayList<Document>());

		for (int i=0; i<listDocuments.size(); i++) {
			Document doc = listDocuments.get(i);
			Document expgroup = (Document) doc.get("exp_group");
			Document parameters = (Document) doc.get("parameters");

			String histoType = parameters.getString("Histology");
			String histoSubtype = parameters.getString("CIT classification");

			expgroup.put("histology_subtype", histoSubtype);

			if (histoType!=null && histoType.toLowerCase().equals("lobular")) {
				morphology = lobular;
			}
			if (histoType!=null && histoType.toLowerCase().equals("ductal")) {
				morphology = ductal;
			}

			expgroup.put("id_morphology", morphology.getIdMorphology());
			expgroup.put("morphology", morphology.getName());

			expgroup.put("sample_source", parameters.getString("Source Name"));

			String organismPart = parameters.getString("OrgansimPart");

			ClTopology topology = null;
			if (organismPart!=null) {

				if (organismPart.toLowerCase().contains("breast")) {
					topology = breast;
				}

				if (organismPart.toLowerCase().contains("blood")) {
					topology = blood;
				}
				if (organismPart.toLowerCase().contains("lymph")) {
					topology = lymphNode;
				}

			}
			else {
				topology = breast;
			}

			expgroup.put("id_topology", topology.getIdTopology());
			expgroup.put("topology", topology.getName());
			expgroup.put("id_topology_group", topology.getClTopologyGroup().getIdGroup());
			expgroup.put("topology_group", topology.getClTopologyGroup().getName());

			// ==== Survival =====
			
			Object dfs_months = parameters.get("Delay Metastasis Free Survival months");
			if (dfs_months!=null) {
				expgroup.put("dfs_months", dfs_months);
			}
			
			Object os_months = parameters.get("Delay Overall Survival months");
			if (os_months!=null) {
				expgroup.put("os_months", os_months);
			}

			
			
			Double os = (Double) expgroup.get("os_months");
			Double dfs = (Double) expgroup.get("dfs_months");
			if (os!=null && dfs!=null && dfs.equals(os)) {
				expgroup.put("relapsed", false);
			}
			
			if (os!=null && dfs!=null && dfs<os) {
				expgroup.put("relapsed", true);
			}
			
			if (os!=null && dfs!=null && dfs>os) {
				expgroup.put("relapsed", null);
			}
			
			
			Object relapseDate = parameters.get("Relapse  Metastasis Date");
			if (relapseDate!=null) {
				expgroup.put("relapsed", true);
			}
			
			
			// ==== Grade ====
			expgroup.put("tnm_grade", parameters.get("Grade  Scarff Bloom Richardson"));

			// ==== Files =====

			expgroup.put("ftp", parameters.getString("ArrayExpress FTP file"));
			expgroup.put("file_name", parameters.getString("Array Data File"));

			expgroup.remove("individual");
			if (parameters.getString("Individual")!=null) {
				expgroup.put("individual", parameters.getString("Individual"));
			}

			// ==== Biomarkers ====
			/*
			String p53 = parameters.getString("Phenotype - TP53  Gene mutation  Status");
			expgroup.put("p53", value)

			String pr = parameters.getString("PGR  Protein expression");
			String er = parameters.getString("ESR1  Protein expression");
			String her2 = parameters.getString("ERBB2  Protein expression");
			 */



			doc.put("exp_group", expgroup);

			System.out.println(i + " " + doc.get("_id") + " " + doc.get("analyzed") + " " + expgroup);

			if (commit) {
				UpdateResult updateResult = collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
			}
		}

		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}



	/** =============================================================== */

	public static void main(String[] args) {
		new UpdateSamplesEMTAB365();
	}

	/** ============================================================== */

}
