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
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.HibernateUtil;
import config.MongoUtil;
import model.entity.ClPathology;
import model.entity.ClTissueStatus;

public class TransferFromEpimedProd extends BaseModule {

	@SuppressWarnings({ "unused", "unchecked" })
	public TransferFromEpimedProd () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		String sql = "select * from epimed_prod.view_exp_group order by main_gse_number, id_sample";
		List<Object> list = session.createSQLQuery(sql).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();

		String [] commonAttributes = {"pathology", "tnm_stage", "id_topology_group", "histology_subtype", "tissue_stage", "dead",
				"dfs_months", "age_max", "morphology", "id_tissue_stage", "topology", "sex", "age_min", "m", "topology_group", "n",
				"t", "collection_method", "id_morphology", "relapsed", "histology_type", "os_months", "id_topology"};


		List<ClPathology> listPathology = session.createCriteria(ClPathology.class).list();
		Map<String, ClPathology> mapPathology = new HashMap<String, ClPathology>();
		for (ClPathology p : listPathology) {
			mapPathology.put(p.getName(), p);
		}
		
		
		List<ClTissueStatus> listTissueStatus = session.createCriteria(ClTissueStatus.class).list();
		Map<Integer, ClTissueStatus> mapTissueStatus = new HashMap<Integer, ClTissueStatus>();
		for (ClTissueStatus t : listTissueStatus) {
			mapTissueStatus.put(t.getIdTissueStatus(), t);
		}
		
		
		System.out.println(listTissueStatus);
				
		for (Object item : list) {

			Map<String, Object> map = (HashMap <String, Object>) item;

			String gsmNumber =  (String) map.get("id_sample");
			String gseNumber =  (String) map.get("main_gse_number");
			
			System.out.println("-----------------------------");
			System.out.println(gseNumber + " " + gsmNumber);
			
			Document doc = collection.find(Filters.eq("_id", gsmNumber)).first();

			if (doc!=null) {
				Document expGroup = (Document) doc.get("exp_group");
				for (int j=0; j<commonAttributes.length; j++) {
					String attr = commonAttributes[j];
					expGroup.put(attr, map.get(attr));
				}
				
				// Treatment
				expGroup.put("treatment", map.get("treatment_type"));
				
				// Pathology
				String pathoString = (String) map.get("pathology");
				ClPathology pathology = mapPathology.get(pathoString);
				if (pathology!=null) {
					expGroup.put("id_pathology", pathology.getIdPathology());
					expGroup.put("pathology", pathology.getName());
				}
				
				// Tissue status
				Integer idTissueStatus = (Integer) map.get("id_tissue_status");
				if (idTissueStatus!=null && idTissueStatus>3) {
					idTissueStatus = 3;
				}
				ClTissueStatus tissueStatus = mapTissueStatus.get(idTissueStatus);
				if (tissueStatus!=null) {
					expGroup.put("id_tissue_status", tissueStatus.getIdTissueStatus());
					expGroup.put("tissue_status", tissueStatus.getName());
				}
				
				System.out.println("idTissueStatus=" + tissueStatus + ", pathology=" + pathology);
				System.out.println(expGroup);
				
				// Update Mongo document
				doc.put("exp_group", expGroup);
				doc.put("analyzed", true);
				UpdateResult updateResult = collection.updateOne(Filters.eq("_id", gsmNumber), new Document("$set", doc));
			}
			
		}

		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new TransferFromEpimedProd();
	}

	/** ============================================================== */

}
