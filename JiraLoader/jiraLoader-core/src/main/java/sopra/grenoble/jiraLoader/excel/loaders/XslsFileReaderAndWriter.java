package sopra.grenoble.jiraLoader.excel.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sopra.grenoble.jiraLoader.excel.dto.GenericModel;
import sopra.grenoble.jiraLoader.exceptions.UnexpectedTypeLineException;

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
		final Cell cell = nextRow.getCell(1);
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
}
