package sopra.grenoble.jiraLoader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
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
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.MetadataGeneralLoader;
import sopra.grenoble.jiraLoader.wrappers.AbstractWrapper;
import sopra.grenoble.jiraLoader.wrappers.WrapperFactory;

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
		// load and validate file
		XslsFileReaderAndWriter excelLoader = null;
		try {
			File excelFile = new File(excelFilePath);
			excelLoader = new XslsFileReaderAndWriter(excelFile);
		} catch (Exception e) {
			LOG.error("Error while opening Excel file. ", e);
			return;
		}
		
		LOG.info("###################################################");
		LOG.info("STEP 3 - Validate excel format file and load specific configuration");
		if (!loadConfigurationAndValidateExcelFormat(excelLoader)) {
			LOG.error("The first sheet 'Configuration' is not valid. Please read logs and fix excel file");
			return;
		}
		
		try {
			/*
			 * File is opened. Now validate all rows
			 */
			LOG.info("###################################################");
			LOG.info("STEP 4 - Validating all excel file rows");
			if (!validateAllRows(excelLoader)) {
				LOG.error("At least one row is not valid. Please read logs and fix excel file");
				return;
			}

			/*
			 * ALl row are valid. Now inject row one by one in JIRA
			 */
			LOG.info("###################################################");
			LOG.info("STEP 5 - Starting the injection");
			injectAllRows(excelLoader);

		} finally {
			// always close the excel file
			excelLoader.closeFile();
		}
	}
	
	/**
	 * This function validate the excel file version and loads the configuration specified in the first sheet.
	 * @param excelLoader
	 */
	protected boolean loadConfigurationAndValidateExcelFormat(XslsFileReaderAndWriter excelLoader) {
		excelLoader.openSheet(0);
		
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
		
		return true;
	}

	/**
	 * Call wrapper for each row to inject datas in JIRA
	 * 
	 * @param excelLoader
	 */
	private void injectAllRows(XslsFileReaderAndWriter excelLoader) {
		// skip header
		excelLoader.openSheet(1);
		excelLoader.setRowPosition(1);

		do {
			final Row row = excelLoader.readNextRow();
			
			String typeDemande = row.getCell(1).getStringCellValue();
			AbstractWrapper<? extends GenericModel> wrapper = wrapperFact.getWrapper(typeDemande);

			// load the row
			GenericModel genModel = wrapper.loadRow(row);

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
		} while (!excelLoader.isLastRow());

		LOG.info("Injection is done !!! Good game !!!");
	}

	/**
	 * Validate all excel file rows. Validation is done by the wrapper.
	 * 
	 * @param excelLoader
	 * @return {@link Boolean}
	 */
	private boolean validateAllRows(XslsFileReaderAndWriter excelLoader) {
		// skip header
		excelLoader.openSheet(1);
		excelLoader.setRowPosition(1);
		boolean allLineOK = true;
		boolean atLeastOneDataFound = false;

		while (!excelLoader.isLastRow()) {
			atLeastOneDataFound = true;

			final Row row = excelLoader.readNextRow();

			try {
				String typeDemande = row.getCell(1).getStringCellValue();
				if (typeDemande == null) {
					throw new UnexpectedTypeLineException();
				}
				
				AbstractWrapper<? extends GenericModel> wrapper = wrapperFact.getWrapper(typeDemande);
				if (wrapper == null) {
					throw new UnexpectedTypeLineException();
				}
				// load the row
				wrapper.loadRow(row);

				// validate the row
				if (!wrapper.validateRow()) {
					allLineOK = false;
				}
			} catch (JiraGeneralException e) {
				LOG.error("Row <" + row.getRowNum() + "> JIRA has raised an exception. I can't do anything for you...",e);
				allLineOK = false;
			} catch (UnexpectedTypeLineException e) {
				LOG.error("Row <" + row.getRowNum() + "> The typeDemande value is not support by the application", e);
				allLineOK = false;
			}
		}
		
		if (atLeastOneDataFound == false) {
			//no line in the excel file
			LOG.error("No line has been found in the excel file. Please check your second excel sheet");
			allLineOK = false;
		}
		return allLineOK;
	}
}
