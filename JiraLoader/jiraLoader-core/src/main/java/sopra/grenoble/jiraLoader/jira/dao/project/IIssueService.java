package sopra.grenoble.jiraLoader.jira.dao.project;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;

import java.util.Optional;

public interface IIssueService extends IIssueGenericService {
	
	BasicIssue createSubTask(String projectName, String parentKey, String issueTypeName, String resume,
			String description, String priority, String estimation, String componentName) throws JiraGeneralException;

	BasicIssue createStory(String projectName, String epicName, String versionName, String clientReference, String resume,
						   String description, String priority, String componentName, String versionCorrected, String versionAffected)
					throws JiraGeneralException;

	/**
	 * Return a basicissue if found. If not found, return a null value in the {@link Optional} container.
	 * @param storyName
	 * @param projectName
	 * @return
	 */
	Optional<BasicIssue> getByStartingName(String storyName, String projectName);
	
	void updateIssue(String issueKey, String projectName, String priority)
			throws IssueNotFoundException, JiraGeneralException;

}
