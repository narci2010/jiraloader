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
	public void insertInJira() throws JiraGeneralException {
		//check if version exists
		com.atlassian.jira.rest.client.domain.Version v = null;
		try {
			v = vSrv.getVersion(confBean.getProjectName(), dtoExcelModel.versionName);
			LOG.info("Version " + dtoExcelModel.versionName + " already exist with ID <" + v.getId() + ">");
		} catch (VersionNotFoundException e) {
		}
		
		//only create the version if no reference has been found previously
		if (v == null) {
			//create the version
			v = vSrv.createVersion(dtoExcelModel.versionName, dtoExcelModel.resume, confBean.getProjectName(), null);
			LOG.info("Creating new Version in JIRA with name : " + dtoExcelModel.versionName + " with ID <" + v.getId() + ">");
		}
		
		//updated DTO line
		this.dtoExcelModel.key = String.valueOf(v.getId());
	}

}
