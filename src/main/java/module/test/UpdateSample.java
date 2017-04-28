package module.test;

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
import model.entity.ClTopology;

public class UpdateSample {

	private String gseNumber = "E-MTAB-3716";
	private boolean commit = true;

	@SuppressWarnings("unchecked")
	public UpdateSample() {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== INIT =====
		ClTopology topo = session.get(ClTopology.class, "C58.9"); 


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collection = db.getCollection("samples");

		List<Document> listDocuments = collection
				.find(Filters.in("series", gseNumber))
				// .find(Filters.and(Filters.in("series", gseNumber), Filters.eq("analyzed", false)))
				.into(new ArrayList<Document>());


		for (int i=0; i<listDocuments.size(); i++) {
			Document doc = listDocuments.get(i);
			Document expgroup = (Document) doc.get("exp_group");
			Document parameters = (Document) doc.get("parameters");

			expgroup.put("id_platform", "GPL10999");

			/*
			expgroup.put("id_topology", topo.getIdTopology());
			expgroup.put("topology", topo.getName());
			expgroup.put("id_topology_group", topo.getClTopologyGroup().getIdGroup());
			expgroup.put("topology_group", topo.getClTopologyGroup().getName());
			expgroup.put("tnm_stage", null);
			 */

			doc.append("exp_group", expgroup);

			System.out.println(i + " " + doc.get("_id") + " " + doc.get("analyzed") + " " + expgroup);
			// System.out.println(i + " " + series);
			
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
		new UpdateSample();
	}

	/** ============================================================== */


}
