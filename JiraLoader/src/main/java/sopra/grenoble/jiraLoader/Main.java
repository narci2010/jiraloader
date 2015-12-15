package sopra.grenoble.jiraLoader;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.spring.ApplicationContextProvider;
import sopra.grenoble.jiraLoaderfx.JiraLoaderFx;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
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
		
		if (args.length == 0) {
			loadJavaFxApplication();
		}
		else {
			if (args.length != 1) {
				LOG.error("JiraLoader : No parameter has been passed. The excel file path should be passed");
				System.out.println("JiraLoader : No parameter has been passed. The excel file path should be passed");
				System.exit(0);
			}
			
			//start command line
			loadCmdLineApplication(args);
		}
	}
	
	/**
	 * Start the application in commande line
	 * @param appContext
	 * @param args
	 * @throws Exception 
	 */
	private static void loadCmdLineApplication(String[] args) throws Exception {
		LOG.info("Starting application in command line MODE");
		
		//opening connection
		IJiraRestClientV2 jiraConnection = ApplicationContextProvider.getApplicationContext().getBean(IJiraRestClientV2.class);
		jiraConnection.openConnection();

		//load metadata
		JiraLoader jiraLoader = ApplicationContextProvider.getApplicationContext().getBean(JiraLoader.class);

		//run application
		String excelFilePath = args[0];
		try {
			jiraLoader.loadingFile(excelFilePath);
		} catch (IOException e) {
			LOG.error("Error while loading excel file in JIRA", e);
			System.exit(1);
		}
		LOG.info("Excel file has been successfully loaded");
	}
	
	/**
	 * Start the application in JAVA FX MODE
	 * @param appContext
	 * @param args
	 */
	private static void loadJavaFxApplication() {
		LOG.info("Starting application in java FX MODE");
		JiraLoaderFx.launchJavaFx();
	}
	
}
