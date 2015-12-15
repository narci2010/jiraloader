package sopra.grenoble.jiraLoader.jira.dao.metadatas;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.Priority;

import sopra.grenoble.jiraLoader.jira.dao.metadatas.utils.MetadataGenLoader;

@Service
public class JiraPriorityLoader extends MetadataGenLoader<Priority> {
	
	private static final Logger LOG = LoggerFactory.getLogger(JiraPriorityLoader.class);

	@Autowired
	private JiraRestClient jiraConnection;

	@Autowired
	private ProgressMonitor pm;

	/**
	 * Default constructor
	 */
	public JiraPriorityLoader() {
		super();
	}

	@Override
	public void loadElements() {
		LOG.info("Loading Status");
		List<Priority> lstpriorities = (List<Priority>) jiraConnection.getMetadataClient().getPriorities(pm);
		for (Priority p : lstpriorities) {
			this.addElement(p.getName(), p);
		}
		LOG.info("Status loaded. " + lstpriorities.size() + " has been loaded");
	}
}
