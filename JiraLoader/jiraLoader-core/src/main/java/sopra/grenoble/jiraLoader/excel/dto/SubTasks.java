package sopra.grenoble.jiraLoader.excel.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cmouilleron
 */
public class SubTasks extends GenericModel {

    private static final Logger LOG = LoggerFactory.getLogger(SubTasks.class);

    @Override
    public boolean validate() {
        if (resume == null) {
            LOG.error("SubTasks validation : resume cannot be empty");
            return false;
        }
        if (composantName == null) {
            LOG.error("SubTasks validation : composantName cannot be empty");
            return false;
        }
        if (clientReference != null) {
            LOG.error("SubTasks validation : The field Client Reference in excel should not be set");
            return false;
        }
        return true;
    }
}
