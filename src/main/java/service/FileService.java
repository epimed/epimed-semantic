package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileService {

	private static String columnSeparator = ";";
	private static String lineSeparator = "\n";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

	/** ================================================================================= */

	public void writeCsvFile(String fileName, String[] header, List<Object> listData) {


		try {
			FileWriter writer = new FileWriter(fileName);

			// === Header ===
			if (header!=null) {
				for (int i=0; i<header.length; i++) {
					writer.append(header[i]!=null ? header[i].replaceAll(columnSeparator, "") : "");
					if (i<(header.length-1)) {
						writer.append(columnSeparator);
					}
				}
				writer.append(lineSeparator);
				writer.flush();
			}

			// === Data ===
			if (listData!=null) {
				for ( Iterator<Object> iterator = listData.iterator(); iterator.hasNext(); ) {
					Object data[] = (Object[]) iterator.next();
					for (int j=0; j<data.length; j++) {
						writer.append(data[j]!=null ? data[j].toString().replaceAll(columnSeparator, "") : "");
						if (j<(data.length-1)) {
							writer.append(columnSeparator);
						}
					}
					writer.append(lineSeparator);
				}
				writer.flush();
			}

			// ===== Close file =====
			writer.close();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** ====================================================================================== */

	/**
	 * Generate a file name depending on parameters
	 * 
	 * @param prefix - prefix of the filename
	 * @param list - list of elements in the file which could appear in the filename
	 * @param maxListNb - max number of elements in the list that can be displayed in the filename
	 * @param suffix - suffux that appear if the list is longer than maxListNb
	 * @param fileExtension - file extension
	 * @return complete filename
	 * 
	 * Examples:
	 * 
	 *  fileService.generateFileName("experimental_grouping", listIdSeries, 3, "SEVERAL_STUDIES", "xlsx");
	 *  
	 *  listIdSeries = [GSE11092, GSE13309, GSE15431]
	 *  Generated file name = experimental_grouping_2016.07.28_GSE11092_GSE13309_GSE15431.xlsx
	 *  
	 *  listIdSeries = [GSE11092, GSE12662, GSE13309, GSE15431]
	 *  Generated file name = experimental_grouping_2016.07.28_SEVERAL_STUDIES.xlsx
	 *  
	 */
	public String generateFileName(String prefix, String[] list, Integer maxListNb, String suffix, String fileExtension) {

		String text="";

		if (suffix!=null && ((list==null ) || 
				(list!=null && list.length>maxListNb))){
			text = text + "_" + suffix;
		}

		if (list!=null && list.length<=maxListNb) {
			text = text + "_";
			for (int i=0; i<list.length; i++) {
				text = text + list[i];
				if (i<list.length-1) {
					text = text + "_";
				}
			}
		}

		String fileName =  prefix + "_" + dateFormat.format(new Date()) + text + "." + fileExtension;
		return fileName;
	}



	/** ================================================================================= */

	public void writeExcelFile(String fileName, List<String> header, List<Object> listData) {

		// === Blank workbook ===
		XSSFWorkbook workbook = new XSSFWorkbook(); 

		// === Create a blank sheet ===
		XSSFSheet sheet = workbook.createSheet("data");

		// === Nb of rows and cells ===
		int rownum = 0;


		// === Header ===
		if (header!=null) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			for (int i=0; i<header.size(); i++) {
				Cell cell = row.createCell(cellnum++);
				cell.setCellValue(header.get(i));
			}
		}

		// === Data ===
		if (listData!=null) {
			for ( Iterator<Object> iterator = listData.iterator(); iterator.hasNext(); ) {
				Object data[] = (Object[]) iterator.next();

				Row row = sheet.createRow(rownum++);

				int cellnum = 0;
				for (int j=0; j<data.length; j++) {

					Cell cell = row.createCell(cellnum++);
					cell.setCellType(Cell.CELL_TYPE_STRING);

					boolean isNull = (data[j]==null);
					if (!isNull) {
						cell.setCellValue(data[j].toString());
					}
				}
			}
		}

		// === Write the workbook in file system ===
		try
		{
			FileOutputStream out = new FileOutputStream(new File(fileName));
			workbook.write(out);
			workbook.close();
			out.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}


	/** ================================================================================= */

	public void addSheet(XSSFWorkbook workbook, String sheetName, List<String> header, List<Object> listData) {

		
		// === Create a blank sheet ===
		XSSFSheet sheet = workbook.createSheet(sheetName);

		// === Nb of rows and cells ===
		int rownum = 0;


		// === Header ===
		if (header!=null) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			for (int i=0; i<header.size(); i++) {
				Cell cell = row.createCell(cellnum++);
				cell.setCellValue(header.get(i));
			}
		}

		// === Data ===
		if (listData!=null) {
			for ( Iterator<Object> iterator = listData.iterator(); iterator.hasNext(); ) {
				Object data[] = (Object[]) iterator.next();

				Row row = sheet.createRow(rownum++);

				int cellnum = 0;
				for (int j=0; j<data.length; j++) {

					Cell cell = row.createCell(cellnum++);
					cell.setCellType(Cell.CELL_TYPE_STRING);

					boolean isNull = (data[j]==null);
					if (!isNull) {
						cell.setCellValue(data[j].toString());
					}
				}
			}
		}

	}



	/** ================================================================================= 
	 * @return */

	public XSSFWorkbook createWorkbook(){
		return new XSSFWorkbook(); 
	}


	/** ================================================================================= */

	public void writeWorkbook(XSSFWorkbook workbook, String fileName) {

		// === Write the workbook in file system ===
		try
		{
			FileOutputStream out = new FileOutputStream(new File(fileName));
			workbook.write(out);
			workbook.close();
			out.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

	/** ================================================================================= */

}
