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
	public void insertInJira() throws JiraGeneralException {
		//test if exists
		BasicIssue epicIssue = epicSrv.getByName(dtoExcelModel.resume, confBean.getProjectName());
		if (epicIssue == null) {
			epicIssue = epicSrv.createEpic(confBean.getProjectName(), dtoExcelModel.resume, dtoExcelModel.composantName);
			LOG.info("Creating new Epic in JIRA with name : " + dtoExcelModel.resume + " with KEY <" + epicIssue.getKey() + ">");
		} else {
			LOG.info("Epic already exist in JIRA with KEY <" + epicIssue.getKey() + ">");
		}
		
		//update the DTO key
		this.dtoExcelModel.key = String.valueOf(epicIssue.getKey());
	}

}
