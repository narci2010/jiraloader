package sopra.grenoble.jiraLoader.jira.dao.metadatas;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.IssueType;

import sopra.grenoble.jiraLoader.jira.dao.metadatas.utils.MetadataGenLoader;

@Service
public class JiraIssuesTypeLoader extends MetadataGenLoader<IssueType> {
	
	private static final Logger LOG = LoggerFactory.getLogger(JiraIssuesTypeLoader.class);
	
	public static final String JIRA_EPIC_ISSUE_TYPE_NAME = "Epic";
	public static final String JIRA_STORY_ISSUE_TYPE_NAME = "Story";

	@Autowired
	private JiraRestClient jiraConnection;

	@Autowired
	private ProgressMonitor pm;

	/**
	 * Default constructor
	 */
	public JiraIssuesTypeLoader() {
		super();
	}

	@Override
	public void loadElements() {
		LOG.info("Loading JiraIssueType");
		List<IssueType> lstIssueTypes = (List<IssueType>) jiraConnection.getMetadataClient().getIssueTypes(pm);
		for (IssueType is : lstIssueTypes) {
			this.addElement(is.getName(), is);
		}
		LOG.info("JiraIssueType loaded. " + lstIssueTypes.size() + " has been loaded");
	}
}
