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
import model.entity.ClMorphology;
import model.entity.ClPathology;
import model.entity.ClTissueStatus;
import model.entity.ClTopology;

public class UpdateSample {

	private String gseNumber = "GSE58984";
	private boolean commit = true;


	public UpdateSample() {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== INIT =====
		ClTopology topology = session.get(ClTopology.class, "C50.9"); // Breast
		ClMorphology morphology = session.get(ClMorphology.class, "8000/3"); // Neoplasm malignant
		ClTissueStatus status = session.get(ClTissueStatus.class, 3); // Pathological tumoral 
		ClPathology pathology = session.get(ClPathology.class, "C80.9"); // Cancer

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		List<Document> listDocuments = collection
				.find(Filters.in("series", gseNumber))
				// .find(Filters.and(Filters.in("series", gseNumber), Filters.eq("analyzed", false)))
				// .find(Filters.exists("exp_group.triple_negative"))
				.into(new ArrayList<Document>());


		for (int i=0; i<listDocuments.size(); i++) {
			Document doc = listDocuments.get(i);
			Document expgroup = (Document) doc.get("exp_group");
			Document parameters = (Document) doc.get("parameters");

			Integer relapsed = parameters.getInteger("dfs");
			String osDays = parameters.getString("time overall survival");

			if (relapsed!=null) {
				expgroup.put("relapsed", relapsed.equals(1) ? true : false);
			}
	
			if (osDays!=null) {
				Double value =  Double.parseDouble(osDays)/30;
				Double osMonths  =	Math.round(value * Math.pow(10, 2)) / Math.pow(10, 2);
				expgroup.put("os_months", osMonths);
			}

				expgroup.put("dfs_months", null);
			

				// expgroup.put("id_topology", topology.getIdTopology());
				// expgroup.put("topology", topology.getName());
				// expgroup.put("id_topology", topology.getIdTopology());

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
			new UpdateSample();
		}

		/** ============================================================== */


	}
