package sopra.grenoble.jiraLoaderCmdLine;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import sopra.grenoble.jiraLoader.JiraLoader;
import sopra.grenoble.jiraLoader.configurationbeans.JiraUserDatas;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.spring.ApplicationContextProvider;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;

@SpringBootApplication(scanBasePackageClasses={JiraLoader.class, ApplicationConfiguration.class}) // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Main {
	
	public static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	
	/**
	 * Main function
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		LOG.info("Starting the application");
		
		ConfigurableApplicationContext appContextLoc = SpringApplication.run(Main.class, args);
		appContextLoc.registerShutdownHook();
		LOG.info("Application is successfully initialised");
		
			
		//start command line
		loadCmdLineApplication(args);
	}
	
	/**
	 * Start the application in commande line
	 * @param appContext
	 * @param args
	 * @throws Exception 
	 */
	private static void loadCmdLineApplication(String[] args) throws Exception {
		LOG.info("Starting application in command line MODE");

		//set configuration bean
		JiraUserDatas juc = ApplicationContextProvider.getApplicationContext().getBean(JiraUserDatas.class);
		
		LOG.info("Jira configuration : username=" + juc.getUsername());
		LOG.info("Jira configuration : password=" + juc.getPassword());
		LOG.info("Jira configuration : projectName=" + juc.getProjectName());
		LOG.info("Jira configuration : uri=" + juc.getUri());
		LOG.info("Jira excel file path : path=" + juc.getExcelJiraFilePath());
		
		
		//test username and password
		if (juc.getUsername() == null || juc.getPassword() == null) {
			LOG.error("You have to specify the username or password");
			System.exit(1);
		}
		
		//test project name
		if (juc.getProjectName() == null) {
			LOG.error("You have to specify the projectName");
			System.exit(1);
		}
		
		//test jira url
		if (juc.getUri() == null) {
			LOG.error("You have to specify the jira uri");
			System.exit(1);
		}
		
		//test jiraExcel file
		if (juc.getExcelJiraFilePath() == null) {
			LOG.error("You have to specify the excel jira file path");
			System.exit(1);
		}
		
		
		//opening connection
		IJiraRestClientV2 jiraConnection = ApplicationContextProvider.getApplicationContext().getBean(IJiraRestClientV2.class);
		jiraConnection.openConnection();

		//load metadata
		JiraLoader jiraLoader = ApplicationContextProvider.getApplicationContext().getBean(JiraLoader.class);

		//run application
		try {
			jiraLoader.loadingFile(juc.getExcelJiraFilePath());
		} catch (IOException e) {
			LOG.error("Error while loading excel file in JIRA", e);
			System.exit(1);
		}
		LOG.info("Excel file has been successfully loaded");
	}
	
}
