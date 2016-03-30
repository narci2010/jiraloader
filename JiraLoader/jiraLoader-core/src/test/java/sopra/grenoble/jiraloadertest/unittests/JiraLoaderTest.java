package sopra.grenoble.jiraloadertest.unittests;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sopra.grenoble.jiraLoader.JiraLoader;
import sopra.grenoble.jiraLoader.configurationbeans.ExcelDatas;
import sopra.grenoble.jiraLoader.excel.loaders.XslsFileReaderAndWriter;
import sopra.grenoble.jiraLoader.wrappers.StoryWrapper;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class JiraLoaderTest extends JiraLoader {

	@Autowired
	private ExcelDatas excelFileDatasBean;

	@Test
	public void configurationExcel_badNumberVersion_Test() throws Exception {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelFilesValidationTests/Import_JIRA_VERSIONTOOLOW.xlsx");
		assertNotNull(nonExcelFileUrl);
		File excelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter excelLoader = new XslsFileReaderAndWriter(excelFile);

		assertFalse(this.loadConfigurationAndValidateExcelFormat(excelLoader, 0));
	}

	@Test
	public void configurationExcel_badFile_Test() throws Exception {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelFilesValidationTests/Import_JIRA_BADCONF.xlsx");
		assertNotNull(nonExcelFileUrl);
		File excelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter excelLoader = new XslsFileReaderAndWriter(excelFile);

		assertFalse(this.loadConfigurationAndValidateExcelFormat(excelLoader, 0));
	}
	
	@Test
	public void configurationExcel_ok_Test() throws Exception {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelFilesValidationTests/Import_JIRA_OK.xlsx");
		assertNotNull(nonExcelFileUrl);
		File excelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter excelLoader = new XslsFileReaderAndWriter(excelFile);

		assertTrue(this.loadConfigurationAndValidateExcelFormat(excelLoader, 0));
		assertTrue(this.excelFileDatasBean.isSearchStoryByNameBeforeCreate());
	}

	@Test
	public void testImportSheetPage0AndConfSheetPage1() throws Exception {
		String path = "excelFilesValidationTests/Import_JIRA_PAGE_INVERSE.xlsx";

		URL excelFileUrl = ClassLoader.getSystemClassLoader().getResource(path);
		FileInputStream fis = new FileInputStream(excelFileUrl.getPath());
		assertNotNull(fis);

		Integer confSheetNumber = null;
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			if (workbook.getSheetAt(i).getSheetName().equals("Configuration")) {
				confSheetNumber = i;
			}
		}

		File excelFile = new File(excelFileUrl.getPath());
		XslsFileReaderAndWriter excelLoader = new XslsFileReaderAndWriter(excelFile);
		assertTrue(this.loadConfigurationAndValidateExcelFormat(excelLoader, confSheetNumber));
	}

	@Test
	public void lectureFichierOrdreAléatoireTest() throws Exception {
		String path = "excelTestFiles/Import_JIRA_Ordre_Aleatoire.xlsx";

		URL excelFileUrl = ClassLoader.getSystemClassLoader().getResource(path);
		FileInputStream fis = new FileInputStream(excelFileUrl.getPath());
		assertNotNull(fis);

		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		File excelFile = new File(excelFileUrl.getPath());
		XslsFileReaderAndWriter excelLoader = new XslsFileReaderAndWriter(excelFile);
		excelLoader.openSheet(1);
		excelLoader.setRowPosition(1);
		StoryWrapper wrapper = new StoryWrapper();
		Row row = excelLoader.readNextRow();
		wrapper.loadRow(row, excelLoader);
		assertFalse(true);
		// les valeurs attendues sont les suivantes / {"key":null,"typeDemande":"Version","epicName":null,"versionName":"OPAL-Test","resume":"RESUM TEST","clientReference":null,"descriptif":"DESCRIPTIF TEST","priority":"urgent","composantName":null,"estimation":null}
		//Ce test est bloqué exprès en attendant d'avoir trouvé la méthode pour bien tester la lecture de la ligne, cependant la vérification est possible par la lecture du log create dto : ...
	}

}
