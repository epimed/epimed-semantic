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

public class TransferExposure extends BaseModule {

	@SuppressWarnings({ "unused", "unchecked" })
	public TransferExposure () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		String sql = "select id_sample, main_gse_number, string_agg(id_substance, ', ') as list_substances from epimed_prod.om_sample " 
				+ "join epimed_prod.cl_biopatho using (id_biopatho) join epimed_prod.cl_patient using (id_patient) join epimed_prod.cl_exposure using (id_patient) "
				+ "where exposed=true group by id_sample";
		
		List<Object> list = session.createSQLQuery(sql).setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list();

		
				
		for (Object item : list) {

			Map<String, Object> map = (HashMap <String, Object>) item;

			String gsmNumber =  (String) map.get("id_sample");
			String gseNumber =  (String) map.get("main_gse_number");
			
			System.out.println("-----------------------------");
			System.out.println(gseNumber + " " + gsmNumber);
			
			Document doc = collection.find(Filters.eq("_id", gsmNumber)).first();

			if (doc!=null) {
				Document expGroup = (Document) doc.get("exp_group");
				expGroup.put("exposure", map.get("list_substances"));
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
		new TransferExposure();
	}

	/** ============================================================== */

}
