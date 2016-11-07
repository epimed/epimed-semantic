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
package module.script;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import config.HibernateUtil;
import config.MongoUtil;
import module.BaseModule;

public class CreatePro12 extends BaseModule {

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@SuppressWarnings({ "unchecked" })
	public CreatePro12 () {


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
			submissionDate = dateFormat.parse("2010-01-01");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> platforms = new ArrayList<String>();
		platforms.add("GPL570");
		
		Document docSeries = new Document();
		docSeries
		.append("_id", "PRO12")
		.append("title", "Project 12: Reference tissues and cell lines")
		.append("platforms", platforms)
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
		new CreatePro12();
	}

	/** ============================================================== */

}
