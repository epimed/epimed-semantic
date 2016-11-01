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
 * Author: Ekaterina Flin 
 *
 */
package model.bind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class NcbiGeo {
	
	protected static String sep = " = ";
	
	protected String title;
	protected Date submissionDate;
	protected Date lastUpdate;
	protected String gplNumber;
	protected List<String> listFiles = new ArrayList<String>();
	protected String organism;
	
	public String getOrganism() {
		return organism;
	}

	public String getTitle() {
		return title;
	}
	public Date getSubmissionDate() {
		return submissionDate;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public String getGplNumber() {
		return gplNumber;
	}
	public List<String> getListFiles() {
		return listFiles;
	}
	
	/** =================================================================== */
	
	public String extractValue(String line) {

		String result = "";
		String[] parts = line.split(sep);

		if (parts.length>1) {
			result = parts[1].trim();
			for (int i=2; i<parts.length; i++) {
				result = result + sep + parts[i].trim();
			}
		}

		return result;
	}
	
	/** =================================================================== */

	
}
