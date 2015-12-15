package sopra.grenoble.jiraLoader.unittests.jira.dao.project.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;

import sopra.grenoble.jiraLoader.ApplicationConfiguration;
import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraFieldLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueEpicService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class IssueEpicServiceTest {

	@Autowired
	private IIssueEpicService epicSrv;
	
	@Value("${default.project.name}")
	private String projectTestName;
	

	@Value("${test.component.name}")
	private String componentName;
	

	@Autowired private JiraIssuesTypeLoader issueTypeLoader;
	@Autowired private JiraFieldLoader fieldLoader;
	@Autowired private IJiraRestClientV2 jiraConnection;
	private static boolean elementLoaded = false;
	
	@Before
	public void loadIssueTypeFromJira() throws URISyntaxException {
		if (!elementLoaded) {
			jiraConnection.openConnection();
			issueTypeLoader.loadElements();
			fieldLoader.loadElements();
			elementLoaded=true;
		}
	}
	
	@Test(expected=IssueNotFoundException.class)
	public void getNotExistEpic() throws Exception {
		epicSrv.getByKey("badkey", projectTestName);
	}
	
	@Test
	public void isExistFailedTest() {
		assertFalse(epicSrv.isExist("NOTEXISTKEY", projectTestName));
	}
	
	@Test
	public void createEpicAndDelete() throws JiraGeneralException {
		BasicIssue bi = epicSrv.createEpic(projectTestName, "EPIC TEST DEV", componentName);
		assertNotNull(bi);
		
		try {
			//test if exists
			assertTrue(epicSrv.isExist(bi.getKey(), projectTestName));
			//test that not exist in another project
			assertFalse(epicSrv.isExist(bi.getKey(), "TESTPROJECT"));
			//get the epic value
			Issue is = epicSrv.getByKey(bi.getKey(), projectTestName);
			assertNotNull(is);
			assertEquals(is.getSummary(), "EPIC TEST DEV");
			assertEquals(componentName, is.getComponents().iterator().next().getName());
		} finally {
			epicSrv.removeIssue(bi.getKey(), true);
			assertFalse(epicSrv.isExist(bi.getKey(), projectTestName));
		}
	}

}
