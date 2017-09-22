package module;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;


/*
 * ATTENTION aux samples appartenant à plusierus études à la fois.
 * Ils seront aussi supprimes !!!
 */

public class DeleteSeriesWithAllSamples {

	// private String  []  listSeries = {"E-MTAB-3827", "E-MTAB-3871", "E-MTAB-2836", "E-MTAB-2919", "E-MTAB-4344", "E-MTAB-5214", "E-MTAB-513"};

	private String  []  listSeries = {"E-MTAB-1733"};

	
	public DeleteSeriesWithAllSamples() {

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collectionSamples = db.getCollection("samples");
		MongoCollection<Document> collectionSeries = db.getCollection("series");

		for (String idSeries : listSeries) {
			Bson filters = Filters.and(
					Filters.in("series", idSeries)
					);
			collectionSamples.deleteMany(filters);
			collectionSeries.deleteOne(Filters.eq("_id", idSeries));
		}
		
		mongoClient.close();

	}

	public static void main(String[] args) {
		new DeleteSeriesWithAllSamples();
	}

}
