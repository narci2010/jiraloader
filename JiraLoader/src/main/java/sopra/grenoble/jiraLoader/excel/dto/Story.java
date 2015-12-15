package sopra.grenoble.jiraLoader.excel.dto;

/**
 * @author cmouilleron
 *
 */
public class Story extends GenericModel {

	@Override
	public boolean validate() {
		if (epicName == null) return false;
		if (versionName == null) return false;
		if (resume == null) return false;
		if (descriptif == null) return false;
		if (priority == null) return false;
		if (composantName == null) return false;
		if (estimation != null) return false;
		return true;
	}
}
