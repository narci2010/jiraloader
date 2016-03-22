package sopra.grenoble.jiraloadertest.mock;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.JiraIssueTypeException;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class StoryServiceMock implements IIssueService {

	@Override
	public Issue getByKey(String key, String projectName) throws IssueNotFoundException, JiraIssueTypeException {

		return null;
	}

	@Override
	public void removeIssue(String key, boolean deleteSubTasks) throws IssueNotFoundException {

	}

	@Override
	public boolean isExist(String key, String projectName) {
		return false;
	}

	@Override
	public BasicIssue createSubTask(String projectName, String parentKey, String issueTypeName, String resume,
			String description, String priority, String estimation, String componentName) throws JiraGeneralException {
		return null;
	}

	@Override
	public BasicIssue createStory(String projectName, String epicName, String versionName, String clientReference, String resume,
								  String description, String priority, String componentName, String versionCorrected, String versionAffected) throws JiraGeneralException {
		BasicIssue bi = null;
		try {
			bi = new BasicIssue(new URI("uri"), "1", 1L);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return bi;
	}

	@Override
	public Optional<BasicIssue> getByStartingName(String storyName, String projectName) {
		try {
			return Optional.of(new BasicIssue(new URI("uri"), "1", 1L));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public void updateIssue(String issueKey, String projectName, String priority)
			throws IssueNotFoundException, JiraGeneralException {
		// TODO Auto-generated method stub
		
	}

}
