package sopra.grenoble.jiraLoader.excel.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cmouilleron
 */
public class Version extends GenericModel {

    private static final Logger LOG = LoggerFactory.getLogger(SubTasks.class);

    @Override
    public boolean validate() {
        if (versionName == null) {
            LOG.error("Version validation : versionName cannot be empty");
            return false;
        }
        if (clientReference != null) {
            LOG.error("This field Reference Client in excel should not be set");
            return false;
        }
        return true;
    }

}
