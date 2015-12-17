package sopra.grenoble.jiraLoader.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.domain.BasicIssue;

import sopra.grenoble.jiraLoader.excel.dto.Story;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.jira.dao.project.impl.IssueStoryAndSubTaskService;

@Service("wrapper_Story")
public class StoryWrapper extends AbstractWrapper<Story> {

	private static final Logger LOG = LoggerFactory.getLogger(StoryWrapper.class);
	
	@Autowired
	private IssueStoryAndSubTaskService storySrv;
	
	/**
	 * Default constructor
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public StoryWrapper() throws InstantiationException, IllegalAccessException {
		super();
	}


	@Override
	public void insertInJira() throws JiraGeneralException {
		BasicIssue bi = storySrv.createStory(confBean.getProjectName(), dtoExcelModel.epicName, dtoExcelModel.versionName, dtoExcelModel.resume, dtoExcelModel.descriptif, dtoExcelModel.priority, dtoExcelModel.composantName);
		LOG.info("Story has been created with KEY : " + bi.getKey());

		//update the DTO key
		this.dtoExcelModel.key = String.valueOf(bi.getKey());
	}

}
