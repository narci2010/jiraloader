package sopra.grenoble.jiraLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.ProgressMonitor;

/**
 * @author cmouilleron
 *
 */
@Configuration
@ComponentScan
public class ApplicationConfiguration {
	
	public static final Logger LOG = LoggerFactory.getLogger(ApplicationConfiguration.class);
	
	
	@Bean
	public ProgressMonitor pm() {
		return new NullProgressMonitor();
	}
}
