package sopra.grenoble.jiraLoader.jira.dao.project;

import com.atlassian.jira.rest.client.domain.User;

import sopra.grenoble.jiraLoader.exceptions.JiraUserNotFoundException;

public interface IUserService {

	boolean isExist(String username);
	
	User getUserByName(String username) throws JiraUserNotFoundException;
}
