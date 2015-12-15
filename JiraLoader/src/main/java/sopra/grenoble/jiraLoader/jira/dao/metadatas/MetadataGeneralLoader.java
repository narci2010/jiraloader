package sopra.grenoble.jiraLoader.jira.dao.metadatas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetadataGeneralLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(MetadataGeneralLoader.class);

	@Autowired private JiraIssuesTypeLoader jiraIssuesTypeLoader;
	@Autowired private JiraPriorityLoader jiraPriorityLoader;
	@Autowired private JiraStatusLoader jiraStatusLoader;
	@Autowired private JiraFieldLoader fieldLoader;
	
	/**
	 * Init all loaders synchronously
	 */
	public void initLoaderSynchronously() {
		LOG.info("Starting loading metadata synchronously");
		jiraIssuesTypeLoader.loadElements();
		jiraPriorityLoader.loadElements();
		jiraStatusLoader.loadElements();
		fieldLoader.loadElements();
		LOG.info("Metadatas have been successfully loaded");
	}
	
	@Deprecated
	public void initLoaderAsynchronously() {
		throw new RuntimeException("Not yet implemented");
	}
}
