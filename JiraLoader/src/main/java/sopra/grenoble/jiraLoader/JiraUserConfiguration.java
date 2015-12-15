package sopra.grenoble.jiraLoader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JiraUserConfiguration {


	@Value("${jira.username}")
	private String username;

	@Value("${jira.password}")
	private String password;

	@Value("${jira.uri}")
	private String uri;

	@Value("${project.name}")
	private String projectName;
	
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

}
