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
import java.util.List;
import java.util.Locale;

public class NcbiGeoGpl extends NcbiGeo {

	private String manufacturer;
	private String taxid;


	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);

	public NcbiGeoGpl() {
		super();
	}

	public NcbiGeoGpl(List<String> data) {
		this.bind(data);
	}


	private void bind(List<String> data) {
		for (int i=0; i<data.size(); i++) {
			String line = data.get(i);

			// Organism
			if (line.startsWith("!Platform_organism")) {
				this.organism = this.extractValue(line);
			}

			// Tax id (id_organism)
			if (line.startsWith("!Platform_taxid")) {
				this.taxid = this.extractValue(line);
			}

			// Title
			if (line.startsWith("!Platform_title")) {
				this.title = line.split(sep)[1].trim();
			}

			// Gpl number
			if (line.startsWith("!Platform_geo_accession")) {
				this.gplNumber = line.split(sep)[1].trim();
			}

			// Manufacturer
			if (line.startsWith("!Platform_manufacturer")) {
				this.manufacturer = line.split(sep)[1].trim();
			}


			// Submission date
			if (line.startsWith("!Platform_submission_date")) {
				try {
					this.submissionDate = dateFormat.parse(line.split(sep)[1].trim());
				} catch (ParseException e) {
					// nothing to do
				}
			}

			// Last update
			if (line.startsWith("!Platform_last_update_date")) {
				try {
					this.lastUpdate = dateFormat.parse(line.split(sep)[1].trim());
				} catch (ParseException e) {
					// nothing to do
				}
			}

		}
	}



	public String getManufacturer() {
		return manufacturer;
	}
	
	

	public String getTaxid() {
		return taxid;
	}

	@Override
	public String toString() {
		return "NcbiGeoGpl [manufacturer=" + manufacturer + ", taxid=" + taxid + ", title=" + title
				+ ", submissionDate=" + submissionDate + ", lastUpdate=" + lastUpdate + ", gplNumber=" + gplNumber
				+ ", organism=" + organism + "]";
	}

	
	

	


}
