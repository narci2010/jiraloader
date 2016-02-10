package sopra.grenoble.jiraLoader.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.domain.BasicIssue;

import sopra.grenoble.jiraLoader.excel.dto.SubTasks;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;

@Service("wrapper_Sous-tâche")
public class SubTasksWrapper extends AbstractWrapper<SubTasks> {

	private static final Logger LOG = LoggerFactory.getLogger(SubTasksWrapper.class);
	

	@Autowired
	private IIssueService subTSrv;
	
	/**
	 * Default constructor
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public SubTasksWrapper() throws InstantiationException, IllegalAccessException {
		super();
	}


	@Override
	public void insertInJira(SubTasks s) throws JiraGeneralException {
		BasicIssue bi = subTSrv.createSubTask(jiraUserDatas.getProjectName(), jiraUserDatas.getLastStoryKey(), s.typeDemande, s.resume, s.descriptif, s.priority, s.estimation, s.composantName);
		LOG.info("Subtask has been created with KEY : " + bi.getKey());

		//update the DTO key
		s.key = String.valueOf(bi.getKey());
	}
	
	@Override
	public void updateInJira(SubTasks s) {
		LOG.info("SubTasks update action is not allowed - Update function is not implemented... Maybe in next release");
	}

}
