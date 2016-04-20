package sopra.grenoble.jiraLoader;

import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Worklog;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sopra.grenoble.jiraLoader.configurationbeans.ExcelDatas;
import sopra.grenoble.jiraLoader.configurationbeans.JiraUserDatas;
import sopra.grenoble.jiraLoader.excel.dto.GenericModel;
import sopra.grenoble.jiraLoader.excel.loaders.XslsFileReaderAndWriter;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.UnexpectedTypeLineException;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.MetadataGeneralLoader;
import sopra.grenoble.jiraLoader.wrappers.AbstractWrapper;
import sopra.grenoble.jiraLoader.wrappers.WrapperFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author cmouilleron Main class to import excel document in JIRA application
 */
@Component
public class JiraLoader {

	private static final Logger LOG = LoggerFactory.getLogger(JiraLoader.class);

	@Autowired
	private MetadataGeneralLoader metadataLoader;

	@Autowired
	private JiraUserDatas jiraUserDatasBean;

	@Autowired
	private ExcelDatas excelFileDatasBean;

	@Autowired
	private IJiraRestClientV2 jiraConnection;
	
	/**
	 * The wrapper factory
	 */
	private WrapperFactory wrapperFact = new WrapperFactory();

	/**
	 * Function to load metadatas.
	 */
	private void loadMetadata() {
		// load all metadata
		metadataLoader.initLoaderSynchronously();
	}

	/**
	 * Main function to export data from jira worklog to an excel file.
	 * @throws IOException
	 */
	public void exportDataFromWorkLog() throws IOException, URISyntaxException {

		LOG.info("###################################################");
		LOG.info("STEP 1 - Loading metadatas");
		loadMetadata();

		LOG.info("###################################################");
		LOG.info("STEP 3 - Loading data");
		List<Issue> issueList = loadWorkLogData();


		if (issueList.isEmpty()) {
			LOG.warn("Issue list is EMPTY, please check Jira");
			LOG.warn("export status : SUCCESS but nothing was done");
		} else {
			LOG.info("###################################################");
			LOG.info("STEP 4 - Writing data");
			writeDataTwo(issueList);

			LOG.info("###################################################");
			LOG.info("New file created : \"export-worklog-jira.xls\" ");
			LOG.info("Well done,export status : SUCCESS");
		}
	}

	public List<Issue> loadWorkLogData() throws IOException, URISyntaxException {
		//Set params
		LOG.info("Opening connection");
		jiraConnection.openConnection();
		String projectName = jiraUserDatasBean.getProjectName();
		NullProgressMonitor pm = new NullProgressMonitor();

		//Load data
		List<Issue> issueList = new ArrayList<>();
		String jqlSearch = "project=\"" + projectName + "\" ORDER BY updated DESC";
		SearchResult searchResult = jiraConnection.getSearchClient().searchJql(jqlSearch, 1, 0, pm);

		LOG.info("Search request : " + jqlSearch);
		LOG.info("Total issues : " + searchResult.getTotal());

		searchResult = jiraConnection.getSearchClient().searchJql(jqlSearch, 100, 0, pm);

		//Set date for export
		DateTime today = new DateTime().withTimeAtStartOfDay().plusDays(1);

		long dayInMs = 1000 * 60 * 60 * 24 * 3;
		Date twoDaysAgo = new Date(today.getMillis() - dayInMs);


		for (BasicIssue basicIssue : searchResult.getIssues()) {
			Issue issue = jiraConnection.getIssueClient().getIssue(basicIssue.getKey(), pm);
			Iterable<Worklog> worklogList = issue.getWorklogs();
			worklogList.forEach(worklog -> {
				if (worklog.getUpdateDate().isAfter(twoDaysAgo.getTime())) {
					if (!issueList.contains(issue)) {
						LOG.info(issue.getKey() + " : " + issue.getSummary() + " added in list");
						issueList.add(issue);
					}
				}
			});
		}
		return issueList;
	}

