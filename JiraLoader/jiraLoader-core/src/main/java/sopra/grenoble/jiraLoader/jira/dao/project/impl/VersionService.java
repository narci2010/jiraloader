package sopra.grenoble.jiraLoader.jira.dao.project.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.Project;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.domain.input.VersionInput;

import sopra.grenoble.jiraLoader.exceptions.JiraGeneralException;
import sopra.grenoble.jiraLoader.exceptions.ProjectNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.VersionNotFoundException;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.project.IProjectService;
import sopra.grenoble.jiraLoader.jira.dao.project.IVersionService;

@Service
public class VersionService implements IVersionService {

	public static final Logger LOG = LoggerFactory.getLogger(VersionService.class);

	@Autowired
	private IProjectService projectSrv;
	
	@Autowired
	private IJiraRestClientV2 jiraConnection;
	
	@Autowired
	private ProgressMonitor pm;

	@Override
	public Iterable<Version> listFromProject(String projectName) throws ProjectNotFoundException {
		Project bp = projectSrv.getProjectByName(projectName);
		return bp.getVersions();
	}
	
	@Override
	public Version createVersion(String versionName, String description, String projectName, DateTime releaseDate) throws ProjectNotFoundException {
		Project bp = projectSrv.getProjectByName(projectName);
		LOG.info("Creating version with name : " + versionName);
		VersionInput vi = new VersionInput(bp.getKey(), versionName, description, releaseDate, false, true);
		return jiraConnection.getVersionRestClient().createVersion(vi, pm);
	}

	@Override
	public Version getVersion(String projectName, String versionName) throws VersionNotFoundException {
		Version selectedVersion = null;
		
		try {
			List<Version> versions = (List<Version>) listFromProject(projectName);
			for (Version v : versions) {
				if (v.getName().compareTo(versionName) == 0) {
					selectedVersion = v;
					break;
				}
			}
		} catch (JiraGeneralException e) {
			LOG.error("Unable to find version for project name : " + projectName, e);
			throw new VersionNotFoundException();
		}
		
		if (selectedVersion == null) {
			throw new VersionNotFoundException();
		}
		
		return selectedVersion;
	}

	@Override
	public void removeVersion(String projectName, String versionName) throws VersionNotFoundException {
		Version v = getVersion(projectName, versionName);
		
		LOG.info("Deleting version with name : " + versionName);
		jiraConnection.getVersionRestClient().removeVersion(v.getSelf(), null, null, pm);
	}

}
