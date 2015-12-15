package sopra.grenoble.jiraLoader.jira.dao.metadatas;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.Field;

import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.utils.MetadataGenLoader;

@Service
public class JiraFieldLoader extends MetadataGenLoader<Field> {
	
	private static final Logger LOG = LoggerFactory.getLogger(JiraFieldLoader.class);
	
	public static final String EPIC_LINK_FIELD_NAME = "Epic Link ";
	public static final String EPIC_NAME_FIELD_NAME = "Epic Name ";
	public static final String ESTIMATION_ORIGINAL_NAME = "Estimation originale";
	
	@Autowired
	private IJiraRestClientV2 jiraConnection;

	@Autowired
	private ProgressMonitor pm;

	/**
	 * Default constructor
	 */
	public JiraFieldLoader() {
		super();
	}

	@Override
	public void loadElements() {
		LOG.info("Loading Field from JIRA");
		List<Field> fields = (List<Field>) jiraConnection.getMetadataClientV2().getFields(pm);
		for (Field is : fields) {
			this.addElement(is.getName(), is);
			LOG.info("Available Field : " + is.getName());
		}
		LOG.info("Fields loaded. " + fields.size() + " have been loaded");
	}
}
