package sopra.grenoble.jiraloadertest.unittests.jira.dao.project.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.domain.Version;

import sopra.grenoble.jiraLoader.exceptions.ProjectNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.VersionNotFoundException;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.project.IVersionService;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class VersionServiceTest {

	private static final Logger LOG = LoggerFactory.getLogger(VersionServiceTest.class);
	
	@Autowired private IVersionService vSrv;
	@Autowired private IJiraRestClientV2 jiraConnection;

	@Value("${version.project.name}")
	private String projectName;
	
	@Before
	public void openingConnection() throws URISyntaxException {
		jiraConnection.openConnection();
	}
	
	@Test
	public void listAllProjectVersionTest() throws ProjectNotFoundException {
		List<Version> versions = (List<Version>) vSrv.listFromProject(projectName);
		assertNotNull(versions);
		assertNotEquals(0, versions.size());
		for (Version version : versions) {
			LOG.info("Version from project <" + projectName + "> => " + version.getName());
		}
	}
	
	@Test(expected=VersionNotFoundException.class)
	public void getNonExistingVersion() throws VersionNotFoundException {
		vSrv.getVersion(projectName, "UNKNOWN");
	}
	
	
	@Test
	public void testVersionCreate() throws ProjectNotFoundException, VersionNotFoundException {
		Version vCreated = vSrv.createVersion("Version Test", "Description version", projectName, new DateTime());
		assertNotNull(vCreated);

		//on verifie que celle ci n'existe plus
		Version v = vSrv.getVersion(projectName, "Version Test");
		assertNotNull(v);
		assertEquals("Version Test", v.getName());
		assertEquals("Description version", v.getDescription());
		
		//suppression de la version
		vSrv.removeVersion(projectName, "Version Test");
		
		//on verifie qu'elle est supprimé
		try {
			vSrv.getVersion(projectName, "Version Test");
			fail("An exception should have been raised");
		} catch (VersionNotFoundException e) {
			
		}
	}

}
