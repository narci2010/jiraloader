package sopra.grenoble.jiraloadertest.unittests.excel.loader;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;
import sopra.grenoble.jiraLoader.excel.loaders.XslsFileReaderAndWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

public class XslsFileReaderAndWriterTest {

	@Test(expected=FileNotFoundException.class)
	public void createWithNullFileTest() throws IOException {
		new XslsFileReaderAndWriter(null);
		
	}
	
	@Test(expected=FileNotFoundException.class)
	public void createWithNonExistingFile() throws IOException {
		new XslsFileReaderAndWriter(new File("/tmp/nofile"));
	}
	
	@Test(expected=IOException.class)
	public void createWithNonExcelFile() throws IOException {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("application.properties");
		assertNotNull(nonExcelFileUrl);
		
		File nonExcelFile = new File(nonExcelFileUrl.getPath());
		assertNotNull(nonExcelFile);
		assertTrue(nonExcelFile.isFile());
		
		new XslsFileReaderAndWriter(nonExcelFile);
	}
	
	@Test
	public void createWithCorrectFile() throws IOException {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelTestFiles/Import_JIRA_TEST1.xlsx");
		assertNotNull(nonExcelFileUrl);
		
		File nonExcelFile = new File(nonExcelFileUrl.getPath());
		assertNotNull(nonExcelFile);
		assertTrue(nonExcelFile.isFile());
		
		new XslsFileReaderAndWriter(nonExcelFile);
	}
	
	@Test
	public void readSecondLine() throws IOException {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelTestFiles/Import_JIRA_TEST1.xlsx");
		File nonExcelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter xlsrw = new XslsFileReaderAndWriter(nonExcelFile);
		xlsrw.openSheet(1);
		Row row = xlsrw.readLine(1);
		assertNotNull(row);
	}
	
	@Test
	public void isLastRowTest() throws IOException {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelTestFiles/Import_JIRA_TEST1.xlsx");
		File nonExcelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter xlsrw = new XslsFileReaderAndWriter(nonExcelFile);
		xlsrw.openSheet(1);
		assertFalse(xlsrw.isLastRow());
		
		//set the position at the end of the file
		xlsrw.setRowPosition(100);
		assertTrue(xlsrw.isLastRow());
	}

	@Test
	public void findColumnNumberTest() throws IOException {
		URL excelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelTestFiles/Test_FindColumnNumber.xlsx");
		File excelFile = new File(excelFileUrl.getPath());
		XslsFileReaderAndWriter xlsrw = new XslsFileReaderAndWriter(excelFile);
		xlsrw.openSheet(0);
		Row row = xlsrw.readLine(0);
		assertEquals(xlsrw.findColumnNumber(xlsrw, "Hello"), 1);
		assertNotEquals(xlsrw.findColumnNumber(xlsrw, "Hello"), 2);

	}

	@Test
	public void createExcelFile() throws IOException {
		FileOutputStream fileOut = new FileOutputStream("export-worklog-jira.xls");
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Export work log Jira");
		HSSFRow row = sheet.createRow((short) 0);
		row.createCell(0).setCellValue("Issue");
		row.createCell(1).setCellValue("WorkLog");
		row.createCell(2).setCellValue("Author");
		row.createCell(3).setCellValue("Comment");
		row.createCell(4).setCellValue("Time spent");
		row.createCell(5).setCellValue("RAE");
		workbook.write(fileOut);
		fileOut.flush();
		fileOut.close();
	}
}
