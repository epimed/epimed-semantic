package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;


import module.BaseModule;


public class WebService extends BaseModule {


	/** ====================================================================================== */

	// ===== CONSTRUCTOR ======

	public WebService(){
		if (this.isWindows()) {
			System.getProperties().put("http.proxyHost", "www-cache.ujf-grenoble.fr");
			System.getProperties().put("http.proxyPort", "3128");
		}
	}



	/** ====================================================================================== */

	public List<String> loadGeo(String geoAccession) {

		List<String> data = new ArrayList<String>();

		String url = "http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + geoAccession + "&targ=self&view=brief&form=text";

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
				// System.out.println(line);
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

	/** ======================================================================================*/
}
