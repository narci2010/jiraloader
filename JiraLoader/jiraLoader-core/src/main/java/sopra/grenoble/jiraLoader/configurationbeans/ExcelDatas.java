package sopra.grenoble.jiraLoader.configurationbeans;

import org.springframework.stereotype.Component;

@Component
public class ExcelDatas {
	
	/**
	 * The excel file version required for this version
	 */
	public static int JIRA_LOADER_REQUIRED_VERSION = 1; 
	
	/**
	 * boolean to indicate if the program must check while a story creation, if a story with the same name already exists.
	 * The ckeck is only based on the name before a | character.
	 * For instance, if a story with name "XXX | YYY" have to be created, the program will check if a story with a name which start with "XXX" already exist.
	 */
	private boolean searchStoryByNameBeforeCreate = false;

	private boolean allowUpdate = false;

	private boolean updateStoryAndSubTasks = false;

	
	
	public boolean isSearchStoryByNameBeforeCreate() {
		return searchStoryByNameBeforeCreate;
	}

	public boolean isAllowingUpdate() {
		return allowUpdate;
	}

	public boolean isUpdatingStoryAndSubTaks() {
		return updateStoryAndSubTasks;
	}

	public void setSearchStoryByNameBeforeCreate(boolean searchStoryByNameBeforeCreate) {
		this.searchStoryByNameBeforeCreate = searchStoryByNameBeforeCreate;
	}

	public void setAllowUpdate(boolean allowUpdate) {
		this.allowUpdate = allowUpdate;
	}

	public void setUpdateStoryAndSubTasks(boolean updateStoryAndSubTasks) {
		this.updateStoryAndSubTasks = updateStoryAndSubTasks;
	}
}
