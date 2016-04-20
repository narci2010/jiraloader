package sopra.grenoble.jiraLoader.jira.dao.project;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.JiraIssueTypeException;

import java.util.Optional;

public interface IIssueEpicService extends IIssueGenericService {


	/**
	 * @param projectName
	 * @param epicName
	 * @param componentName
	 * @return
	 * @throws JiraGeneralException 
	 */
	public BasicIssue createEpic(String projectName, String epicName, String componentName) throws JiraGeneralException;

	/**
	 * Return an epic issue based on the name.
	 * @param epicName
	 * @return
	 * @throws JiraIssueTypeException 
	 * @throws IssueNotFoundException 
	 */
	public Optional<BasicIssue> getByName(String epicName, String projectName) throws IssueNotFoundException, JiraIssueTypeException;
}
