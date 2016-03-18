package sopra.grenoble.jiraLoader.excel.loaders;

import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sopra.grenoble.jiraLoader.excel.dto.GenericModel;
import sopra.grenoble.jiraLoader.exceptions.UnexpectedTypeLineException;

import java.io.*;
import java.util.Optional;

/**
 * @author cmouilleron
 *
 */
public class XslsFileReaderAndWriter {

	private static final Logger LOG = LoggerFactory.getLogger(XslsFileReaderAndWriter.class);

	private final File excelFile;
	private FileInputStream excelFileInputStream;
	private XSSFWorkbook workbook = null;
	private int linePosition = 0;
	private Sheet sheetOpened = null;

	/**
	 * Default constructor
	 * 
	 * @param file
	 *            : excel file
	 * @throws IOException
	 */
	public XslsFileReaderAndWriter(File file) throws IOException {
		super();
		this.sheetOpened = null;
		this.excelFile = file;

		// test if the file can be read and write*
		if (file == null) {
			LOG.error("Null value has been passed for filename");
			throw new FileNotFoundException("Unable to load a file with empty value");
		}

		if (!file.isFile()) {
			LOG.error("The file passed in parameter is not a file or cannot be accessed");
			throw new FileNotFoundException("The file passed in parameter is not a file or cannot be accessed");
		}

		if (!file.canWrite()) {
			LOG.error("The file cannot be opened in write mode");
			throw new FileNotFoundException("The file cannot be opened in write mode");
		}

		// load excel file
		this.loadExcelFile();
	}

	/**
	 * Function to load excel file in memory
	 * 
	 * @throws IOException
	 */
	protected void loadExcelFile() throws IOException {
		try {
			excelFileInputStream = new FileInputStream(this.excelFile);
			workbook = new XSSFWorkbook(excelFileInputStream);
		} catch (IOException | POIXMLException e) {
			LOG.error("The input file is not an excel file", e);
			throw new IOException(e);
		}
	}

	/**
	 * Open the sheet
	 * 
	 * @param sheetPosition
	 */
	public void openSheet(int sheetPosition) {
		sheetOpened = workbook.getSheetAt(sheetPosition);
	}

	
	/**
	 * return the {@link Row} at the position {@link #linePosition}
	 * @param linePosition
	 * @return {@link Row}
	 */
	public Row readLine(int linePosition) {
		setRowPosition(linePosition);
		return readNextRow();
	}
	
	/**
	 * Read the content of a specific cell. Result can be null. 
	 * @param linePosition
	 * @param columPosition
	 * @return {@link Optional}
	 */
	public Optional<String> readStringCellContent(int linePosition, int columPosition) {
		setRowPosition(linePosition);
		Row row = readNextRow();
		return ExcelRowUtils.getStringValueFromRow(row, columPosition);
	}
	
	/**
	 * Read the content of a specific cell. Result can be null. 
	 * @param linePosition
	 * @param columPosition
	 * @return {@link Optional}
	 */
	public Optional<Integer> readIntegerCellContent(int linePosition, int columPosition) {
		setRowPosition(linePosition);
		Row row = readNextRow();
		return ExcelRowUtils.getIntegerValueFromRow(row, columPosition);
	}
	
	/**
	 * Read the content of a specific cell. Result can be null. 
	 * @param linePosition
	 * @param columPosition
	 * @return {@link Optional}
	 */
	public Optional<Boolean> readBooleanCellContent(int linePosition, int columPosition) {
		setRowPosition(linePosition);
		Row row = readNextRow();
		return ExcelRowUtils.getBooleanValueFromRow(row, columPosition);
	}

	/**
	 * Set the row position in the current sheet
	 * @param rowPosition
	 */
	public void setRowPosition(int rowPosition) {
		this.linePosition = rowPosition;
	}

	/**
	 * @return {@link GenericModel} row
	 * @throws UnexpectedTypeLineException
	 */
	public Row readNextRow() {
		return sheetOpened.getRow(linePosition++);
	}
	
	/**
	 * Return true if the last row has been reached.
	 * The last raw is defined like this
	 * 	- the raw with no value in TypeDemande
	 * @return
	 */
	public boolean isLastRow() {
		final Row nextRow = sheetOpened.getRow(linePosition);
		//if nextrow is null, return lastLine = true
		if (nextRow == null) {
			return true;
		}
		
		//if cell is null, return lastLine = true
		Row firstRow = sheetOpened.getRow(0);
		final Cell cell = nextRow.getCell(findCellByName(firstRow, "Type de demande"));
		if (cell == null) {
			return true;
		}
		
		//if cell content is null or empty, return lastLine = true
		final String typeDemande = cell.getStringCellValue();
		if (typeDemande == null || typeDemande.compareTo("") == 0) {
			return true;
		}
		return false;
	}
	
	public void closeFile() {
		try {
			this.excelFileInputStream.close();
			FileOutputStream outFile =new FileOutputStream(excelFile);
			this.workbook.write(outFile);
			outFile.close();
		} catch (IOException e) {
			LOG.error("Unable to close the excel document");
		}
	}

	/**
	 * @param excelLoader
	 * @param cellName
	 * @return columnNumber or -1 if column doesn't exist.
	 */
	public static int findColumnNumber(XslsFileReaderAndWriter excelLoader, String cellName) {
		String valueFromCell;
		int columnNumber = -1;
		excelLoader.setRowPosition(0);
		Row row = excelLoader.readNextRow();
		for (int i = 0; i < row.getLastCellNum(); i++) {
			valueFromCell = ExcelRowUtils.getStringValueFromRow(row, i).get();
			if (valueFromCell.equals(cellName)) {
				columnNumber = i;
			}
		}
		return columnNumber;
	}

	/**
	 *
	 * @param row
	 * @param cellName
	 * @return columnNumber or -1 if cellName doesn't exist
	 */
	public static int findCellByName(Row row, String cellName) {
		String valueFromCell;
		int columnNumber = -1;
		for (int i = 0; i < row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (cell.getStringCellValue().equals(cellName)) {
				columnNumber = i;
			}
		}
		return columnNumber;
	}
}
