package sopra.grenoble.jiraLoader.jira.dao.project;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.domain.Version;

import sopra.grenoble.jiraLoader.exceptions.ProjectNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.VersionNotFoundException;

public interface IVersionService {

	/**
	 * List all versions from a specific project
	 * @param projectName : the project name
	 * @return {@link Iterable}{@link Version}
	 * @throws ProjectNotFoundException 
	 */
	public Iterable<Version> listFromProject(String projectName) throws ProjectNotFoundException;

	/**
	 * Create a new version i a specific project
	 * @param versionName
	 * @param description
	 * @param projectName
	 * @param releaseDate
	 * @return 
	 * @throws ProjectNotFoundException 
	 */
	Version createVersion(String versionName, String description, String projectName, DateTime releaseDate) throws ProjectNotFoundException;
	
	Version getVersion(String projectName, String versionName) throws VersionNotFoundException;
	void removeVersion(String projectName, String versionName) throws VersionNotFoundException;
}
