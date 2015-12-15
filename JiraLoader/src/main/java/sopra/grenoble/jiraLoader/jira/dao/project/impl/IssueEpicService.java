package sopra.grenoble.jiraLoader.jira.dao.project.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;

import sopra.grenoble.jiraLoader.exceptions.IssueNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.JiraIssueTypeException;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraFieldLoader;
import sopra.grenoble.jiraLoader.jira.dao.metadatas.JiraIssuesTypeLoader;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueEpicService;

@Service
public class IssueEpicService extends IssueAbstractGenericService implements IIssueEpicService {

	public static final Logger LOG = LoggerFactory.getLogger(IssueEpicService.class);
	
	@Autowired
	private JiraFieldLoader fieldLoader;

	@Override
	public Issue getByKey(String key, String projectName) throws IssueNotFoundException, JiraIssueTypeException {
		Issue epicIssue = super.getByKey(key, projectName);
		// check that this issue is a epic
		if (epicIssue.getIssueType().getName().compareTo(JiraIssuesTypeLoader.JIRA_EPIC_ISSUE_TYPE_NAME) != 0) {
			throw new JiraIssueTypeException();
		}
		return epicIssue;
	}
	
	@Override
	public BasicIssue getByName(String epicName, String projectName) {
		String jpqlFormat = String.format("issuetype = " + JiraIssuesTypeLoader.JIRA_EPIC_ISSUE_TYPE_NAME + " AND summary ~ '%s'", epicName);
		SearchResult sr = jiraConnection.getSearchClient().searchJql(jpqlFormat, pm);
		for (BasicIssue epicIssue : sr.getIssues()) {
			if (isExist(epicIssue.getKey(), projectName)) {
				return epicIssue;
			}
		}
		return null;
	}

	@Override
	public boolean isExist(String key, String projectName) {
		Issue issue;
		try {
			issue = getByKey(key, projectName);
			if (issue != null) {
				return true;
			}
		} catch (IssueNotFoundException | JiraIssueTypeException e) {
		}
		return false;
	}

	@Override
	public BasicIssue createEpic(String projectName, String epicName, String componentName) throws JiraGeneralException {
		//call generic builder
		IssueInputBuilder iib = createGenericIssue(projectName, JiraIssuesTypeLoader.JIRA_EPIC_ISSUE_TYPE_NAME, epicName, null, null, componentName);
		
		//get field id by name from loader
		Field fieldEpic = fieldLoader.getElement(JiraFieldLoader.EPIC_NAME_FIELD_NAME);
		//add Epic field : Epic Name
		FieldInput fi = new FieldInput(fieldEpic.getId(), epicName);
		iib.setFieldInput(fi);
		
		//insert in JIRA
		BasicIssue bi = super.createIssueInJIRA(iib.build());
		return bi;
	}

}
