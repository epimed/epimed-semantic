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
package module;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BaseModule {

	
	// === ATTRIBUTES ===

	private String operatingSystem = System.getProperty("os.name").toLowerCase();
	private String workingDir = System.getProperty("user.dir");
	private String dirSeparator;
	private String inputDirectory;
	private String outputDirectory;
	private Date currentDate = new Date();

	// === CONSTRUCTORS ===

	public BaseModule(){
		if (isWindows()) {
			this.setDirSeparator("\\");
		}
		else {
			this.setDirSeparator("/");
		}

		// output directory
		setOutputDirectory(getWorkingDir() + this.getDirSeparator() + "data" + this.getDirSeparator() + "out");
		createDirectory(getOutputDirectory());

		// input directory
		setInputDirectory(getWorkingDir() + this.getDirSeparator() + "data" + this.getDirSeparator() + "in");
		createDirectory(getInputDirectory());
	}


	// === GETTERS SETTERS ===


	public Date getCurrentDate() {
		return currentDate;
	}


	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}


	public String getInputDirectory() {
		return inputDirectory;
	}


	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}


	public String getOutputDirectory() {
		return outputDirectory;
	}


	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}


	public String getOperatingSystem() {
		return operatingSystem;
	}


	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}


	public String getWorkingDir() {
		return workingDir;
	}


	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}


	public String getDirSeparator() {
		return dirSeparator;
	}


	public void setDirSeparator(String dirSeparator) {
		this.dirSeparator = dirSeparator;
	}


	// === METHODS === 

	/** ======================================================================================*/
	
	public boolean isWindows() {

		return (operatingSystem.indexOf("win") >= 0);

	}

	/** ======================================================================================*/
	
	public boolean isMac() {

		return (operatingSystem.indexOf("mac") >= 0);

	}

	/** ======================================================================================*/
	
	public boolean isUnix() {

		return (operatingSystem.indexOf("nix") >= 0 || operatingSystem.indexOf("nux") >= 0 || operatingSystem.indexOf("aix") > 0 );

	}

	/** ======================================================================================*/
	
	public boolean isSolaris() {

		return (operatingSystem.indexOf("sunos") >= 0);

	}

	/** ======================================================================================*/

	public void createDirectory(String directoryPath) {
		File directoryPathFile = new File(directoryPath);
		if (!directoryPathFile.exists()) {
			directoryPathFile.mkdirs();
		}
	}
	
	/** ======================================================================================*/
	
	public static int getDiffYears(Date first, Date last) {
	    Calendar a = getCalendar(first);
	    Calendar b = getCalendar(last);
	    int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
	    if (a.get(Calendar.DAY_OF_YEAR) > b.get(Calendar.DAY_OF_YEAR)) {
	        diff--;
	    }
	    return diff;
	}

	/** ======================================================================================*/
	
	public static Calendar getCalendar(Date date) {
	    Calendar cal = Calendar.getInstance(Locale.US);
	    cal.setTime(date);
	    return cal;
	}
	
	/** ======================================================================================*/

	public static double round (double value, int scale) {
		return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
	}
	
	/** ======================================================================================*/

}
