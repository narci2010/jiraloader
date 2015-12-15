package sopra.grenoble.jiraLoader.jira.dao.metadatas;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.Status;

import sopra.grenoble.jiraLoader.jira.dao.metadatas.utils.MetadataGenLoader;

@Service
public class JiraStatusLoader extends MetadataGenLoader<Status> {
	
	private static final Logger LOG = LoggerFactory.getLogger(JiraStatusLoader.class);

	@Autowired
	private JiraRestClient jiraConnection;

	@Autowired
	private ProgressMonitor pm;

	/**
	 * Default constructor
	 */
	public JiraStatusLoader() {
		super();
	}

	@Override
	public void loadElements() {
		LOG.info("Loading Status");
		List<Status> lstStatus = (List<Status>) jiraConnection.getMetadataClient().getStatuses(pm);
		for (Status st : lstStatus) {
			this.addElement(st.getName(), st);
			LOG.debug("Available status : " + st.getName());
		}
		LOG.info("Status loaded. " + lstStatus.size() + " has been loaded");
	}
}
