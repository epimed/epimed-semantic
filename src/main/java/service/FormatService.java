package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

public class FormatService {

	public FormatService() {
		super();
	}
	
	/** ================================================================================ */

	public double round(double value, int decimals) {
		double precision = Math.pow(10, decimals);
		return (double) Math.round(value * precision) / precision;
	}


	/** ================================================================================= */

	public List<String> splitInArray(String line, String separator) {
		List<String> list = null;

		if (line!=null && !line.isEmpty()) {
			list = new ArrayList<String>();
			String[] parts = line.split(separator);
			for (String part : parts) {
				String value = part.trim();
				if (value!=null && !value.isEmpty()) {
					list.add(value);
				}
			}
		}

		return list;
	}

	/** ================================================================================= */

	public List<Object> convertHomogeneousMongoDocuments (List<Document> listDocuments) {

		List<Object> data = new ArrayList<Object>();
		List<String> header = new ArrayList<String>();

		try  {

			// ===== Extract header =====
			header.addAll(listDocuments.get(0).keySet());

			// ===== Extract data =====
			for (Document doc : listDocuments) {
				Object [] dataLine = new Object [header.size()];

				for (int j=0; j<header.size(); j++) {
					dataLine[j] = (Object) doc.get(header.get(j));
				}
				data.add(dataLine);
			}
		}
		catch (Exception e) {
			// e.printStackTrace();
		}

		return data;

	}

	/** ================================================================================= */


	public List<Object> convertHeterogeneousMongoDocuments (List<Document> listDocuments) {

		List<Object> data = new ArrayList<Object>();
		Set<String> headerSet = new HashSet<String>();
		List<String> header = new ArrayList<String>();

		try  {

			// ===== Extract header =====
			for (Document doc : listDocuments) {
				headerSet.addAll(doc.keySet());
			}
			header.addAll(headerSet);
			Collections.sort(header);


			// ===== Extract data =====
			for (Document doc : listDocuments) {

				Object [] dataLine = new Object [header.size()];

				for (int j=0; j<header.size(); j++) {
					dataLine[j] = (Object) doc.get(header.get(j));
				}
				data.add(dataLine);
			}
		}
		catch (Exception e) {
			// e.printStackTrace();
		}

		return data;

	}

	/** ================================================================================= */

	public List<String> extractHeader (List<Document> listDocuments, String rootDocumentName) {


		List<String> header = new ArrayList<String>();

		try  {


			// ===== Extract header =====

			for (Document doc : listDocuments) {

				if (rootDocumentName!=null) {
					doc = (Document) doc.get(rootDocumentName);
				}

				for (String key : doc.keySet()) {
					if (!header.contains(key)) {
						header.add(key);
					}
				}
			}
		}
		catch (Exception e) {
			// e.printStackTrace();
		}

		return header;
	}

	/** ================================================================================= */

	public List<Object> extractData (List<Document> listDocuments, List<String> header, String rootName) {


		List<Object> data = new ArrayList<Object>();

		try  {


			// ===== Extract data =====
			for (Document doc : listDocuments) {

				if (rootName!=null) {
					doc = (Document) doc.get(rootName);
				}

				Object [] dataLine = new Object [header.size()];

				for (int j=0; j<header.size(); j++) {
					dataLine[j] = (Object) doc.get(header.get(j));
				}
				data.add(dataLine);
			}
		}
		catch (Exception e) {
			// e.printStackTrace();
		}

		return data;

	}

	/** ================================================================================= */
}
