package sopra.grenoble.jiraLoader.wrappers;

import com.atlassian.jira.rest.client.domain.BasicIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sopra.grenoble.jiraLoader.excel.dto.Story;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.jira.dao.project.IIssueService;

import java.util.Optional;

@Service("wrapper_Story")
public class StoryWrapper extends AbstractWrapper<Story> {

	private static final Logger LOG = LoggerFactory.getLogger(StoryWrapper.class);
	
	@Autowired
	private IIssueService storySrv;
	
	/**
	 * Default constructor
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public StoryWrapper() throws InstantiationException, IllegalAccessException {
		super();
	}


	@Override
	public void insertInJira(Story s) throws JiraGeneralException {
		Optional<BasicIssue> bi = Optional.empty();
		//if option checkStoryExist is activated, check if the story is existing in JIRA
		if (excelConfigurationDatas.isSearchStoryByNameBeforeCreate()) {
			LOG.info(getLogPrefixe() + "isSearchStoryByNameBeforeCreate is activated. Looking for issue with name : " + s.resume);
			bi = getStoryIfExist(s.resume, jiraUserDatas.getProjectName());
		}
		
		if (bi.isPresent()) {
			LOG.info(getLogPrefixe() + "Story has been retrieved with the key : " + bi.get().getKey());
			s.key = String.valueOf(bi.get().getKey());
			updateRowInJira();
		} else {
			bi = Optional.of(storySrv.createStory(jiraUserDatas.getProjectName(), s.epicName, s.versionName, s.clientReference, s.resume, s.descriptif, s.priority, s.composantName));
			LOG.info(getLogPrefixe() + "Story has been created with KEY : " + bi.get().getKey());
		}

		//update the DTO key
		s.key = String.valueOf(bi.get().getKey());
	}

	@Override
	public void updateInJira(Story s) throws JiraGeneralException {
		if (excelConfigurationDatas.isAllowingUpdate()) {
			storySrv.updateIssue(s.key, jiraUserDatas.getProjectName(), s.priority);
			LOG.info(getLogPrefixe() + "Story with KEY : " + s.key + " has been updated");
		} else {
			LOG.info(getLogPrefixe() + "Story with KEY : " + s.key + "has not been updated.(Settings: Allow Story update false)");
		}
	}


	private Optional<BasicIssue> getStoryIfExist(String fullStoryName, String projectName) {
		String storyName = fullStoryName.split("\\|")[0].trim();
		return storySrv.getByStartingName(storyName, projectName);
	}
	
}
