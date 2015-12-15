package sopra.grenoble.jiraLoader.jira.parsers;

import com.atlassian.jira.rest.client.MetadataRestClient;
import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.Field;

/**
 * @author cmouilleron
 *
 */
public interface IMetadataRestClientV2 extends MetadataRestClient {

	/**
	 * Retrieves from the server complete list of available issue type
	 * @param progressMonitor progress monitor
	 * @return complete information about issue type resource
	 * @throws RestClientException in case of problems (connectivity, malformed messages, etc.)
	 * @since client 1.0, server 5.0
	 */
	Iterable<Field> getFields(ProgressMonitor progressMonitor);
}
