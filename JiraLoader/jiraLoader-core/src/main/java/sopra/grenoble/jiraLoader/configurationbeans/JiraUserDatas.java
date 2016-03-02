package sopra.grenoble.jiraLoader.configurationbeans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JiraUserDatas {

	/**
	 * Username used for the Jira connection
	 */
	@Value("${jira.username}")
	private String username;

	/**
	 * Password used for the Jira connection
	 */
	@Value("${jira.password}")
	private String password;

	/**
	 * The Jira URL endpoint
	 */
	@Value("${jira.uri}")
	private String uri;

	/**
	 * The Jira project name to update 
	 */
	@Value("${project.name}")
	private String projectName;
	
	
	/**
	 * The excel file path
	 */
	@Value("${excelFilePath}")
	private String excelJiraFilePath;
	
	
	/**
	 * To create task and sub tasks, the program needs to rely these both elements
	 * to a story. This parameter is used to keep in memory the last story used while the injection.
	 */
	private String lastStoryKey;

	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getLastStoryKey() {
		return lastStoryKey;
	}

	public void setLastStoryKey(String lastStoryKey) {
		this.lastStoryKey = lastStoryKey;
	}

	public String getExcelJiraFilePath() {
		return excelJiraFilePath;
	}

	public void setExcelJiraFilePath(String excelJiraFilePath) {
		this.excelJiraFilePath = excelJiraFilePath;
	}
}
