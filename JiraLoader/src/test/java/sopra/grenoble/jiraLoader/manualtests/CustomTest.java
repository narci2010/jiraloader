package sopra.grenoble.jiraLoader.manualtests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssuelinksType;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Version;

import sopra.grenoble.jiraLoader.ApplicationConfiguration;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class CustomTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(CustomTest.class);

	@Autowired
	private IJiraRestClientV2 jiraConnection;
	
	@Autowired
	private ProgressMonitor pm;
	
	@Test
	public void getAllissuesLink() {
		Iterable<IssuelinksType> lstIssueLink = jiraConnection.getMetadataClient().getIssueLinkTypes(pm);
		for (IssuelinksType issuelinksType : lstIssueLink) {
			LOG.info("issue link =>" + issuelinksType.getName());
		}
	}
	
	@Test
	public void getIssue() {
		Issue issue = jiraConnection.getIssueClient().getIssue("OODY-715", pm);
		assertNotNull(issue);
		System.out.println(issue.toString());
	}
	
	@Test
	public void getAllProjectsAndVersionsAndComponent() {
		Iterable<BasicProject> lstProject = jiraConnection.getProjectClient().getAllProjects(pm);
		for (BasicProject basicProject : lstProject) {
			LOG.info("Project name =>" + basicProject.getName() + " uri =>" + basicProject.getSelf().toString());
			
			//list all versions of the project
			Project p = jiraConnection.getProjectClient().getProject(basicProject.getKey(), pm);
			for (Version v : p.getVersions()) {
				LOG.info("	Version in project => " + v.getName());
			}
			for (BasicComponent c : p.getComponents()) {
				LOG.info("	Component in project => " + c.getName());
			}
		}
	}
	
	@Test
	public void JQLTest() {
		String JQL = "project = OODY AND text ~ 'P1'";
		SearchResult results = jiraConnection.getSearchClient().searchJql(JQL, pm);
		assertNotNull(results);
	}
	
	@Test
	public void getAllFields() {
		Iterable<Field> fields = jiraConnection.getMetadataClientV2().getFields(pm);
		assertNotNull(fields);
	}
}
