package sopra.grenoble.jiraLoader.jira.parsers;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.atlassian.jira.rest.client.ProgressMonitor;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.internal.jersey.JerseyMetadataRestClient;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.sun.jersey.client.apache.ApacheHttpClient;

/**
 * @author cmouilleron
 *
 */
public class MetadataRestV2Client extends JerseyMetadataRestClient implements IMetadataRestClientV2 {

	private final FieldJsonParser fieldJsonParser = new FieldJsonParser();
	private final GenericJsonArrayParser<Field> fieldsJsonParser = GenericJsonArrayParser.create(fieldJsonParser);

	/**
	 * Default constructor
	 * @param baseUri
	 * @param client
	 */
	public MetadataRestV2Client(URI baseUri, ApacheHttpClient client) {
		super(baseUri, client);
	}

	@Override
	public Iterable<Field> getFields(ProgressMonitor progressMonitor) {
		final URI uri = UriBuilder.fromUri(baseUri).path("field").build();
		return getAndParse(uri, fieldsJsonParser, progressMonitor);
	}

}
