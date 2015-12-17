package sopra.grenoble.jiraLoader.jira.dao.project;

import java.util.List;

import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Project;

import sopra.grenoble.jiraLoader.exceptions.ComponentNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.ProjectNotFoundException;

public interface IProjectService {

	/**
	 * Return true if the specify project identified by the name exist
	 * @param projectName : the project name
	 * @return {@link Boolean}
	 */
	public boolean isProjectExist(String projectName);
	
	/**
	 * Return the {@link BasicProject} identified by the project key
	 * @param key : the project key.
	 * @throws ProjectNotFoundException
	 */
	public Project getProjectByKey(String key) throws ProjectNotFoundException;
	
	/**
	 * Return the {@link BasicProject} identified by the project name
	 * @param projectName : the project name
	 * @return {@link BasicProject}
	 * @throws ProjectNotFoundException
	 */
	public Project getProjectByName(String projectName) throws ProjectNotFoundException;
	
	/**
	 * Return only the project key based on the project name
	 * @param projectName : the project name
	 * @return the project key
	 * @throws ProjectNotFoundException
	 */
	public String getProjectKey(String projectName) throws ProjectNotFoundException;
	

	/**
	 * Return a component of a specify project identified by its name.
	 * @param project
	 * @param componentName
	 * @return
	 * @throws ComponentNotFoundException 
	 */
	public BasicComponent getComponentByNameFromProject(Project project, String componentName) throws ComponentNotFoundException;

	/**
	 * Return true if the componentName exist in the project.
	 * @param projectName
	 * @param componentName
	 * @return
	 * @throws ProjectNotFoundException
	 */
	boolean isComponentNameExistsInProject(String projectName, String componentName) throws ProjectNotFoundException;
	
	
	/**
	 * @return all projects for a client
	 */
	public List<BasicProject> getAllProject();
}
