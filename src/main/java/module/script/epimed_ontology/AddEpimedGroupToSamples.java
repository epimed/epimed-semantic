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
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.HibernateUtil;
import config.MongoUtil;
import dao.ClTopologyDao;
import model.entity.ClEpimedGroup;
import model.entity.ClTopology;


public class AddEpimedGroupToSamples {


	public AddEpimedGroupToSamples () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();
		ClTopologyDao topologyDao = new ClTopologyDao(session);


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSample = db.getCollection("sample");


		List<Document> samples = collectionSample.find().into(new ArrayList<Document>());

		for (int i=0; i<samples.size(); i++) {
			Document sample = samples.get(i);
			Document expgroup = sample.get("exp_group", Document.class);

			String idTopology = expgroup.getString("id_topology");

			if (idTopology!=null && !idTopology.isEmpty()) {

				ClTopology topology = topologyDao.find(idTopology);
				ClEpimedGroup grp1 = topology.getClEpimedGroup();
				ClEpimedGroup grp2 = grp1.getParent();
				ClEpimedGroup grp3 = grp2.getParent();
				expgroup.append("tissue_group_level1", grp1.getName());
				expgroup.append("tissue_group_level2", grp2.getName());
				expgroup.append("tissue_group_level3", grp3.getName());

				System.out.println((i+1) + "/" + samples.size() + " " + expgroup);

				sample.append("exp_group", expgroup);
				collectionSample.updateOne(Filters.eq("_id", sample.getString("_id")), new Document("$set", sample));
			}

		}


		// === Commit transaction ===
		// session.getTransaction().commit();
		session.getTransaction().rollback();


		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}


	/** =============================================================== */

	public static void main(String[] args) {
		new AddEpimedGroupToSamples();
	}

	/** ============================================================== */

}
