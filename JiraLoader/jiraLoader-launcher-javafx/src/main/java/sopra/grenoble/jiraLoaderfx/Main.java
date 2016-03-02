package sopra.grenoble.jiraLoaderfx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import sopra.grenoble.jiraLoader.JiraLoader;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;

@SpringBootApplication(scanBasePackageClasses={JiraLoader.class, ApplicationConfiguration.class}) // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Main {
	
	public static final Logger LOG = LoggerFactory.getLogger(Main.class);
	
	
	/**
	 * Main function 
	 */
	public static void main(String[] args) throws Exception {
		LOG.info("Starting the application");
		
		ConfigurableApplicationContext appContextLoc = SpringApplication.run(Main.class, args);
		appContextLoc.registerShutdownHook();
		LOG.debug("Application is successfully initialised DEBUG");
		LOG.warn("Application is successfully initialised WARN");

		loadJavaFxApplication();
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
