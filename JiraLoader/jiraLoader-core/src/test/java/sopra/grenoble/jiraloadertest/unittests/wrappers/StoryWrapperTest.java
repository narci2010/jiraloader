package sopra.grenoble.jiraloadertest.unittests.wrappers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.ProgressMonitor;

import sopra.grenoble.jiraLoader.excel.dto.Story;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;
import sopra.grenoble.jiraLoader.jira.dao.project.impl.IssueStoryAndSubTaskService;
import sopra.grenoble.jiraLoader.wrappers.StoryWrapper;
import sopra.grenoble.jiraLoader.wrappers.WrapperFactory;
import sopra.grenoble.jiraloadertest.mock.StoryServiceMock;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(StoryWrapperTest.Config.class)
public class StoryWrapperTest {
	

	@Configuration
	@ComponentScan(basePackages={
				"sopra.grenoble.jiraLoader"}, 
//			excludeFilters={ @ComponentScan.Filter(type = FilterType.REGEX, pattern = "sopra\\.grenoble\\.jiraLoader\\.jira\\.dao\\.project\\.impl.*")})
			excludeFilters={ @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=IssueStoryAndSubTaskService.class)})
    static class Config {

        // this bean will be injected into the OrderServiceTest class
		@Bean
		public IIssueService mockIssueService() {
			return new StoryServiceMock();
		}
		
		@Bean
		public ProgressMonitor pm() {
			return new NullProgressMonitor();
		}
    }
	
	
	@Autowired
	private StoryWrapper wrapper;
	
	
	@Autowired private JiraRestClient jiraConn;
	
	@Autowired
	private IIssueService storySrv;
	
	@Value("${default.project.name}")
	private String projectTestName;
	
	@Value("${test.component.name}")
	private String componentName;
	
	@Value("${test.OPAL_ODYSEE.version.name}")
	private String versionName;
	
	@Test
	public void insertInJira_newLine_Test() throws Exception {
//		WrapperFactory wrapperFact = new WrapperFactory();
//		wrapper = (StoryWrapper) wrapperFact.getWrapper("Story");
		
		Story s = new Story();
		s.epicName = "TOTO";
		s.versionName = versionName;
		s.resume = "resume";
		s.composantName = componentName;
		
		assertTrue(s.validate());
		
		wrapper.insertInJira(s);
		
		assertNotNull(s.key);
		storySrv.removeIssue(s.key, true);
	}
	
}
