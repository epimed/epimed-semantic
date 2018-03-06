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
package module.script.epimed_ontology;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.HibernateUtil;
import config.MongoUtil;
import dao.ClTopologyDao;
import model.entity.ClTopology;


public class UpdateFetalAdultOvary {


	public UpdateFetalAdultOvary () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();
		ClTopologyDao topologyDao = new ClTopologyDao(session);
		ClTopology adultOvary = topologyDao.find("C56.9");
		ClTopology fetalOvary = topologyDao.find("E56.9");


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSample = db.getCollection("sample");

		Bson filters = Filters.and(
				Filters.eq("exp_group.id_topology", "C56.9"), // ovary
				Filters.eq("exp_group.id_tissue_stage", 1) // adult
				);

		List<Document> samples = collectionSample.find(filters).into(new ArrayList<Document>());

		for (Document sample: samples) {
			Document expgroup = sample.get("exp_group", Document.class);
			expgroup.append("id_topology", adultOvary.getIdTopology());
			expgroup.append("topology", adultOvary.getName());
			sample.append("exp_group", expgroup);
			collectionSample.updateOne(Filters.eq("_id", sample.getString("_id")), new Document("$set", sample));
		}
		System.out.println(samples.size());

		// === Commit transaction ===
		// session.getTransaction().commit();
		session.getTransaction().rollback();


		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}


	/** =============================================================== */

	public static void main(String[] args) {
		new UpdateFetalAdultOvary();
	}

	/** ============================================================== */

}
