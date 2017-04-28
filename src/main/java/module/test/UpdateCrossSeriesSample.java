package module.test;

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
import com.mongodb.client.result.UpdateResult;

import config.HibernateUtil;
import config.MongoUtil;
import model.entity.ClTopology;

public class UpdateCrossSeriesSample {

	private boolean commit = true;


	public UpdateCrossSeriesSample() {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== INIT =====
		ClTopology topo = session.get(ClTopology.class, "C71.9"); // Brain

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collection = db.getCollection("samples");

		Bson filters = Filters.eq("exp_group.id_topology_group", "C71");
		
		List<Document> listDocuments = collection
				.find(filters)
				.into(new ArrayList<Document>());


		for (int i=0; i<listDocuments.size(); i++) {

			Document doc = listDocuments.get(i);

			Document expgroup = (Document) doc.get("exp_group");

			expgroup.put("id_topology", topo.getIdTopology());
			expgroup.put("topology", topo.getName());
			expgroup.put("id_topology_group", topo.getClTopologyGroup().getIdGroup());
			expgroup.put("topology_group", topo.getClTopologyGroup().getName());


			doc.append("exp_group", expgroup);

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
		new UpdateCrossSeriesSample();
	}

	/** ============================================================== */


}
