package sopra.grenoble.jiraLoader.jira.connection;

import java.net.URISyntaxException;

import com.atlassian.jira.rest.client.JiraRestClient;

import sopra.grenoble.jiraLoader.jira.parsers.IMetadataRestClientV2;

public interface IJiraRestClientV2 extends JiraRestClient {

	public IMetadataRestClientV2 getMetadataClientV2();
	
	public void openConnection() throws URISyntaxException;
}