	public void writeDataTwo(List<Issue> issueList) throws IOException {
		// Create excel file
		LOG.info("Creating excel file ....");
		FileOutputStream fileOut = new FileOutputStream("export-worklog-jira.xls");
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Export work log Jira");
		HSSFRow row = sheet.createRow((short) 0);
		LOG.info("Excel file created");

		// Set style for cells
		HSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		row.setRowStyle(style);

		HSSFCellStyle tabStyle = workbook.createCellStyle();
		tabStyle.setWrapText(true);
		tabStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		tabStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		tabStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle.setRightBorderColor(HSSFColor.BLACK.index);
		tabStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle.setTopBorderColor(HSSFColor.BLACK.index);

		HSSFCellStyle tabStyle2 = workbook.createCellStyle();
		tabStyle2.setWrapText(true);
		tabStyle2.setWrapText(true);
		tabStyle2.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle2.setBottomBorderColor(HSSFColor.BLACK.index);
		tabStyle2.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle2.setLeftBorderColor(HSSFColor.BLACK.index);
		tabStyle2.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle2.setRightBorderColor(HSSFColor.BLACK.index);
		tabStyle2.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		tabStyle2.setTopBorderColor(HSSFColor.BLACK.index);
		tabStyle2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		tabStyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		HSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		headerStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		headerStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		headerStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		headerStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		headerStyle.setRightBorderColor(HSSFColor.BLACK.index);
		headerStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		headerStyle.setTopBorderColor(HSSFColor.BLACK.index);
		headerStyle.setFillForegroundColor(HSSFColor.ROYAL_BLUE.index);
		headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// Create header
		LOG.info("Header creation");
		row.createCell(0).setCellValue("Issue");
		row.getCell(0).setCellStyle(headerStyle);
		row.createCell(1).setCellValue("Summary");
		row.getCell(1).setCellStyle(headerStyle);
		row.createCell(2).setCellValue("Author");
		row.getCell(2).setCellStyle(headerStyle);
		row.createCell(3).setCellValue("Comment");
		row.getCell(3).setCellStyle(headerStyle);
		row.createCell(4).setCellValue("Time spent");
		row.getCell(4).setCellStyle(headerStyle);
		row.createCell(5).setCellValue("RAE");
		row.getCell(5).setCellStyle(headerStyle);
		row.createCell(6).setCellValue("Update date");
		row.getCell(6).setCellStyle(headerStyle);

		DateTime today = new DateTime().withTimeAtStartOfDay().plusDays(1);
		long dayInMs = 1000 * 60 * 60 * 24 * 3;
		Date twoDaysAgo = new Date(today.getMillis() - dayInMs);

		// Create others rows
		LOG.info("Insert data");
		if (issueList != null) {
			for (int i = 0; i < issueList.size(); i++) {
				Issue issue = issueList.get(i);
				//		List<Worklog> worklogList = (List<Worklog>) issue.getWorklogs();
				for (Iterator<Worklog> worklogs = issue.getWorklogs().iterator(); worklogs.hasNext(); ) {
					Worklog worklog = worklogs.next();
					// Initialize variable
					int lastRowNum = sheet.getLastRowNum();
					lastRowNum++;

					//Checking update date
					if (worklog.getUpdateDate().isAfter(twoDaysAgo.getTime())) {

						// Create new row
						row = sheet.createRow(lastRowNum);

						// Create first cell : the Issue's KEY
						row.createCell(0).setCellValue(issue.getKey());
						row.getCell(0).setCellStyle(tabStyle);

						// Create second cell : the Issue's summary
						row.createCell(1).setCellValue(issue.getSummary());
						row.getCell(1).setCellStyle(tabStyle);

						//Create third cell : the Worklog's update author
						row.createCell(2).setCellValue(worklog.getUpdateAuthor().getDisplayName());
						row.getCell(2).setCellStyle(tabStyle);

						// Create fourth cell : the Worklog's comment
						row.createCell(3).setCellValue(worklog.getComment());
						row.getCell(3).setCellStyle(tabStyle);

						// Create 5th cell : Worklog's minutes spent
						if (worklog.getMinutesSpent() != null) {
							if (worklog.getMinutesSpent() % 60 == 0) {
								row.createCell(4).setCellValue(worklog.getMinutesSpent() / 60 + "h");
								row.getCell(4).setCellStyle(tabStyle);
							} else if (worklog.getMinutesSpent() <= 59) {
								row.createCell(4).setCellValue(worklog.getMinutesSpent() + "m");
								row.getCell(4).setCellStyle(tabStyle);
							} else if (worklog.getMinutesSpent() % 60 != 0 && worklog.getMinutesSpent() > 60) {
								row.createCell(4).setCellValue(worklog.getMinutesSpent() / 60 + "h" + worklog.getMinutesSpent() % 60 + "m");
								row.getCell(4).setCellStyle(tabStyle);
							}
						} else {
							row.createCell(4).setCellValue("No time spent set");
							row.getCell(4).setCellStyle(tabStyle);
						}

						// Create 6th cell : RAE
						if (issue.getTimeTracking().getRemainingEstimateMinutes() != null) {
							if (issue.getTimeTracking().getRemainingEstimateMinutes() % 60 == 0) {
								row.createCell(5).setCellValue(issue.getTimeTracking().getRemainingEstimateMinutes() / 60 + "h");
								row.getCell(5).setCellStyle(tabStyle);
							} else if (issue.getTimeTracking().getRemainingEstimateMinutes() <= 59) {
								row.createCell(5).setCellValue(issue.getTimeTracking().getRemainingEstimateMinutes() + "m");
								row.getCell(5).setCellStyle(tabStyle);
							} else if (issue.getTimeTracking().getRemainingEstimateMinutes() > 60 && issue.getTimeTracking().getRemainingEstimateMinutes() % 60 != 0) {
								row.createCell(5).setCellValue(issue.getTimeTracking().getRemainingEstimateMinutes() / 60 + "h" + issue.getTimeTracking().getRemainingEstimateMinutes() % 60 + "m");
								row.getCell(5).setCellStyle(tabStyle);
							}

						} else {
							row.createCell(5).setCellValue("No RAE set");
							row.getCell(5).setCellStyle(tabStyle);
						}

						// Create the last cell : Worklog's update date
						row.createCell(6).setCellValue(worklog.getUpdateDate().toString().substring(0, 10));
						row.getCell(6).setCellStyle(tabStyle);
					}
				}
			}
		} else {
			LOG.warn("Careful, empty issue list");
		}

		// Autosize Columns
		for (int i = 0; i <= 6; i++) {
			sheet.autoSizeColumn(i);
		}
		for (long i = 1; i < sheet.getLastRowNum(); i++) {
			for (int j = 0; j < 7; j++) {
				if (i % 2 == 1) {
					row = sheet.getRow((int) i);
					row.getCell(j).setCellStyle(tabStyle2);
				}
			}
		}

		// Save & close file
		try {
			LOG.info("Save data in file : ...");
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
			LOG.info("Save data in file : OK");
		} catch (IOException e) {
			LOG.warn(e.getStackTrace().toString());
			LOG.warn("Error during data saving");
		}
	}

