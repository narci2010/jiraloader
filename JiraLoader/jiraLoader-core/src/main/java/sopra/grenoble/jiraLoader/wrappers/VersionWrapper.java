package sopra.grenoble.jiraLoader.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopra.grenoble.jiraLoader.excel.dto.Version;
import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.VersionNotFoundException;
import sopra.grenoble.jiraLoader.jira.dao.project.IVersionService;

@Service("wrapper_Version")
public class VersionWrapper extends AbstractWrapper<Version> {

	private static final Logger LOG = LoggerFactory.getLogger(VersionWrapper.class);
	
	@Autowired
	private IVersionService vSrv;

	/**
	 * Default constructor
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public VersionWrapper() throws InstantiationException, IllegalAccessException {
		super();
	}


	@Override
	public void insertInJira(Version iv) throws JiraGeneralException {
		//check if version exists
		com.atlassian.jira.rest.client.domain.Version v = null;
		try {
			v = vSrv.getVersion(jiraUserDatas.getProjectName(), iv.versionName);
			LOG.info(getLogPrefixe() + "Version " + iv.versionName + " already exist with ID <" + v.getId() + ">");
		} catch (VersionNotFoundException e) {
		}
		
		//only create the version if no reference has been found previously
		if (v == null) {
			//create the version
			v = vSrv.createVersion(iv.versionName, iv.resume, jiraUserDatas.getProjectName(), null);
			LOG.info(getLogPrefixe() + "Creating new Version in JIRA with name : " + iv.versionName + " with ID <" + v.getId() + ">");
		}
		
		//updated DTO line
		iv.key = String.valueOf(v.getId());
	}
	
	@Override
	public void updateInJira(Version v) {
		LOG.info(getLogPrefixe() + "Version update action is not allowed - Update function is not implemented... Maybe in next release");
	}

}
