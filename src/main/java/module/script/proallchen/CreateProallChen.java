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
package module.script.proallchen;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.HibernateUtil;
import config.MongoUtil;
import module.BaseModule;

public class CreateProallChen extends BaseModule {

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	

	public CreateProallChen () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		// MongoDatabase db = mongoClient.getDatabase("geo");

		MongoCollection<Document> collectionSeries = db.getCollection("series");

		
		Date submissionDate=null;
		try {
			submissionDate = dateFormat.parse("2016-05-13");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// List<String> platforms = new ArrayList<String>();
		// platforms.add("GPL570");
		
		Document docSeries = new Document();
		docSeries
		.append("_id", "PROALL_CHEN")
		.append("title", "Genomic Profiling of Adult and Pediatric B-cell Acute Lymphoblastic Leukemia")
		.append("platforms", null)
		.append("submission_date", submissionDate)
		.append("last_update", submissionDate)
		.append("import_date", new Date())
		;

		System.out.println(docSeries);
		collectionSeries.insertOne(docSeries);
		
		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new CreateProallChen();
	}

	/** ============================================================== */

}
