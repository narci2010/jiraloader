package sopra.grenoble.jiraloadertest.unittests;

import com.atlassian.jira.rest.client.domain.Issue;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sopra.grenoble.jiraLoader.JiraLoader;
import sopra.grenoble.jiraLoader.configurationbeans.ExcelDatas;
import sopra.grenoble.jiraLoader.excel.loaders.XslsFileReaderAndWriter;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class JiraLoaderTest extends JiraLoader {

	@Autowired
	private ExcelDatas excelFileDatasBean;

	@Autowired
	private IJiraRestClientV2 jiraConnection;

	@Autowired
	private IIssueService issueSrv;

	@Value("${default.project.name}")
	private String projectTestName;

	@Value("${test.component.name}")
	private String componentName;

	@Value("${test.OPAL_ODYSEE.version.name}")
	private String versionName;

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
		System.out.println(excelFileUrl);
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
	public void testWriteDataExportWorklog() throws Exception {
		List<Issue> issueList = loadWorkLogData();
		writeDataTwo(issueList);

	}

	@Test
	public void testListNullWriteDataOk() throws IOException {
		//Set parameters
		List<Issue> issueList = new ArrayList<>();
		issueList = null;
		writeDataTwo(issueList);
		//Load file
		FileInputStream fileInputStream = new FileInputStream("export-worklog-jira.xls");
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = sheet.getRow(0);
		// Check header
		assertEquals("Issue".equals(row.getCell(0).getStringCellValue()), true);
		assertEquals("Summary".equals(row.getCell(1).getStringCellValue()), true);
		assertEquals("Author".equals(row.getCell(2).getStringCellValue()), true);
		assertEquals("Comment".equals(row.getCell(3).getStringCellValue()), true);
		assertEquals("Time spent".equals(row.getCell(4).getStringCellValue()), true);
		assertEquals("RAE".equals(row.getCell(5).getStringCellValue()), true);
		assertEquals("Update date".equals(row.getCell(6).getStringCellValue()), true);
	}
}
