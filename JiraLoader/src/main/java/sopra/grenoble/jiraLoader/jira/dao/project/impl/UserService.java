package sopra.grenoble.jiraLoader.jira.dao.project.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.User;

import sopra.grenoble.jiraLoader.exceptions.JiraUserNotFoundException;
import sopra.grenoble.jiraLoader.jira.dao.project.IUserService;

@Service
public class UserService implements IUserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private JiraRestClient jiraConnection;
	
	@Autowired
	private ProgressMonitor pm;

	@Override
	public boolean isExist(String username) {
		try {
			getUserByName(username);
		} catch (JiraUserNotFoundException e) {
			return false;
		}
		return true;
	}

	@Override
	public User getUserByName(String username) throws JiraUserNotFoundException {
		User u = null;
		try {
			u = jiraConnection.getUserClient().getUser(username, pm);
		} catch (RestClientException e) {
			LOG.error("Unable to retreive jira user", e);
			throw new JiraUserNotFoundException();
		}
		return u;
	}
	
	
}
