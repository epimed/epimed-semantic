package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import module.BaseModule;


public class WebService extends BaseModule {

	private URL url;
	private Scanner scan;
	private boolean DEBUG = false;

	/** ====================================================================================== */

	// ===== CONSTRUCTOR ======

	public WebService(){
		if (this.isWindows()) {
			// System.getProperties().put("http.proxyHost", "www-cache.ujf-grenoble.fr");
			// System.getProperties().put("http.proxyPort", "3128");
			// System.getProperties().put("https.proxyHost", "www-cache.ujf-grenoble.fr");
			// System.getProperties().put("https.proxyPort", "3128");
		}
	}



	/** ====================================================================================== */

	public List<String> loadGeoOld(String geoAccession) {

		List<String> data = new ArrayList<String>();

		String url = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + geoAccession + "&targ=self&view=brief&form=text";


		if (DEBUG) {
			System.out.println(url);	
		}

		try {

			URL link = new URL(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(link.openStream()));

			// === Wait for content loading ===
			int waitSeconds = 1;
			int maxTrials = 10;
			int currentTrial = 0;
			boolean hasNext = br.ready();
			while (!hasNext && currentTrial<maxTrials) {
				System.err.println("Buffer not ready for " + url + ". New trial in " + waitSeconds + " sec. Trial " +  currentTrial +  "/" + maxTrials + ".");
				currentTrial ++;
				Thread.sleep(waitSeconds*1000);
				hasNext = br.ready();
			}

			// === Content read ===
			while(hasNext) {
				String line = br.readLine();

				if (DEBUG) {
					System.out.println(line);	
				}

				hasNext = line!=null && !line.isEmpty();
				if (hasNext) {data.add(line);}
			}
			br.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return data;	
	}

	/** ====================================================================================== */

	public List<String> loadGeo(String geoAccession) {

		String url = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + geoAccession + "&targ=self&view=brief&form=text";

		String text = this.loadUrl(url);
		String [] parts = text.split(System.getProperty( "line.separator" ));
		List<String> data = new ArrayList<String>(Arrays.asList(parts));

		return data;	
	}

	/** ====================================================================================== */

	/**
	 * 
	 * @param geoAccession
	 * @return
	 */
	public List<String> loadGeoData(String geoAccession) {

		String url = "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + geoAccession + "&targ=self&view=data&form=text";

		String text = this.loadUrl(url);
		String [] parts = text.split(System.getProperty( "line.separator" ));
		List<String> data = new ArrayList<String>(Arrays.asList(parts));

		return data;	
	}


	/** ====================================================================================== */

	public String loadUrl (String urlString) {

		String result = null;

		if (urlString!=null && !urlString.isEmpty()) {
			try {
				HttpURLConnection conn = null;

				conn = openConnection(urlString, true);

				if (conn!=null && conn.getInputStream()!=null) {
					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

					result = readAll(br);

					br.close();
					conn.disconnect();
				}
			}

			catch (IOException e) {
				e.printStackTrace();
			} 
		}
		return result;
	}

	
	
	/** ======================================================================================*/

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}


	/** ======================================================================================*/

	private HttpURLConnection openConnection(String urlString, boolean isJson) {

		int maxNbConn=5;
		boolean isOpen = false;

		int currentConnNb=0;
		int waitSeconds = 10;

		if (urlString!=null && !urlString.isEmpty()) {

			while (!isOpen && currentConnNb<maxNbConn) {
				currentConnNb ++;
				HttpURLConnection conn;
				try {	
					if (DEBUG) {System.out.println(urlString);}
					URL url = new URL(urlString);
					conn = (HttpURLConnection) url.openConnection();		

					conn.setRequestMethod("GET");
					if (isJson) {
						conn.setRequestProperty("Accept", "application/json");
					}

					if (DEBUG) {System.out.println("Response code=" + conn.getResponseCode());}

					// if (conn.getResponseCode()>=400 && conn.getResponseCode()<=407) {
					// Bad request / Forbidden / Not Found etc
					// return null;
					// }

					this.scan = new Scanner(conn.getInputStream());
					isOpen = true;
					return conn;
				} 
				catch (IOException e) {
					e.printStackTrace();
					try {
						System.err.println("Lost connection to the host " + url + " (trial " +  currentConnNb + "/"+ maxNbConn + ")");
						System.err.println("New trial in " + waitSeconds + " sec");
						isOpen = false;
						Thread.sleep(waitSeconds*1000);
					}
					catch (InterruptedException e1) {
						e1.printStackTrace();
						System.exit(0);
					}
				}

			}
		}

		return null;
	}

	/** ====================================================================================== */

}
