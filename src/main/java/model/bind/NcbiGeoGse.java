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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NcbiGeoGse extends NcbiGeo {

	private String gseNumber;
	private List<String> listGsm = new ArrayList<String>();
	private List<String> listGpl = new ArrayList<String>();

	private static String sep = " = ";

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);

	public NcbiGeoGse() {
		super();
	}

	public NcbiGeoGse(List<String> data) {
		this.bind(data);
	}


	private void bind(List<String> data) {
		for (int i=0; i<data.size(); i++) {
			String line = data.get(i);

			// Organism
			if (line.startsWith("!Series_sample_organism")) {
				this.organism = line.split(sep)[1].trim();
			}
			
			// Title
			if (line.startsWith("!Series_title")) {
				this.title = line.split(sep)[1].trim();
			}

			// Gse number
			if (line.startsWith("!Series_geo_accession")) {
				this.gseNumber = line.split(sep)[1].trim();
			}

			// List of GSM
			if (line.startsWith("!Series_sample_id")) {
				this.listGsm.add(line.split(sep)[1].trim());
			}

			// Submission date
			if (line.startsWith("!Series_submission_date")) {
				try {
					this.submissionDate = dateFormat.parse(line.split(sep)[1].trim());
				} catch (ParseException e) {
					// nothing to do
				}
			}

			// Last update
			if (line.startsWith("!Series_last_update_date")) {
				try {
					this.lastUpdate = dateFormat.parse(line.split(sep)[1].trim());
				} catch (ParseException e) {
					// nothing to do
				}
			}

			// GPL numbers
			if (line.startsWith("!Series_platform_id")) {
				this.listGpl.add(line.split(sep)[1].trim());
				this.gplNumber = line.split(sep)[1].trim();
			}

			// List of files
			if (line.startsWith("!Series_supplementary_file")) {
				this.listFiles.add(line.split(sep)[1].trim());
			}

		}
	}


	public String getGseNumber() {
		return gseNumber;
	}

	public List<String> getListGsm() {
		return listGsm;
	}

	public List<String> getListGpl() {
		return listGpl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gseNumber == null) ? 0 : gseNumber.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NcbiGeoGse other = (NcbiGeoGse) obj;
		if (gseNumber == null) {
			if (other.gseNumber != null)
				return false;
		} else if (!gseNumber.equals(other.gseNumber))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "NcbiGeoGse [title=" + title + ", gseNumber=" + gseNumber + ", listGsm=" + listGsm + ", submissionDate="
				+ submissionDate + ", lastUpdate=" + lastUpdate + ", gplNumber=" + gplNumber + ", listFiles="
				+ listFiles + "]";
	}




}
