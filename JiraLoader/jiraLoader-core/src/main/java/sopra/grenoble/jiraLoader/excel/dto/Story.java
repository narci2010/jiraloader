package sopra.grenoble.jiraLoader.excel.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cmouilleron
 */
public class Story extends GenericModel {

    private static final Logger LOG = LoggerFactory.getLogger(Story.class);

    @Override
    public boolean validate() {
        if (resume == null) {
            LOG.error("Story validation : resume cannot be empty");
            return false;
        }
        if (composantName == null) {
            LOG.error("Story validation : composantName cannot be empty");
            return false;
        }
        return true;
    }
}
