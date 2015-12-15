package sopra.grenoble.jiraLoader.unittests.jira.dao.project.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.domain.BasicProject;

import sopra.grenoble.jiraLoader.ApplicationConfiguration;
import sopra.grenoble.jiraLoader.exceptions.ProjectNotFoundException;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.project.impl.ProjectService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class ProjectServiceTest {

	@Autowired private ProjectService projectSrv;	
	@Autowired private IJiraRestClientV2 jiraConnection;
	
	@Value("${default.project.name}")
	private String projectName;
	
	@Before
	public void openingConnection() throws URISyntaxException {
		jiraConnection.openConnection();
	}
	
	@After
	public void cleanData() {
		projectSrv.resetProjectCache();
	}
	
	@Test
	public void isExistTest() {
		assertFalse(projectSrv.isProjectExist("TOTO"));
		assertTrue(projectSrv.isProjectExist(projectName));
	}
	
	@Test(expected=ProjectNotFoundException.class)
	public void getByBadName() throws ProjectNotFoundException {
		projectSrv.getProjectByName("TOTO");
	}
	
	@Test(expected=ProjectNotFoundException.class)
	public void getByBadKey() throws ProjectNotFoundException {
		projectSrv.getProjectByKey("KK");
	}
	
	@Test
	public void getByName() throws ProjectNotFoundException {
		BasicProject bp = projectSrv.getProjectByName(projectName);
		assertNotNull(bp);
		assertNotNull(bp.getId());
		assertNotNull(bp.getKey());
		assertEquals("Project name must be the same", projectName, bp.getName());
	}
}