	public int incrementeI(int i) {
		return i++;
	}

	/**
	 * Function to load excel file in JIRA.
	 * 
	 * @param excelFilePath
	 * @throws IOException
	 */
	public void loadingFile(String excelFilePath) throws IOException {

		// create a runnable task in a separate thread

		LOG.info("###################################################");
		LOG.info("STEP 1 - Loading metadatas ");
		loadMetadata();

		LOG.info("###################################################");
		LOG.info("STEP 2 - Starting the excel file import : " + excelFilePath);

		//Select file and create the workbook
		FileInputStream fis;
		XSSFWorkbook workbook;
		try {
			fis = new FileInputStream(excelFilePath);
			workbook = new XSSFWorkbook(fis);
		} catch (Exception e) {
			LOG.error("Error while opening Excel file. ", e);
			return;
		}

		Integer importSheetPageNumber = null;
		Integer configurationSheetPageNumber = null;

		//For each sheet in the workbook find page number for Import & Configuration.
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			if (workbook.getSheetName(i).equals("Import")) {
				importSheetPageNumber = i;
			}
			if (workbook.getSheetName(i).equals("Configuration")) {
				configurationSheetPageNumber = i;
			}
		}

		XslsFileReaderAndWriter excelLoader = null;
		try {
			File excelFile = new File(excelFilePath);
			excelLoader = new XslsFileReaderAndWriter(excelFile);
		} catch (Exception e) {
			LOG.error("Error while opening Excel file. ", e);
			return;
		}
		// Valide excel format file and load specific configuration with the page number of the configuration sheet.
		LOG.info("###################################################");
		LOG.info("STEP 3 - Validate excel format file and load specific configuration");
		if (!loadConfigurationAndValidateExcelFormat(excelLoader, configurationSheetPageNumber)) {
			LOG.error("The first sheet 'Configuration' is not valid. Please read logs and fix excel file");
			return;
		}
		
