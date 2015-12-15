package sopra.grenoble.jiraLoader.unittests.excel.loader;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.apache.poi.ss.usermodel.Row;
import org.junit.Test;

import sopra.grenoble.jiraLoader.excel.loaders.XslsFileReaderAndWriter;

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
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("Import_JIRA_TEST1.xlsx");
		assertNotNull(nonExcelFileUrl);
		
		File nonExcelFile = new File(nonExcelFileUrl.getPath());
		assertNotNull(nonExcelFile);
		assertTrue(nonExcelFile.isFile());
		
		new XslsFileReaderAndWriter(nonExcelFile);
	}
	
	@Test
	public void readSecondLine() throws IOException {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("Import_JIRA_TEST1.xlsx");
		File nonExcelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter xlsrw = new XslsFileReaderAndWriter(nonExcelFile);
		xlsrw.openSheet(1);
		Row row = xlsrw.readLine(1);
		assertNotNull(row);
	}
	
	@Test
	public void isLastRowTest() throws IOException {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("Import_JIRA_TEST1.xlsx");
		File nonExcelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter xlsrw = new XslsFileReaderAndWriter(nonExcelFile);
		xlsrw.openSheet(1);
		assertFalse(xlsrw.isLastRow());
		
		//set the position at the end of the file
		xlsrw.setRowPosition(100);
		assertTrue(xlsrw.isLastRow());
	}

}
