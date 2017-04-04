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
package module.script.probcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.HibernateUtil;
import config.MongoUtil;
import model.entity.ClMorphology;
import model.entity.ClTopology;
import module.BaseModule;

@SuppressWarnings("unchecked")
public class ImportSamplesProbcp extends BaseModule {

	private Date today = new Date();

	public ImportSamplesProbcp () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");

		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		String[] studies = {"tya16", "law15"};
		List<String> series = new ArrayList<String>();

		for (int l=0; l<studies.length; l++) {

			String idStudy = studies[l];
			String studyName = idStudy.toUpperCase();

			series.clear();
			series.add(studyName);
			series.add("PROBCP");

			String sql = "select * from st_bcp." + idStudy + "_sample order by id_sample";

			List<Object> listSamples = session.createSQLQuery(sql).list();

			for (int i=0; i<listSamples.size(); i++) {
				
				Object[] lineSample = (Object[]) listSamples.get(i);
				
				String idSample = (String) lineSample[0];
				String clinicalClassification = (String) lineSample[1];
				String tnmStage = (String) lineSample[2];
				Integer grade = (Integer) lineSample[3];
				String type = (String) lineSample[4];
				
				System.out.println(Arrays.toString(lineSample));
				
				
				// ===== Collection method ====
				String collectionMethod = "biopsy";
				if (idStudy.equals("law15") && !idSample.startsWith("Tumor")) {
					collectionMethod = "cell line";
				}
				
				// ==== Topology ====
				ClTopology topology = session.get(ClTopology.class, "C50.9");
				
				// === Morphology ===
				ClMorphology morphology = session.get(ClMorphology.class, "8010/3"); // carcinoma
				ClMorphology idc = session.get(ClMorphology.class, "8500/3"); // inf. duct. carcinoma
				ClMorphology lo = session.get(ClMorphology.class, "8520/3"); // lobular carcinoma
				ClMorphology ac = session.get(ClMorphology.class, "8140/3"); // adenocarcinoma
				
				if (type!=null && (type.contains("IDC") || type.contains("DC") || type.contains("ductal"))) {
					morphology = idc;
				}
				if (type!=null && type.contains("Lo")) {
					morphology = lo;
				}
				
				if (type!=null && (type.contains("AC") || type.contains("adeno"))) {
					morphology = ac;
				}
				
				// ===== Sample Document =====

				Document docSample = new Document();

				docSample
				.append("_id", studyName + "_" + idSample)
				.append("main_gse_number", studyName)
				.append("series", series)
				.append("organism", "Homo sapiens")
				.append("submission_date", today)
				.append("last_update", today)
				.append("import_date", today)
				.append("analyzed", true)
				;

				// ===== Mandatory parameters =====

				Document expGroup = this.generateExpGroup(idSample, studyName, tnmStage, grade, type, collectionMethod, topology, morphology) ;
				docSample.append("exp_group", expGroup);

				// ===== Supplementary parameters =====

				Document parameters = this.generateParameters(idSample);
				docSample.append("parameters", parameters);
				parameters.append("clinical_classification", clinicalClassification);
				parameters.append("tnm_stage", tnmStage);
				parameters.append("grade", grade);
				parameters.append("type", type);
				
				
				// === Append parameters to document ===
				
				
				docSample.append("parameters", parameters);
				
				// === Save ===
				collectionSamples.insertOne(docSample);
				
				System.out.println(docSample);
				
			}

		}

		if (session.isOpen()) {session.close();}
		sessionFactory.close();
		
		mongoClient.close();	

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new ImportSamplesProbcp();
	}

	
	/** =============================================================== */


	public Document generateExpGroup(String idSample, String mainGse, String tnmStage, Integer grade, String type, String collectionMethod, 
			ClTopology topology, ClMorphology morphology) {


		Document expGroup = new Document();

		expGroup
		.append("id_sample", idSample)
		.append("main_gse_number", mainGse)
		.append("id_platform", "proteomics")
		.append("sample_title", null)
		.append("sample_source", null)
		.append("sex", null)
		.append("ethnic_group", null)
		.append("age_min", null)
		.append("age_max", null)
		.append("id_tissue_stage", 1)
		.append("tissue_stage", "adult")
		.append("id_tissue_status", 3)
		.append("tissue_status", "Pathological tumoral")
		.append("id_pathology", "C80.9")
		.append("pathology", "Cancer")
		.append("collection_method", collectionMethod)
		.append("id_topology", topology.getIdTopology())
		.append("topology", topology.getName())
		.append("id_topology_group", topology.getClTopologyGroup().getIdGroup())
		.append("topology_group", topology.getClTopologyGroup().getName())
		.append("id_morphology", morphology.getIdMorphology())
		.append("morphology", morphology.getName())
		.append("histology_type", type)
		.append("histology_subtype", null)
		.append("t", null)
		.append("n", null)
		.append("m", null)
		.append("tnm_stage", tnmStage)
		.append("tnm_grade", grade)
		.append("dfs_months", null)
		.append("os_months", null)
		.append("relapsed", null)
		.append("dead", null)
		.append("treatment", null)
		.append("exposure", null)
		;

		return expGroup;
	}


	/** =============================================================== */

	public Document generateParameters(String idSample) {
		Document parameters = new Document();
		parameters.append("id_sample", idSample);
		;
		return parameters;
	}

	/** =============================================================== */

}