		try {
			/*
			 * File is opened. Now validate all rows for the import sheet
			 */
			LOG.info("###################################################");
			LOG.info("STEP 4 - Validating all excel file rows");
			if (!validateAllRows(excelLoader, importSheetPageNumber)) {
				LOG.error("At least one row is not valid. Please read logs and fix excel file");
				return;
			}

			/*
			 * ALl row are valid. Now inject row one by one in JIRA for the import sheet.
			 */
			LOG.info("###################################################");
			LOG.info("STEP 5 - Starting the injection");
			injectAllRows(excelLoader, importSheetPageNumber);

		} finally {
			// always close the excel file
			excelLoader.closeFile();
			fis.close();
		}
	}
	
	/**
	 * This function validate the excel file version and loads the configuration specified in the first sheet.
	 * @param excelLoader
	 */
	protected boolean loadConfigurationAndValidateExcelFormat(XslsFileReaderAndWriter excelLoader, Integer pageNumber) {
		excelLoader.openSheet(pageNumber);
		// The excel file version must be valid
		Optional<Integer> result = excelLoader.readIntegerCellContent(1, 5);
		if (!result.isPresent() || result.get().intValue() < ExcelDatas.JIRA_LOADER_REQUIRED_VERSION) 
		{
			LOG.error("The excel file version is not correct. You have to use at least the version " + ExcelDatas.JIRA_LOADER_REQUIRED_VERSION);
			return false;
		}
		
		LOG.info("Your excel file version is : " + result.get());
		
		//load the other configuration value
		excelFileDatasBean.setSearchStoryByNameBeforeCreate(excelLoader.readBooleanCellContent(2, 5).orElseGet(() -> {
			LOG.warn("No configuration at line 2,5. Set default value to false");
			return false;
		}));

		// load the configuration value for allowing update

		excelFileDatasBean.setAllowUpdate(excelLoader.readBooleanCellContent(3, 5).orElseGet(() -> {
			LOG.warn("No configuration at line 3,5. Set default value to false");
			return false;
		}));

		// load the configuration value : reverberate update from Story to subtask
		excelFileDatasBean.setUpdateStoryAndSubTasks(excelLoader.readBooleanCellContent(4, 5).orElseGet(() -> {
			LOG.warn("No configuration at line 4,5. Set default value to false");
			return false;
		}));

		excelFileDatasBean.setUpdateSubTasksOutOfFile(excelLoader.readBooleanCellContent(5, 5).orElseGet(() -> {
			LOG.warn("No configuration at line 5,5. Set default value to false");
			return false;
		}));

		return true;
	}

	/**
	 * Call wrapper for each row to inject datas in JIRA
	 * 
	 * @param excelLoader
	 */
	private void injectAllRows(XslsFileReaderAndWriter excelLoader, Integer pageNumber) {
		// skip header
		excelLoader.openSheet(pageNumber);
		int i = 1;
		boolean lastRow = false;
		do {
			excelLoader.setRowPosition(i);
			final Row row = excelLoader.readNextRow();
			lastRow = !excelLoader.isLastRow();

			String typeDemande = row.getCell(XslsFileReaderAndWriter.findColumnNumber(excelLoader, "Type de demande")).getStringCellValue();
			AbstractWrapper<? extends GenericModel> wrapper = wrapperFact.getWrapper(typeDemande);
			// load the row
			GenericModel genModel = wrapper.loadRow(row, excelLoader);

			try {
				// call create or update line
				if (wrapper.isCreateAction()) {
					// inject
					wrapper.createRowInJira();
				} else {
					wrapper.updateRowInJira();
				}
			} catch (Exception e) {
				LOG.error("Error while processing row in JIRA", e);
				return;
			}

			// save the lastStory key if it's a story
			if (genModel.typeDemande.compareTo(JiraIssuesTypeLoader.JIRA_STORY_ISSUE_TYPE_NAME) == 0) {
				LOG.debug("Save last story key : " + genModel.key);
				jiraUserDatasBean.setLastStoryKey(genModel.key);
			}
			i++;
		} while (lastRow);

		LOG.info("Injection is done !!! Good game !!!");
	}

	/**
	 * Validate all excel file rows. Validation is done by the wrapper.
	 * 
	 * @param excelLoader
	 * @return {@link Boolean}
	 */
	private boolean validateAllRows(XslsFileReaderAndWriter excelLoader, Integer pageNumber) {
		// skip header
		int i = 1;
		excelLoader.openSheet(pageNumber);
		boolean allLineOK = true;
		boolean atLeastOneDataFound = false;
		boolean lastRow = false;
		do {
			excelLoader.setRowPosition(i);
			Row row = excelLoader.readNextRow();
			atLeastOneDataFound = true;
			lastRow = !excelLoader.isLastRow();
			try {
				String typeDemande = row.getCell(XslsFileReaderAndWriter.findColumnNumber(excelLoader, "Type de demande")).getStringCellValue();
				if (typeDemande == null) {
					throw new UnexpectedTypeLineException();
				}

				AbstractWrapper<? extends GenericModel> wrapper = wrapperFact.getWrapper(typeDemande);
				if (wrapper == null) {
					throw new UnexpectedTypeLineException();
				}
				// load the row
				wrapper.loadRow(row, excelLoader);

				// validate the row
				if (!wrapper.validateRow()) {
					allLineOK = false;
				}
			} catch (JiraGeneralException e) {
				LOG.error("Row <" + row.getRowNum() + "> JIRA has raised an exception. I can't do anything for you...", e);
					allLineOK = false;
			} catch (UnexpectedTypeLineException e) {
				LOG.error("Row <" + row.getRowNum() + "> The typeDemande value is not support by the application", e);
				allLineOK = false;
				}
			i++;
		}
		while (lastRow);
		
		if (atLeastOneDataFound == false) {
			//no line in the excel file
			LOG.error("No line has been found in the excel file. Please check your second excel sheet");
			allLineOK = false;
		}
		return allLineOK;
	}


}
