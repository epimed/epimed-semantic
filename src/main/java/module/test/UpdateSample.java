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

	private String gseNumber = "GSE10121";
	private boolean commit = true;


	public UpdateSample() {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();
		
		// ===== INIT =====
		ClTopology topology = session.get(ClTopology.class, "C06.9"); // Mouth
		ClMorphology morphology = session.get(ClMorphology.class, "8070/3"); // Squamous cell carcinoma, NOS
		ClTissueStatus status = session.get(ClTissueStatus.class, 3); // Pathological tumoral 
		ClPathology pathology = session.get(ClPathology.class, "C80.9"); // Cancer
		
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
			Document parameters = (Document) doc.get("parameters");
			Document expgroup = (Document) doc.get("exp_group");

			String source = expgroup.getString("sample_title");
			
			if (source.contains("Patient_H")) {
				System.out.println("Patient");
				expgroup.append("id_morphology", morphology.getIdMorphology());
				expgroup.append("morphology", morphology.getName());
				expgroup.append("id_tissue_status", status.getIdTissueStatus());
				expgroup.append("tissue_status", status.getName());
				expgroup.append("id_pathology", pathology.getIdPathology());
				expgroup.append("pathology", pathology.getName());
			}
			
			if (source.contains("Patient_MU")) {
				System.out.println("Control");
			}
			expgroup.append("id_topology", topology.getIdTopology());
			expgroup.append("topology", topology.getName());
			expgroup.append("id_topology_group", topology.getClTopologyGroup().getIdGroup());
			expgroup.append("topology_group", topology.getClTopologyGroup().getName());

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
