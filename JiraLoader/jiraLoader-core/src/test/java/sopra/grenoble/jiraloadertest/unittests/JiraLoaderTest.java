package sopra.grenoble.jiraloadertest.unittests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sopra.grenoble.jiraLoader.JiraLoader;
import sopra.grenoble.jiraLoader.configurationbeans.ExcelDatas;
import sopra.grenoble.jiraLoader.excel.loaders.XslsFileReaderAndWriter;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;


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
		
		assertFalse(this.loadConfigurationAndValidateExcelFormat(excelLoader));
	}

	@Test
	public void configurationExcel_badFile_Test() throws Exception {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelFilesValidationTests/Import_JIRA_BADCONF.xlsx");
		assertNotNull(nonExcelFileUrl);
		File excelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter excelLoader = new XslsFileReaderAndWriter(excelFile);
		
		assertFalse(this.loadConfigurationAndValidateExcelFormat(excelLoader));
	}
	
	@Test
	public void configurationExcel_ok_Test() throws Exception {
		URL nonExcelFileUrl = ClassLoader.getSystemClassLoader().getResource("excelFilesValidationTests/Import_JIRA_OK.xlsx");
		assertNotNull(nonExcelFileUrl);
		File excelFile = new File(nonExcelFileUrl.getPath());
		XslsFileReaderAndWriter excelLoader = new XslsFileReaderAndWriter(excelFile);
		
		assertTrue(this.loadConfigurationAndValidateExcelFormat(excelLoader));
		assertTrue(this.excelFileDatasBean.isSearchStoryByNameBeforeCreate());
	}
}
