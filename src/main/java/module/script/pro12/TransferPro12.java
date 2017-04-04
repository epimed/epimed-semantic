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
package module.script.pro12;

import java.util.List;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.HibernateUtil;
import config.MongoUtil;
import module.BaseModule;

public class TransferPro12 extends BaseModule {

	@SuppressWarnings({ "unchecked" })
	public TransferPro12 () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		String sql = "select id_sample from epimed_prod.om_sample join epimed_prod.om_sample_series using (id_sample) "
				+ "join epimed_prod.om_series using (id_series) where id_series='PRO12'";

		List<String> list = session.createSQLQuery(sql).list();


		Document pro12 = new Document();
		pro12.append("series", "PRO12");

		for (String gsmNumber : list) {


			Document doc = collection.find(Filters.eq("_id", gsmNumber)).first();

			System.out.println("-----------------------------");
			System.out.println(gsmNumber + " " + doc);

			if (doc!=null) {
				// Update Mongo document
				collection.updateOne(Filters.eq("_id", gsmNumber), new Document("$push", pro12));
			}

		}

		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new TransferPro12();
	}

	/** ============================================================== */

}
