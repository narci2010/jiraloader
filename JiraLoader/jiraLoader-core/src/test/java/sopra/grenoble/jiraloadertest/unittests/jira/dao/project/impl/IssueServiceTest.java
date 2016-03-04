package sopra.grenoble.jiraloadertest.unittests.jira.dao.project.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Version;

import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraEpicNotFound;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.VersionNotFoundException;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraFieldLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraPriorityLoader;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueEpicService;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;
import sopra.grenoble.jiraLoader.jira.dao.project.IVersionService;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class IssueServiceTest {

	@Autowired
	private IIssueService issueSrv;
	
	@Autowired 
	private IIssueEpicService epicSrv;
	
	@Value("${default.project.name}")
	private String projectTestName;
	
	@Value("${test.component.name}")
	private String componentName;
	
	@Value("${test.OPAL_ODYSEE.version.name}")
	private String versionName;
	
	@Autowired private JiraIssuesTypeLoader issueTypeLoader;
	@Autowired private JiraFieldLoader fieldLoader;
	@Autowired private IVersionService vService;
	@Autowired private JiraPriorityLoader priorityLoader;
	@Autowired private IJiraRestClientV2 jiraConnection;
	private static boolean elementLoaded = false;
	private static Version v;
	private static BasicIssue epicIssue;
	
	@Before
	public void initJira() throws JiraGeneralException, URISyntaxException {
		if (!elementLoaded) {
			jiraConnection.openConnection();
			issueTypeLoader.loadElements();
			fieldLoader.loadElements();
			priorityLoader.loadElements();
			v = vService.getVersion(projectTestName, versionName);
			assertNotNull(v);
			elementLoaded = true;
		}
		//creating epic
		epicIssue = epicSrv.createEpic(projectTestName, "EpicTestSummary", componentName);
	}
	
	@After
	public void cleanJira() throws VersionNotFoundException, JiraGeneralException {
		epicSrv.removeIssue(epicIssue.getKey(), true);
	}
		
	
	@Test
	public void createStoryAndDeleteIssue() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "resume", "description", "urgent", componentName);
		assertNotNull(bi);
		issueSrv.removeIssue(bi.getKey(), true);
	}
	
	@Test
	public void createStoryAndSubTaskAndDeleteAll() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "resume", "description", "urgent", componentName);
		assertNotNull(bi);
		//Create sub task
		try {
			BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", "urgent", "1d", componentName);
			assertNotNull(subTask);
		} finally {
			issueSrv.removeIssue(bi.getKey(), true);
		}
	}
	
	@Test
	public void createStoryAndSubTaskWithoutPriority() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "resume", "description", null, componentName);
		assertNotNull(bi);
		//Create sub task
		try {
			BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", null, "1d", componentName);
			assertNotNull(subTask);
		} finally {
			issueSrv.removeIssue(bi.getKey(), true);
		}
	}
	
	@Test
	public void createStoryAndSubTaskWithoutDescriptionAndDeleteAll() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "resume", null, "urgent", componentName);
		assertNotNull(bi);
		//Create sub task
		try {
			BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", null, "urgent", "1d", componentName);
			assertNotNull(subTask);
		} finally {
			issueSrv.removeIssue(bi.getKey(), true);
		}
	}
	
	@Test(expected=IssueNotFoundException.class)
	public void createSubTaskWithoutParent() throws JiraGeneralException {
		issueSrv.createSubTask(projectTestName, null, "Sous-tâche", "sous tach resume", "sous tach desc", "urgent", "1d", componentName);
	}
	
	@Test
	public void createStoryWithEpic() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "resume", "description", "urgent", componentName);
		assertNotNull(bi);
		issueSrv.removeIssue(bi.getKey(), true);
	}
	
	@Test
	public void createStoryWithoutEpic() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "resume", "description", "urgent", componentName);
		assertNotNull(bi);
		issueSrv.removeIssue(bi.getKey(), true);
	}
	
	@Test
	public void createStoryWithEpicWithoutDescription() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "resume", null, "urgent", componentName);
		assertNotNull(bi);
		issueSrv.removeIssue(bi.getKey(), true);
	}
	
	@Test
	public void createStoryWithoutPriority() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "resume", "description", null, componentName);
		assertNotNull(bi);
		issueSrv.removeIssue(bi.getKey(), true);
	}
	
	@Test
	public void createStoryWithVersion() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, null, v.getName(), "resume", "description", "urgent", componentName);
		assertNotNull(bi);
		issueSrv.removeIssue(bi.getKey(), true);
	}
	
	/**
	 * @throws JiraGeneralException
	 */
	@Test(expected=VersionNotFoundException.class)
	public void createStoryWithBadVersion() throws JiraGeneralException {
		issueSrv.createStory(projectTestName, null, "BADVERSION", "resume", "description", "urgent", componentName);
	}
	
	@Test
	public void createStoryWithSpecialChar() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, null, v.getName(), "resume\nOK", "description\nOK", "urgent", componentName);
		assertNotNull(bi);
		issueSrv.removeIssue(bi.getKey(), true);
	}
	
	@Test(expected=JiraEpicNotFound.class)
	public void createStoryWithBadEpics() throws JiraGeneralException {
		issueSrv.createStory(projectTestName, "LOLOLOLO", v.getName(), "resume", "description\nOK", "urgent", componentName);
	}

	@Test
	public void createStoryWithFullOptionAndSubTasksWithFullOption() throws JiraGeneralException {
		BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", v.getName(), "resume", "description", "urgent", componentName);
		assertNotNull(bi);
		//Create sub task
		try {
			BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", "urgent", "1d", componentName);
			assertNotNull(subTask);
		} finally {
			issueSrv.removeIssue(bi.getKey(), true);
		}
	}
	
	@Test(expected=NoSuchElementException.class)
	public void getByStartingName_notFoundTest() {
		Optional basicIssue = issueSrv.getByStartingName("TOTO", projectTestName);
		assertNotNull(basicIssue);
		basicIssue.get();
	}
	
	@Test
	public void getByStartingName_FoundTest() throws Exception {
		BasicIssue bi1 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO1 | test 1", "description", "urgent", componentName);
		BasicIssue bi2 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO2 | test 2", "description", "urgent", componentName);
		BasicIssue bi3 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO3 | test 3", "description", "urgent", componentName);
		
		try {
			Optional <BasicIssue> basicIssue = issueSrv.getByStartingName("XXX_TOTO2", projectTestName);
			assertNotNull(basicIssue);
			assertEquals("bi2 id should have been returned", bi2.getKey(), basicIssue.get().getKey());
		} finally {
			issueSrv.removeIssue(bi1.getKey(), true);
			issueSrv.removeIssue(bi2.getKey(), true);
			issueSrv.removeIssue(bi3.getKey(), true);
		}
	}
	
	@Test(expected=IssueNotFoundException.class)
	public void updateStory_notexist_Test() throws IssueNotFoundException, JiraGeneralException {
		issueSrv.updateIssue("1", projectTestName, null);
	}
	
	@Test
	public void updateStory_ok_Test() throws IssueNotFoundException, JiraGeneralException {
		//create one issue
		BasicIssue bi1 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO1 | test 1", "description", "urgent", componentName);
		
		try {
			//update the issue
			issueSrv.updateIssue(bi1.getKey(), projectTestName, "normal");
			//test
			Issue i = issueSrv.getByKey(bi1.getKey(), projectTestName);
			assertNotNull(i);
			assertEquals("priority is not correct", "normal", i.getPriority().getName());
		} finally {
			//delete the issue
			issueSrv.removeIssue(bi1.getKey(), true);
		}
	}
	
	@Test
	public void updateStory_nothing_Test() throws IssueNotFoundException, JiraGeneralException {
		//create one issue
		BasicIssue bi1 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO1 | test 1", "description", "urgent", componentName);
		
		try {
			//update the issue
			issueSrv.updateIssue(bi1.getKey(), projectTestName, "urgent");
			//test
			Issue i = issueSrv.getByKey(bi1.getKey(), projectTestName);
			assertNotNull(i);
			assertEquals("priority is not correct", "urgent", i.getPriority().getName());
		} finally {
			//delete the issue
			issueSrv.removeIssue(bi1.getKey(), true);
		}
	}
}
