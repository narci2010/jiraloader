package sopra.grenoble.jiraLoader.jira.dao.project;

import com.atlassian.jira.rest.client.domain.Issue;

import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraIssueTypeException;

public abstract interface IIssueGenericService {

	/**
	 * Get an issue by key
	 * @param key
	 * @return
	 * @throws IssueNotFoundException if issue doesn't exist
	 * @throws JiraIssueTypeException  if issue is not an epic
	 */
	public Issue getByKey(String key, String projectName) throws IssueNotFoundException, JiraIssueTypeException;
	

	/**
	 * Delete from JIRA an issue
	 * @param key : the Epic key
	 * @param deleteSubTasks : delete subTasks in cascade
	 * @throws IssueNotFoundException
	 */
	public void removeIssue(String key, boolean deleteSubTasks) throws IssueNotFoundException;
	


	/**
	 * Return true if the issue with key "key" is in the project
	 * @param key
	 * @param projectName
	 * @return
	 */
	boolean isExist(String key, String projectName);


}
