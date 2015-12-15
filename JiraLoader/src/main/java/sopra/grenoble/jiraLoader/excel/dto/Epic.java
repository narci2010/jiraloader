package sopra.grenoble.jiraLoader.excel.dto;

/**
 * @author cmouilleron
 *
 */
public class Epic extends GenericModel {

	@Override
	public boolean validate() {
		if (resume == null) return false;
		if (composantName == null) return false;
		return true;
	}

}
