package sopra.grenoble.jiraLoader.jira.dao.project.impl;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.json.gen.IssueInputJsonGenerator;

import sopra.grenoble.jiraLoader.exceptions.ComponentNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraIssueTypeException;
import sopra.grenoble.jiraLoader.exceptions.JiraPriorityNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.ProjectNotFoundException;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraPriorityLoader;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueGenericService;
import sopra.grenoble.jiraLoader.jira.dao.project.IProjectService;

public abstract class IssueAbstractGenericService implements IIssueGenericService {

	public static final Logger LOG = LoggerFactory.getLogger(IssueAbstractGenericService.class);

	@Autowired
	protected JiraRestClient jiraConnection;

	@Autowired
	protected ProgressMonitor pm;

	@Autowired
	private IProjectService projectSrv;

	@Autowired
	private JiraIssuesTypeLoader issueTypeLoaderSrv;
	
	@Autowired
	private JiraPriorityLoader priorityLoader;

	@Override
	public Issue getByKey(String key, String projectName) throws IssueNotFoundException, JiraIssueTypeException {
		LOG.info("Looking for issue with key = " + key);
		Issue issue = null;
		try {
			issue = jiraConnection.getIssueClient().getIssue(key, pm);
			if (issue.getProject().getName().compareTo(projectName) == 0) {
				return issue;
			}
		} catch (RuntimeException e) {
			LOG.warn("Epic with key <" + key + "> has not been found", e);
			throw new IssueNotFoundException();
		}
		throw new IssueNotFoundException();
	}
	
	@Override
	public void removeIssue(String key, boolean deleteSubTasks) throws IssueNotFoundException {
		LOG.info("Deleting jira issue with key : " + key);
		jiraConnection.getIssueClient().removeIssue(key, deleteSubTasks, pm);
	}

	@Override
	public boolean isExist(String key, String projectName) {
		try {
			Issue issue = getByKey(key, projectName);
			if (issue != null) {
				return true;
			}
		} catch (IssueNotFoundException | JiraIssueTypeException e) {
			return false;
		}
		return false;
	}

	/**
	 * Create a generic issue builder
	 * @param projectName
	 * @param issueTypeName
	 * @param resume
	 * @param description
	 * @param componentName
	 * @return
	 * @throws ProjectNotFoundException
	 * @throws JiraIssueTypeException
	 * @throws ComponentNotFoundException
	 * @throws JiraPriorityNotFoundException 
	 */
	protected IssueInputBuilder createGenericIssue(String projectName, String issueTypeName, String resume, String description, String priorityName, String componentName) throws ProjectNotFoundException, JiraIssueTypeException, ComponentNotFoundException, JiraPriorityNotFoundException {
		// get the project
		Project jiraProject = projectSrv.getProjectByName(projectName);
		
		// get the issueType description
		IssueType issueType = issueTypeLoaderSrv.getElement(issueTypeName);
		if (issueType == null) 
		{
			LOG.error("Issue type with name <" + issueTypeName + "> does not exist");
			throw new JiraIssueTypeException();
		}
		
		IssueInputBuilder issueInB = new IssueInputBuilder(jiraProject.getKey(), issueType.getId());
		//set resume => mandatory
		issueInB.setSummary(resume);
		//set description (optional)
		issueInB.setDescription(description);
		//set priority (optional)
		if (priorityName != null) {
			Priority priority = priorityLoader.getElement(priorityName);
			if (priority == null) {
				LOG.error("Priority with name <" + priority + "> does not exist");
				throw new JiraPriorityNotFoundException();
			}
			issueInB.setPriority(priority);			
		}
			
		//set component 
		//get component from Project
		if (componentName != null) {
			BasicComponent basicComponent = projectSrv.getComponentByNameFromProject(jiraProject, componentName);
			issueInB.setComponents(basicComponent);
		}
		
		return issueInB;		
	}
	
	protected void logIssueInJSON(IssueInput issue) {
		try {
			IssueInputJsonGenerator jGenerator = new IssueInputJsonGenerator();
			JSONObject jsonObj = jGenerator.generate(issue);
			LOG.debug("Issue =>" + jsonObj.toString());
		} catch (Exception e) {
			LOG.warn("Unable to convert issue in JSON", e);
		}
	}
	
	/**
	 * Create the issue passed in parameter in JIRA
	 * @param issueInput
	 * @return
	 */
	protected BasicIssue createIssueInJIRA(IssueInput issueInput) {
		//call JIRA
		this.logIssueInJSON(issueInput);
		BasicIssue issueCreated = jiraConnection.getIssueClient().createIssue(issueInput, pm);
		LOG.info("New issue has been created with key : " + issueCreated.getKey());
		return issueCreated;
	}
	
	/**
	 * Generic function to execute a JQL request on JIRA
	 * @param jpqlRequest
	 * @return
	 */
	protected boolean isExistGen(String jpqlRequest) {
		LOG.debug("Running JPQL command : " + jpqlRequest);
		try {
			SearchResult sr = jiraConnection.getSearchClient().searchJql(jpqlRequest, pm);
			if (sr.getTotal() != 0) {
				return true;
			}
		} catch (RuntimeException e) {
			return false;
		}
		return false;
	}
}
