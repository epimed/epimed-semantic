package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseService {

	protected StringBuilder text = new StringBuilder();
	protected List<Object> header = new ArrayList<Object>();
	protected Map<String, Integer> headerMap = new HashMap<String, Integer>();
	protected List<List<Object>> data = new ArrayList<List<Object>>();

	/** ================================================================================= */

	public BaseService(String inputfile) {
		loadText(inputfile);
	}

	/** ================================================================================= */

	public StringBuilder getText() {
		return text;
	}

	public List<Object> getHeader() {
		return header;
	}


	public void setHeader(List<Object> header) {
		this.header = header;
	}


	public Map<String, Integer> getHeaderMap() {
		return headerMap;
	}


	public void setHeaderMap(Map<String, Integer> headerMap) {
		this.headerMap = headerMap;
	}


	public List<List<Object>> getData() {
		return data;
	}


	public void setData(List<List<Object>> data) {
		this.data = data;
	}


	public void setText(StringBuilder text) {
		this.text = text;
	}

	/** ================================================================================= */

	public void writeData(FileWriter writer, String[] data) throws IOException {

		for (int i=0; i<data.length; i++) {
			writer.append(data[i]!=null ? data[i].replaceAll(";", "") : "");
			if (i<(data.length-1)) {
				writer.append(";");
			}
		}
		writer.append('\n');
	}
	
	/** ================================================================================= */

	public void loadText(String inputfile) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(inputfile));
			String line = br.readLine();
			while (line != null) {
				text.append(line.trim());
				text.append(System.lineSeparator());
				line = br.readLine();
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** ================================================================================= */

	/**
	 * Fill a Map structure (headerMap) with the following information:
	 * <Name of the column in Excel file, index of this column>
	 */
	protected void fillHeaderMap() {

		for (int i=0; i<header.size(); i++) {
			headerMap.put(header.get(i).toString(), i);
		}
	}

	/** ================================================================================= */

	/**
	 * Extract the column corresponding to a given index
	 * @param columnIndex
	 * @return
	 */
	public List<Object> extractColumn(Integer columnIndex) {

		List<Object> column = new ArrayList<Object> ();
		if (header.size()<=columnIndex);
		for (int i=0; i<data.size(); i++) {
			column.add(data.get(i).get(columnIndex));
		}
		return column;
	}

}
