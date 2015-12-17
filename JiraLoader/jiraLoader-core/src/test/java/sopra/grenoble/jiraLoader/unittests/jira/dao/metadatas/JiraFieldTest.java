package sopra.grenoble.jiraLoader.unittests.jira.dao.metadatas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import sopra.grenoble.jiraLoader.ApplicationConfiguration;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraFieldLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationConfiguration.class)
public class JiraFieldTest {
	
	@Autowired
	private JiraFieldLoader jiraFieldLoader;
	
	@Autowired private IJiraRestClientV2 jiraConnection;
	
	@Before
	public void openingConnection() throws URISyntaxException {
		jiraConnection.openConnection();
	}
	
	
	@After
	public void cleanElements() {
		jiraFieldLoader.cleanAllElements();
	}

	@Test
	public void testNotNull() {
		assertNotNull(jiraFieldLoader);
		assertEquals("No element must be in the hashmap", 0, jiraFieldLoader.countElements());
	}
	
	@Test
	public void testLoadedElement() {
		jiraFieldLoader.loadElements();
		assertNotEquals("At least one element must be in the hashmap", 0, jiraFieldLoader.countElements());
	}

}
