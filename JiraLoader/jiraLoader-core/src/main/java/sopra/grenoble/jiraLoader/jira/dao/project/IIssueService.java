package sopra.grenoble.jiraLoader.jira.dao.project;

import java.util.Optional;

import com.atlassian.jira.rest.client.domain.BasicIssue;

import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;

public interface IIssueService extends IIssueGenericService {
	
	BasicIssue createSubTask(String projectName, String parentKey, String issueTypeName, String resume,
			String description, String priority, String estimation, String componentName) throws JiraGeneralException;

	BasicIssue createStory(String projectName, String epicName, String versionName, String resume,
			String description, String priority, String componentName)
					throws JiraGeneralException;

	Optional<BasicIssue> getByStartingName(String storyName, String projectName);

}
