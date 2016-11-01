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

public class NcbiGeoGsm  extends NcbiGeo {

	private String gsmNumber;
	private String sourceName;
	private List<String> listCharacteristics = new ArrayList<String>();
	private String treatmentProtocol;
	private String growthProtocol;
	private String extractProtocol;
	private List<String> description = new ArrayList<String>();
	private List<String> listGse = new ArrayList<String>();

	// private static String sep = " = ";

	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);


	public NcbiGeoGsm() {
		super();
	}


	public NcbiGeoGsm(List<String> data) {
		this.bind(data);
	}


	private void bind(List<String> data) {
		for (int i=0; i<data.size(); i++) {
			String line = data.get(i);

			// Organism
			if (line.startsWith("!Sample_organism_ch1")) {
				this.organism = this.extractValue(line);
			}
			
			// Title
			if (line.startsWith("!Sample_title")) {
				this.title = this.extractValue(line);
			}

			// Gsm number
			if (line.startsWith("!Sample_geo_accession")) {
				this.gsmNumber = this.extractValue(line);
			}

			// Submission date
			if (line.startsWith("!Sample_submission_date")) {
				try {
					this.submissionDate = dateFormat.parse(this.extractValue(line));
				} catch (ParseException e) {
					// nothing to do
				}
			}

			// Last update
			if (line.startsWith("!Sample_last_update_date")) {
				try {
					this.lastUpdate = dateFormat.parse(this.extractValue(line));
				} catch (ParseException e) {
					// nothing to do
				}
			}

			// Source name
			if (line.startsWith("!Sample_source_name")) {
				this.sourceName = this.extractValue(line);
			}

			// Characteristics
			if (line.startsWith("!Sample_characteristics")) {
				this.listCharacteristics.add(this.extractValue(line));
			}

			// Description
			if (line.startsWith("!Sample_description")) {
				this.description.add(this.extractValue(line));
			}

			// Treatment protocol
			if (line.startsWith("!Sample_treatment_protocol")) {
				this.treatmentProtocol = this.extractValue(line);
			}

			// Growth protocol
			if (line.startsWith("!Sample_growth_protocol")) {
				this.growthProtocol = this.extractValue(line);
			}

			// Extract protocol
			if (line.startsWith("!Sample_extract_protocol")) {
				this.extractProtocol = this.extractValue(line);
			}

			// GPL number
			if (line.startsWith("!Sample_platform_id")) {
				this.gplNumber = this.extractValue(line);
			}

			// List of files
			if (line.startsWith("!Sample_supplementary_file")) {
				this.listFiles.add(this.extractValue(line));
			}

			// List of Gse
			if (line.startsWith("!Sample_series_id")) {
				this.listGse.add(this.extractValue(line));
			}
		}
	}




	public String getGsmNumber() {
		return gsmNumber;
	}


	public String getSourceName() {
		return sourceName;
	}


	public List<String> getListCharacteristics() {
		return listCharacteristics;
	}


	public String getExtractProtocol() {
		return extractProtocol;
	}



	public List<String> getDescription() {
		return description;
	}


	public List<String> getListGse() {
		return listGse;
	}


	public String getTreatmentProtocol() {
		return treatmentProtocol;
	}


	public String getGrowthProtocol() {
		return growthProtocol;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gsmNumber == null) ? 0 : gsmNumber.hashCode());
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
		NcbiGeoGsm other = (NcbiGeoGsm) obj;
		if (gsmNumber == null) {
			if (other.gsmNumber != null)
				return false;
		} else if (!gsmNumber.equals(other.gsmNumber))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "NcbiGeoGsm [gsmNumber=" + gsmNumber + ", sourceName=" + sourceName + ", listCharacteristics="
				+ listCharacteristics + ", extractProtocol=" + extractProtocol + ", description=" + description
				+ ", listGse=" + listGse + ", dateFormat=" + dateFormat + "]";
	}


	



}
