package sopra.grenoble.jiraLoader.jira.dao.project.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;

import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.VersionNotFoundException;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraFieldLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueEpicService;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;
import sopra.grenoble.jiraLoader.jira.dao.project.IVersionService;

@Service
public class IssueStoryAndSubTaskService extends IssueAbstractGenericService implements IIssueService {

	public static final Logger LOG = LoggerFactory.getLogger(IssueStoryAndSubTaskService.class);

	@Autowired private IIssueEpicService epicSrv;
	@Autowired private IVersionService versionSrv;
	@Autowired private JiraFieldLoader fieldLoader;
	
	@Override
	public BasicIssue createStory(String projectName, String epicName, String versionName,  String resume, String description, String priority, String componentName) throws JiraGeneralException {
		//call generic builder
		IssueInputBuilder iib = createGenericIssue(projectName,  JiraIssuesTypeLoader.JIRA_STORY_ISSUE_TYPE_NAME, resume, description, priority, componentName);
		
		//add epicLink
		if (epicName != null) {
			//get epic based on the name
			BasicIssue epicI = epicSrv.getByName(epicName, projectName);
			addEpicLink(iib, epicI.getKey());
		}
		
		//add version
		if (versionName != null) {
			addFixVersion(iib, projectName, versionName);
		}
				
		//insert in JIRA
		BasicIssue bi = super.createIssueInJIRA(iib.build());
		return bi;
	}

	@Override
	public BasicIssue createSubTask(String projectName, String parentKey, String issueTypeName, String resume, String description, String priority, String estimation, String componentName) throws JiraGeneralException {
		//get the parentIssue
		Issue parentIssue = getByKey(parentKey, projectName);
		
		//call generic builder
		IssueInputBuilder iib = createGenericIssue(projectName, issueTypeName, resume, description, priority, componentName);

		// on ajoute un parent
		if (parentKey == null) {
			LOG.error("Parent key cannot be null");
			throw new IssueNotFoundException();
		}
		HashMap<String, Object> parentHashMap = new HashMap<>();
		parentHashMap.put("key", parentIssue.getKey());
		iib.setFieldValue("parent", new ComplexIssueInputFieldValue(parentHashMap));
		
		
		//add estimation
		if (estimation != null) {
			addEstimation(iib, estimation);
		}
		//add in the same epic
		//Not allowed in our screen JIRA. Subtask is automatically in the same epic as the parent
		
		//add same versions as the parent
		List<Version> parentVersions = (List<Version>) parentIssue.getAffectedVersions();
		if (parentVersions != null && parentVersions.size() != 0) {
			iib.setAffectedVersions(parentVersions);
		}
		
		//insert in JIRA
		BasicIssue bi = super.createIssueInJIRA(iib.build());

		return bi;
	}
	
	
	/**
	 * Add a reference to an epic in issue.
	 * @param iib
	 * @param epicName
	 * @throws JiraGeneralException 
	 * @throws IssueNotFoundException 
	 */
	private void addEpicLink(IssueInputBuilder iib, String epicKey) throws JiraGeneralException {
		//get field id by name from loader
		Field fieldEpic = fieldLoader.getElement(JiraFieldLoader.EPIC_LINK_FIELD_NAME);
		//add Epic field : Epic Name
		FieldInput fi = new FieldInput(fieldEpic.getId(), epicKey);
		iib.setFieldInput(fi);
	}
	
	/**
	 * Add estimation
	 * @param iib
	 * @param estimation
	 */
	private void addEstimation(IssueInputBuilder iib, String estimation) {
		HashMap<String, Object> estimationHashMap = new HashMap<>();
		estimationHashMap.put("originalEstimate", estimation);
		iib.setFieldValue("timetracking", new ComplexIssueInputFieldValue(estimationHashMap));
	}
	
	
	/**
	 * Set a specific version in iib
	 * @param iib
	 * @param projectName
	 * @param versionName
	 * @throws VersionNotFoundException
	 */
	private void addFixVersion(IssueInputBuilder iib, String projectName, String versionName) throws VersionNotFoundException {
		//get version from name
		Version v = versionSrv.getVersion(projectName, versionName);
		List<Version> versions = new ArrayList<>();
		versions.add(v);
		iib.setAffectedVersions(versions);
	}
	
}
