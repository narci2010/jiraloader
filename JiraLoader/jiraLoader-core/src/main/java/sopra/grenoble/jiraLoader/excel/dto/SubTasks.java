package sopra.grenoble.jiraLoader.excel.dto;

/**
 * @author cmouilleron
 *
 */
public class SubTasks extends GenericModel {
	
	@Override
	public boolean validate() {
		if (versionName == null) return false;
		if (resume == null) return false;
		if (composantName == null) return false;
		return true;
	}
}
