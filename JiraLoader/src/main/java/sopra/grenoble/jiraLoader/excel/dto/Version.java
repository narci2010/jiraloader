package sopra.grenoble.jiraLoader.excel.dto;

/**
 * @author cmouilleron
 *
 */
public class Version extends GenericModel{

	@Override
	public boolean validate() {
		if (versionName == null) return false;
		return true;
	}
	
}
