package sopra.grenoble.jiraLoader.excel.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cmouilleron
 *
 */
public class Epic extends GenericModel {

	private static final Logger LOG = LoggerFactory.getLogger(Epic.class);
	
	@Override
	public boolean validate() {
		if (resume == null) return false;
		if (composantName == null) return false;
		if (clientReference != null) {
			LOG.error("The field Client Reference in excel file should not be set");
			return false;
		}
		if (epicName != null) {
			LOG.error("The field Epic in excel file should not be set");
			return false;
		}
		
		if (resume.contains("-")) {
			LOG.error("The special char '-' is not allowed in Epic Name");
			return false;
		}
		return true;
	}

}
