package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelService {

	private List<Object> header = new ArrayList<Object>();
	private Map<String, Integer> headerMap = new HashMap<String, Integer>();
	private List<List<Object>> data = new ArrayList<List<Object>>();
	private List<List<Object>> fontColors = new ArrayList<List<Object>>();
	private boolean isDebug = false;

	/** ================================================================ */
	//===== GETTERS AND SETTERS =====

	public List<Object> getHeader() {
		return header;
	}

	public void setHeader(List<Object> header) {
		this.header = header;
	}

	public List<List<Object>> getData() {
		return data;
	}

	public void setData(List<List<Object>> data) {
		this.data = data;
	}

	public Map<String, Integer> getHeaderMap() {
		return headerMap;
	}

	public void setHeaderMap(Map<String, Integer> headerMap) {
		this.headerMap = headerMap;
	}
	
	public List<List<Object>> getFontColors() {
		return fontColors;
	}

	public void setFontColors(List<List<Object>> fontColors) {
		this.fontColors = fontColors;
	}


	/** ================================================================ */
	//===== METHODS =====


	
	/** ================================================================ */

	private void clearAllAttributes() {
		header.clear();
		headerMap.clear();
		data.clear();
		fontColors.clear();
	}

	/** ================================================================ */

	/**
	 * Load Excel file into a matrix of Objects in memory, named 'data'
	 * @param inputfile
	 */
	public void load (String inputfile) {

		this.clearAllAttributes();

		FileInputStream file;

		try {
			file = new FileInputStream(new File(inputfile));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);

			int rowNb=0;

			// Excel data load

			// ROWS
			for(Row row : sheet) {

				List<Object> dataLine = new ArrayList<Object>();
				List<Object> colorLine = new ArrayList<Object>();

				//For each row, iterate through each columns
				int cellNb =0;

				// CELLS
				for(int cn=0; cn<row.getLastCellNum(); cn++) {

					Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);


					// Cell font color
					CellStyle style = cell.getCellStyle();
					XSSFCellStyle style1 = (XSSFCellStyle) style;
					XSSFFont font = style1.getFont();
					XSSFColor colour = font.getXSSFColor();
					String colorString = "FF000000";
					if (colour!=null) {
						colorString = colour.getARGBHex();
					}
					// System.out.println("Font color " + font.getColor()  + " " + colorString);
					colorLine.add(colorString);

					switch (cell.getCellType())
					{

					case Cell.CELL_TYPE_NUMERIC:
						// date
						if (DateUtil.isCellDateFormatted(cell)) {
							double dv = cell.getNumericCellValue();			
							Date date = DateUtil.getJavaDate(dv);
							if (isDebug) {
								System.out.print("date:" + date + " \t ");
							}
							dataLine.add(date);
						}
						else {
							dataLine.add(cell.getNumericCellValue());
							if (isDebug) {
								System.out.print("num:" + cell.getNumericCellValue() + " \t ");
							}
						}



						break;
					case Cell.CELL_TYPE_STRING:
						dataLine.add(cell.getStringCellValue());
						if (isDebug) {
							System.out.print("str:" + cell.getStringCellValue() + " \t ");
						}
						break;
					default:
						dataLine.add(null);
						if (isDebug) {
							System.out.print("Null" + " \t ");
						}
						break;
					}
				}
				cellNb++;


				if (isDebug) {
					System.out.println("");
				}

				// Add line in a common 2D table
				if (rowNb==0) {
					header.addAll(dataLine);
					this.fillHeaderMap();
				}
				else {
					data.add(dataLine);
					fontColors.add(colorLine);
				}

				rowNb ++;
			} // rows

			workbook.close();
			file.close();


		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}


	/** ================================================================ */

	/**
	 * Fill a Map structure (headerMap) with the following information:
	 * <Name of the column in Excel file, index of this column>
	 */
	protected void fillHeaderMap() {

		for (int i=0; i<header.size(); i++) {

			if (header.get(i) != null) {
				headerMap.put(header.get(i).toString().trim(), i);
			}
			else {
				headerMap.put("null", i);
			}
		}
	}

	/** ================================================================ */

	/**
	 * Extract the column corresponding to a given index
	 * @param columnIndex
	 * @return
	 */
	public List<Object> extractColumn(Integer columnIndex) {

		List<Object> column = new ArrayList<Object> ();
		if (header.size()<=columnIndex);
		for (int i=0; i<data.size(); i++) {
			column.add(data.get(i).get(columnIndex));
		}
		return column;
	}

	/** ================================================================ */



}
