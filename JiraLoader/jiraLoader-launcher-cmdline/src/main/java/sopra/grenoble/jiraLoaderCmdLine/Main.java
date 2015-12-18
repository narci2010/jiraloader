package sopra.grenoble.jiraLoaderCmdLine;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import sopra.grenoble.jiraLoader.JiraLoader;
import sopra.grenoble.jiraLoader.JiraUserConfiguration;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.spring.ApplicationContextProvider;

@SpringBootApplication(scanBasePackageClasses=JiraLoader.class) // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Main {
	
	public static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	/**
	 * Usage function
	 */
	private static void Usage() {
		LOG.error("JiraLoader : bad input parameters. Five parameter must be passed");
		LOG.error("	1 - Jira URL : try http://forge.corp.sopra/jira1");
		LOG.error("	2 - username");
		LOG.error("	3 - password");
		LOG.error("	4 - JIRA project name");
		LOG.error("	5 - Excel file path");
	}
	
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
		
		
		if (args.length != 5) {
			LOG.error("JiraLoader : No parameter has been passed. The excel file path should be passed");
			Usage();
			System.exit(0);
		}
			
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
		JiraUserConfiguration juc = ApplicationContextProvider.getApplicationContext().getBean(JiraUserConfiguration.class);
		
		juc.setUri(args[0]);
		juc.setUsername(args[1]);
		juc.setPassword(args[2]);
		juc.setProjectName(args[3]);
		
		LOG.info("Jira project name = " + juc.getProjectName());
		
		String excelFilePath = args[4];
		
		
		//opening connection
		IJiraRestClientV2 jiraConnection = ApplicationContextProvider.getApplicationContext().getBean(IJiraRestClientV2.class);
		jiraConnection.openConnection();

		//load metadata
		JiraLoader jiraLoader = ApplicationContextProvider.getApplicationContext().getBean(JiraLoader.class);

		//run application
		try {
			jiraLoader.loadingFile(excelFilePath);
		} catch (IOException e) {
			LOG.error("Error while loading excel file in JIRA", e);
			System.exit(1);
		}
		LOG.info("Excel file has been successfully loaded");
	}
	
}
