package sopra.grenoble.jiraLoader.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.domain.BasicIssue;

import sopra.grenoble.jiraLoader.excel.dto.Epic;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueEpicService;

@Service("wrapper_Epic")
public class EpicWrapper extends AbstractWrapper<Epic> {

	private static final Logger LOG = LoggerFactory.getLogger(EpicWrapper.class);
	
	@Autowired
	private IIssueEpicService epicSrv;
	
	
	/**
	 * Default constructor
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public EpicWrapper() throws InstantiationException, IllegalAccessException {
		super();
	}


	@Override
	public void insertInJira(Epic e) throws JiraGeneralException {
		//test if exists
		BasicIssue epicIssue = epicSrv.getByName(e.resume, jiraUserDatas.getProjectName());
		if (epicIssue == null) {
			epicIssue = epicSrv.createEpic(jiraUserDatas.getProjectName(), e.resume, e.composantName);
			LOG.info("Creating new Epic in JIRA with name : " + e.resume + " with KEY <" + epicIssue.getKey() + ">");
		} else {
			LOG.info("Epic already exist in JIRA with KEY <" + epicIssue.getKey() + ">");
		}
		
		//update the DTO key
		e.key = String.valueOf(epicIssue.getKey());
	}


	@Override
	public void updateInJira(Epic e) {
		LOG.info("Epic update action is not allowed - Update function is not implemented... Maybe in next release");
	}

}
