package module.test;

public class FormatUri {

	private String [] uris = {"ftp://SRR980484_1.fastq.zip", "ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR643/SRR980484/SRR980484_1.fastq", "ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR643/SRR643746/SRR643746.gz"};

	public FormatUri() {

		for (String uri : uris) {
			String [] uparts = uri.split("[/\\\\]");
			String fileName = uparts[uparts.length-1];
			String [] fparts = fileName.split("[.fastq.gz.zip]");
			String id = fparts[0];
			System.out.println(id);
		}
	}

	public static void main(String[] args) {
		new FormatUri();
	}

}
