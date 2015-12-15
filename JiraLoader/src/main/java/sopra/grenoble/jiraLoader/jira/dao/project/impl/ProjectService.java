package sopra.grenoble.jiraLoader.jira.dao.project.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.Project;

import sopra.grenoble.jiraLoader.exceptions.ComponentNotFoundException;
import sopra.grenoble.jiraLoader.exceptions.ProjectNotFoundException;
import sopra.grenoble.jiraLoader.jira.dao.project.IProjectService;

@Service
public class ProjectService implements IProjectService {

	public static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);
	
	@Autowired
	private JiraRestClient jiraConnection;
	
	@Autowired
	private ProgressMonitor pm;
	
	/**
	 * Map which contain the couple <projectName/BasicProject>. The goal is to store the 
	 * full project in order to quickly access to the project key.
	 */
	private Map<String, BasicProject> hashProjectKeyName = null;
	
	
	/**
	 * Load from JIRA all projects
	 */
	private synchronized void loadHashProjectKeyName() {
		if (hashProjectKeyName == null) {
			LOG.info("First call to Jira Project Service - Initialising internal map with project name");
			Iterable<BasicProject> lstProject = jiraConnection.getProjectClient().getAllProjects(pm);
			hashProjectKeyName = new HashMap<>();
			for (BasicProject basicProject : lstProject) {
				LOG.info("User can access to project : " + basicProject.getName());
				hashProjectKeyName.put(basicProject.getName(), basicProject);
			}
			
		}
	}
	
	public void resetProjectCache() {
		if (hashProjectKeyName != null) {
			hashProjectKeyName.clear();
			hashProjectKeyName = null;
		}
	}
	
	@Override
	public boolean isProjectExist(String projectName) {
		loadHashProjectKeyName();
		return hashProjectKeyName.containsKey(projectName);
	}

	@Override
	public Project getProjectByKey(String key) throws ProjectNotFoundException {
		loadHashProjectKeyName();
		for (BasicProject bp : hashProjectKeyName.values()) {
			if (bp.getKey().compareTo(key) == 0) {
				return jiraConnection.getProjectClient().getProject(bp.getKey(), pm);
			}
		}
		throw new ProjectNotFoundException();
	}

	@Override
	public Project getProjectByName(String projectName) throws ProjectNotFoundException {
		loadHashProjectKeyName();
		
		if (!isProjectExist(projectName)) throw new ProjectNotFoundException();
		
		BasicProject bp = hashProjectKeyName.get(projectName);
		return jiraConnection.getProjectClient().getProject(bp.getKey(), pm);
	}

	@Override
	public String getProjectKey(String projectName) throws ProjectNotFoundException {
		if (!isProjectExist(projectName)) throw new ProjectNotFoundException();
		BasicProject bp = hashProjectKeyName.get(projectName);
		return bp.getKey();
	}

	@Override
	public BasicComponent getComponentByNameFromProject(Project project, String componentName) throws ComponentNotFoundException {
		for (BasicComponent component : project.getComponents()) {
			if (component.getName().compareTo(componentName)==0) {
				return component;
			}
		}
		throw new ComponentNotFoundException();
	}
	
	@Override
	public boolean isComponentNameExistsInProject(String projectName, String componentName) throws ProjectNotFoundException {
		Project p = getProjectByName(projectName);
		try {
			getComponentByNameFromProject(p, componentName);
		} catch (ComponentNotFoundException e) {
			return false;
		}
		return true;
	}

	@Override
	public List<BasicProject> getAllProject() {
		List<BasicProject> projects = (List<BasicProject>) jiraConnection.getProjectClient().getAllProjects(pm);
		return projects;
	}
	

}
