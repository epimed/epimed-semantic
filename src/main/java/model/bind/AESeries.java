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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AESeries {

	protected static String sep = "\t";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private String accession;
	private String title;
	private Date submissionDate;
	private String sdrf;
	private String protocol;
	private List<String> listAccessions = new ArrayList<String>();

	/** =================================================================== */

	public AESeries() {
		super();
	}

	public AESeries(List<String> data) {
		this.bind(data);
	}

	/** =================================================================== */

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getSdrf() {
		return sdrf;
	}

	public void setSdrf(String sdrf) {
		this.sdrf = sdrf;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	

	public List<String> getListAccessions() {
		return listAccessions;
	}

	public void setListAccessions(List<String> listAccessions) {
		this.listAccessions = listAccessions;
	}

	/** =================================================================== */

	public void bind(List<String> data) {

		for (int i=0; i<data.size(); i++) {

			String line = data.get(i);

			// Accession
			if (line.contains("ArrayExpressAccession")) {
				this.accession = this.extractValue(line);
			}

			// Title
			if (line.startsWith("Investigation Title")) {
				this.title = this.extractValue(line);
			}

			// Submission date
			if (line.startsWith("Public Release Date")) {
				try {
					this.submissionDate = dateFormat.parse(line.split(sep)[1].trim());
				} catch (ParseException e) {
					// nothing to do
				}
			}
			
			// SDRF file
			if (line.startsWith("SDRF File")) {
				this.sdrf = this.extractValue(line);
			}
			
			// Protocol
			if (line.startsWith("Protocol Hardware")) {
				this.protocol = this.extractValue(line);
			}
			
			if (line.contains("SecondaryAccession")) {
				String acc = this.extractValue(line);
				try {
					this.listAccessions.addAll(Arrays.asList(acc.split(sep)));
				}
				catch (Exception e) {
					// nothing to do
				}
			}

		}

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
			result = result.trim();
		}

		return result;
	}

	@Override
	public String toString() {
		return "AESeries [accession=" + accession + ", title=" + title + ", submissionDate=" + submissionDate
				+ ", sdrf=" + sdrf + ", protocol=" + protocol + ", listAccessions=" + listAccessions + "]";
	}

	/** =================================================================== */



}
