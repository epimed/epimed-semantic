package module.test;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;

public class TestQuery {

	public TestQuery() {

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collectionSamples = db.getCollection("samples");
		MongoCollection<Document> collectionSeries = db.getCollection("series");
		MongoCollection<Document> collectionPlatforms = db.getCollection("platforms");
	

		
		List<Bson> filters = new ArrayList<Bson>();
		filters.add(Filters.eq("exp_group.id_platform", "GPL570"));
		filters.add(Filters.eq("exp_group.id_topology_group", "C50"));
		filters.add(Filters.eq("exp_group.id_tissue_status", 3)); // tumoral
		Bson filter = Filters.and(filters);
		
		
		List<Document> list = collectionSamples
				.find(filter)
				.into(new ArrayList<Document>());
		
		for (Document doc : list) {
			System.out.println(doc);

		}

		mongoClient.close();

	}

	public static void main(String[] args) {
		new TestQuery();
	}

}
