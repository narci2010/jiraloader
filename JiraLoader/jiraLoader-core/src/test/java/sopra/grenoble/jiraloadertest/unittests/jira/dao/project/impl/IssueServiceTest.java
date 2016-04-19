package sopra.grenoble.jiraloadertest.unittests.jira.dao.project.impl;

import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sopra.grenoble.jiraLoader.exceptions.*;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraFieldLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraPriorityLoader;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueEpicService;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;
import sopra.grenoble.jiraLoader.jira.dao.project.IVersionService;
import sopra.grenoble.jiraLoaderconfiguration.ApplicationConfiguration;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    @Autowired
    private JiraIssuesTypeLoader issueTypeLoader;
    @Autowired
    private JiraFieldLoader fieldLoader;
    @Autowired
    private IVersionService vService;
    @Autowired
    private JiraPriorityLoader priorityLoader;
    @Autowired
    private IJiraRestClientV2 jiraConnection;
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
        BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryAndSubTaskAndDeleteAll() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        //Create sub task
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", "normal", "1d", componentName, null);
            assertNotNull(subTask);
        } finally {
            issueSrv.removeIssue(bi.getKey(), true);
        }
    }

    @Test
    public void createStoryAndSubTaskWithoutPriority() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", "description", null, componentName, null, null);
        assertNotNull(bi);
        //Create sub task
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", null, "1d", componentName, null);
            assertNotNull(subTask);
        } finally {
            issueSrv.removeIssue(bi.getKey(), true);
        }
    }

    @Test
    public void createStoryAndSubTaskWithoutDescriptionAndDeleteAll() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", null, "normal", componentName, null, null);
        assertNotNull(bi);
        //Create sub task
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", null, "normal", "1d", componentName, null);
            assertNotNull(subTask);
        } finally {
            issueSrv.removeIssue(bi.getKey(), true);
        }
    }

    @Test(expected = IssueNotFoundException.class)
    public void createSubTaskWithoutParent() throws JiraGeneralException {
        issueSrv.createSubTask(projectTestName, null, "Sous-tâche", "sous tach resume", "sous tach desc", "normal", "1d", componentName, null);
    }

    @Test
    public void createStoryWithEpic() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryWithoutEpic() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryWithEpicWithoutDescription() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "clientRef", "resume", null, "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryWithoutPriority() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "clientRef", "resume", "description", null, componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryWithVersion() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, v.getName(), "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    /**
     * @throws JiraGeneralException
     */
    @Test(expected = VersionNotFoundException.class)
    public void createStoryWithBadVersion() throws JiraGeneralException {
        issueSrv.createStory(projectTestName, null, "BADVERSION", "clientRef", "resume", "description", "normal", componentName, null, null);
    }

    @Test
    public void createStoryWithSpecialChar() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, v.getName(), "clientRef", "resume\nOK", "description\nOK", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test(expected = JiraEpicNotFound.class)
    public void createStoryWithBadEpics() throws JiraGeneralException {
        issueSrv.createStory(projectTestName, "LOLOLOLO", v.getName(), "clientRef", "resume", "description\nOK", "normal", componentName, null, null);
    }

    @Test
    public void createStoryWithFullOptionAndSubTasksWithFullOption() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", v.getName(), "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        //Create sub task
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", "normal", "1d", componentName, null);
            assertNotNull(subTask);
        } finally {
            issueSrv.removeIssue(bi.getKey(), true);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void getByStartingName_notFoundTest() throws JiraIssueTypeException, IssueNotFoundException {
        Optional basicIssue = issueSrv.getByStartingName("TOTO", projectTestName);
        assertNotNull(basicIssue);
        basicIssue.get();
    }

    @Test
    public void getByStartingName_FoundTest() throws Exception {
        BasicIssue bi1 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO1", "XXX_TOTO1 | test 1", "description", "normal", componentName, null, null);
        BasicIssue bi2 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO2", "XXX_TOTO2 | test 2", "description", "normal", componentName, null, null);
        BasicIssue bi3 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "XXX_TOTO3", "XXX_TOTO3 | test 3", "description", "normal", componentName, null, null);

        try {
            Optional<BasicIssue> basicIssue = issueSrv.getByStartingName("XXX_TOTO2 | test 2", projectTestName);
            assertNotNull(basicIssue);
            assertEquals("bi2 id should have been returned", bi2.getKey(), basicIssue.get().getKey());
        } finally {
            issueSrv.removeIssue(bi1.getKey(), true);
            issueSrv.removeIssue(bi2.getKey(), true);
            issueSrv.removeIssue(bi3.getKey(), true);
        }
    }

    @Test(expected = IssueNotFoundException.class)
    public void updateStory_notexist_Test() throws IssueNotFoundException, JiraGeneralException {
        issueSrv.updateIssue("1", projectTestName, null);
    }

    @Test
    public void updateStory_ok_Test() throws IssueNotFoundException, JiraGeneralException {
        //create one issue
        BasicIssue bi1 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "clientRef", "XXX_TOTO1 | test 1", "description", "normal", componentName, null, null);

        try {
            //update the issue
            issueSrv.updateIssue(bi1.getKey(), projectTestName, "non-prioritaire");
            //test
            Issue i = issueSrv.getByKey(bi1.getKey(), projectTestName);
            assertNotNull(i);
            assertEquals("priority is not correct", "non-prioritaire", i.getPriority().getName());
        } finally {
            //delete the issue
            issueSrv.removeIssue(bi1.getKey(), true);
        }
    }

    @Test
    public void updateStory_nothing_Test() throws IssueNotFoundException, JiraGeneralException {
        //create one issue
        BasicIssue bi1 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "clientRef", "XXX_TOTO1 | test 1", "description", "normal", componentName, null, null);

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
    public void updateSubTask_ok_Test() throws IssueNotFoundException, JiraGeneralException {
        //create one issue
        BasicIssue bi1 = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "clientRef", "XXX_TOTO1 | test 1", "description", "normal", componentName, null, null);

        try {
            //update the issue
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi1.getKey(), "Sous-tâche", "sous tache resume", "sous tache description", "normal", "1d", componentName, null);
            issueSrv.updateIssue(bi1.getKey(), projectTestName, "non-prioritaire");
            //test
            Issue i = issueSrv.getByKey(bi1.getKey(), projectTestName);
            assertNotNull(i);
            assertEquals("priority is not correct", "non-prioritaire", i.getPriority().getName());
            issueSrv.updateIssue(subTask.getKey(), projectTestName, "non-prioritaire");
            assertEquals("non-prioritaire", issueSrv.getByKey(subTask.getKey(), projectTestName).getPriority().getName());
        } finally {
            //delete the issue
            issueSrv.removeIssue(bi1.getKey(), true);
        }
    }

    @Test
    public void createStoryWithoutClientReference() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", v.getName(), null, "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryWithClientReference() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", v.getName(), "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryWithAffectedVersion() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", v.getName(), "ref client", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void createStoryWithCorrectedVersion() throws JiraGeneralException {
        BasicIssue bi = issueSrv.createStory(projectTestName, "EpicTestSummary", null, "ref client", "resume", "description", "normal", componentName, v.getName(), null);
        assertNotNull(bi);
        issueSrv.removeIssue(bi.getKey(), true);
    }

    @Test
    public void getSubTaskByNameTestOk() throws Exception {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        //Create sub task
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", "normal", "1d", componentName, null);
            assertNotNull(subTask);
            assertEquals(subTask.getKey(), issueSrv.getSubTaskByStartingName("sous tach resume", projectTestName).get().getKey());
        } finally {
            issueSrv.removeIssue(bi.getKey(), true);
        }
    }

    @Test
    public void getSubTaskByNameTestNotFound() throws Exception {
        assertEquals(Optional.empty(), issueSrv.getSubTaskByStartingName("sous tach resume", projectTestName));
    }

    @Test
    public void getSubTaskByNameTestFoundTwoSubTasks() throws Exception {
        BasicIssue bi = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(bi);
        //Create sub task
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", "normal", "1d", componentName, null);
            BasicIssue subTask2 = issueSrv.createSubTask(projectTestName, bi.getKey(), "Sous-tâche", "sous tach resume", "sous tach desc", "normal", "1d", componentName, null);
            assertNotNull(subTask);
            assertNotNull(subTask2);
            assertEquals(Optional.empty(), issueSrv.getSubTaskByStartingName("sous tach resume", projectTestName));
        } finally {
            issueSrv.removeIssue(bi.getKey(), true);
        }
    }

    @Test
    public void linkTwoSubTasks() throws Exception {
        BasicIssue basicIssue = issueSrv.createStory(projectTestName, null, null, "clientRef", "resume", "description", "normal", componentName, null, null);
        assertNotNull(basicIssue);
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, basicIssue.getKey(), "Sous-tâche", "sous tache resume", "sous tache description", "normal", "1d", componentName, null);
            BasicIssue subTask2 = issueSrv.createSubTask(projectTestName, basicIssue.getKey(), "Sous-tâche", "hello world", "sous tache description", "normal", "1d", componentName, "sous tache resume");
            assertNotNull(subTask);
            assertNotNull(subTask2);
            assertEquals(issueSrv.getByKey(subTask2.getKey(), projectTestName).getIssueLinks().iterator().next().getTargetIssueKey(), subTask.getKey());
        } finally {
            issueSrv.removeIssue(basicIssue.getKey(), true);
        }
    }

    @Test
    public void linkTwoStoriesTestOK() throws Exception {
        BasicIssue basicIssue = issueSrv.createStory(projectTestName, null, null, "clientRef", "clientRef | resume", "description", "normal", componentName, null, null);
        BasicIssue basicIssue2 = issueSrv.createStory(projectTestName, null, null, "clientRef2", "clientRef2 | resume", "description", "normal", componentName, null, "clientRef | resume");
        try {
            assertNotNull(basicIssue);
            assertNotNull(basicIssue2);
            assertEquals(issueSrv.getByKey(basicIssue2.getKey(), projectTestName).getIssueLinks().iterator().next().getTargetIssueKey(), basicIssue.getKey());
        } finally {
            issueSrv.removeIssue(basicIssue.getKey(), true);
            issueSrv.removeIssue(basicIssue2.getKey(), true);
        }
    }

    @Test
    public void linkTwoStoriesTestNotFound() throws Exception {
        BasicIssue basicIssue = issueSrv.createStory(projectTestName, null, null, "clientRef", "clientRef | resume", "description", "normal", componentName, null, null);
        //Story with hello world as resume doesn't exist
        BasicIssue basicIssue2 = issueSrv.createStory(projectTestName, null, null, "clientRef2", "clientRef2 | resume", "description", "normal", componentName, null, "hello world");
        try {
            assertNotNull(basicIssue);
            assertNotNull(basicIssue2);
            // Issue not found for this -> []
            assertEquals(issueSrv.getByKey(basicIssue2.getKey(), projectTestName).getIssueLinks(), new ArrayList<>());
        } finally {
            issueSrv.removeIssue(basicIssue.getKey(), true);
            issueSrv.removeIssue(basicIssue2.getKey(), true);
        }
    }

    @Test
    public void linkTwoStoriesTestTooStoriesFound() throws Exception {
        BasicIssue basicIssue = issueSrv.createStory(projectTestName, null, null, "clientRef", "clientRef | resume", "description", "normal", componentName, null, null);
        BasicIssue basicIssue3 = issueSrv.createStory(projectTestName, null, null, "clientRef", "clientRef | resume", "description", "normal", componentName, null, null);
        BasicIssue basicIssue2 = issueSrv.createStory(projectTestName, null, null, "clientRef2", "clientRef2 | resume", "description", "normal", componentName, null, "clientRef | resume");
        try {
            assertNotNull(basicIssue);
            assertNotNull(basicIssue2);
            assertNotNull(basicIssue3);
            assertEquals(issueSrv.getByKey(basicIssue2.getKey(), projectTestName).getIssueLinks(), new ArrayList<IssueLink>());
        } finally {
            issueSrv.removeIssue(basicIssue.getKey(), true);
            issueSrv.removeIssue(basicIssue2.getKey(), true);
            issueSrv.removeIssue(basicIssue3.getKey(), true);
        }
    }

    @Test
    public void linkStoryAndSubtaskOk() throws Exception {
        BasicIssue basicIssue = issueSrv.createStory(projectTestName, null, null, "clientRef", "clientRef | resume", "description", "normal", componentName, null, null);
        try {
            BasicIssue subTask = issueSrv.createSubTask(projectTestName, basicIssue.getKey(), "Sous-tâche", "sous tache resume", "sous tache description", "normal", "1d", componentName, "clientRef | resume");
            assertNotNull(basicIssue);
            assertNotNull(subTask);
            assertEquals(issueSrv.getByKey(subTask.getKey(), projectTestName).getIssueLinks().iterator().next().getTargetIssueKey(), basicIssue.getKey());
        } finally {
            issueSrv.removeIssue(basicIssue.getKey(), true);
        }
    }

    @Test
    public void linkSubTaskAndStoryOk() throws Exception {
        BasicIssue basicIssue = issueSrv.createStory(projectTestName, null, null, "clientRef", "clientRef | resume", "description", "normal", componentName, null, null);
        BasicIssue subTask = issueSrv.createSubTask(projectTestName, basicIssue.getKey(), "Sous-tâche", "Bien le bonjour", "sous tache description", "normal", "1d", componentName, null);
        BasicIssue basicIssue2 = issueSrv.createStory(projectTestName, null, null, "Hello", "Hello | World", "description", "normal", componentName, null, "Bien le bonjour");
        try {
            assertNotNull(basicIssue);
            assertNotNull(subTask);
            assertNotNull(basicIssue2);
            assertEquals(issueSrv.getByKey(basicIssue2.getKey(), projectTestName).getIssueLinks().iterator().next().getTargetIssueKey(), subTask.getKey());
        } finally {
            issueSrv.removeIssue(basicIssue.getKey(), true);
            issueSrv.removeIssue(basicIssue2.getKey(), true);
        }
    }

    @Test
    public void testEnCarton() throws Exception {
        List<Issue> issueList = new ArrayList<>();
        String jqlSearch = "project=\"" + projectTestName + "\"";
        SearchResult searchResult = jiraConnection.getSearchClient().searchJql(jqlSearch, new NullProgressMonitor());
        System.out.println(jqlSearch);
        Date today = Date.from(ZonedDateTime.now().toInstant());
        System.out.println(today);
        for (BasicIssue basicIssue : searchResult.getIssues()) {
            Issue issue = issueSrv.getByKey(basicIssue.getKey(), projectTestName);
            Iterable<Worklog> worklogList = issue.getWorklogs();
            worklogList.forEach(worklog -> {
                if (worklog.getUpdateDate().isBeforeNow()) {
                    if (!issueList.contains(issue)) {
                        issueList.add(issue);
                    }
                }
            });
        }
        issueList.forEach(issue -> issue.getWorklogs().forEach(worklog -> {
            System.out.println(worklog.getUpdateAuthor());
            System.out.println(worklog.getMinutesSpent());
            System.out.println(issue.getTimeTracking().getRemainingEstimateMinutes());
            System.out.println(worklog.getComment());

        }));
    }
}

