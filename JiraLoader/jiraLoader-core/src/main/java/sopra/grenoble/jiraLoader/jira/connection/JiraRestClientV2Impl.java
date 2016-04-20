package sopra.grenoble.jiraLoader.jira.connection;

import com.atlassian.jira.rest.client.*;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.jersey.*;
import com.sun.jersey.api.client.AsyncViewResource;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ViewResource;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sopra.grenoble.jiraLoader.configurationbeans.JiraUserDatas;
import sopra.grenoble.jiraLoader.jira.parsers.IMetadataRestClientV2;
import sopra.grenoble.jiraLoader.jira.parsers.MetadataRestV2Client;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author cmouilleron
 *
 */
@Component
public class JiraRestClientV2Impl implements IJiraRestClientV2 {

	/*
	 * Static internal logger
	 */
	private static final Logger LOG = LoggerFactory.getLogger(JiraRestClientV2Impl.class);

	/*
	 * Parameter for connection
	 */
	private URI baseUri;
	private URI serverUri;
	private ApacheHttpClient client;
	
	
	/*
	 * rest clients
	 */
	private IssueRestClient issueRestClient;
	private SessionRestClient sessionRestClient;
	private UserRestClient userRestClient;
	private ProjectRestClient projectRestClient;
	private ComponentRestClient componentRestClient;
	private IMetadataRestClientV2 metadataRestV2Client;
	private MetadataRestClient metadataRestClient;
	private SearchRestClient searchRestClient;
	private VersionRestClient versionRestClient;
	private ProjectRolesRestClient projectRolesRestClient;
	
	
	@Autowired
	private JiraUserDatas configuration;
	
	
	@Override
	public void openConnection() throws URISyntaxException {
		LOG.info("Opening connection on URI : " + configuration.getUri());
		LOG.info("Using user : " + configuration.getUsername());
		this.serverUri = new URI(configuration.getUri());
		this.baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build();
		
		AuthenticationHandler authentificationHandler =  new BasicHttpAuthenticationHandler(configuration.getUsername(), configuration.getPassword());
		DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
		authentificationHandler.configure(config);
		client = createDefaultClient(config, authentificationHandler);
		
		//init clients
		initClients();
	}
	
	
	/**
	 * Initialize all rest clients.
	 */
	private void initClients() {
		metadataRestV2Client = new MetadataRestV2Client(baseUri, client);
		metadataRestClient = new JerseyMetadataRestClient(baseUri, client);
		sessionRestClient = new JerseySessionRestClient(client, serverUri);
		issueRestClient = new JerseyIssueRestClient(baseUri, client, sessionRestClient, metadataRestClient);
		userRestClient = new JerseyUserRestClient(baseUri, client);
		projectRestClient = new JerseyProjectRestClient(baseUri, client);
		componentRestClient = new JerseyComponentRestClient(baseUri, client);
		searchRestClient = new JerseySearchRestClient(baseUri, client);
		versionRestClient = new JerseyVersionRestClient(baseUri, client);
		projectRolesRestClient = new JerseyProjectRolesRestClient(baseUri, client, serverUri);
	}

	@Override
	public IssueRestClient getIssueClient() {
		return issueRestClient;
	}

	@Override
	public SessionRestClient getSessionClient() {
		return sessionRestClient;
	}

	@Override
	public UserRestClient getUserClient() {
		return userRestClient;
	}

	@Override
	public ProjectRestClient getProjectClient() {
		return projectRestClient;
	}

	@Override
	public ComponentRestClient getComponentClient() {
		return componentRestClient;
	}

	@Override
	public MetadataRestClient getMetadataClient() {
		return metadataRestClient;
	}

	@Override
	public SearchRestClient getSearchClient() {
		return searchRestClient;
	}

	@Override
	public VersionRestClient getVersionRestClient() {
		return versionRestClient;
	}

	@Override
	public ProjectRolesRestClient getProjectRolesRestClient() {
		return projectRolesRestClient;
	}

	@Override
	public ApacheHttpClient getTransportClient() {
		return client;
	}

	public static ApacheHttpClientHandler createDefaultClientHander(DefaultApacheHttpClientConfig config) {
		final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
		return new ApacheHttpClientHandler(client, config);
	}

	public static ApacheHttpClient createDefaultClient(DefaultApacheHttpClientConfig config,
			final AuthenticationHandler authenticationHandler) {
		return new ApacheHttpClient(createDefaultClientHander(config)) {
			@Override
			public WebResource resource(URI u) {
				final WebResource resource = super.resource(u);
				authenticationHandler.configure(resource, this);
				return resource;
			}

			@Override
			public AsyncWebResource asyncResource(URI u) {
				final AsyncWebResource resource = super.asyncResource(u);
				authenticationHandler.configure(resource, this);
				return resource;
			}

			@Override
			public ViewResource viewResource(URI u) {
				final ViewResource resource = super.viewResource(u);
				authenticationHandler.configure(resource, this);
				return resource;
			}

			@Override
			public AsyncViewResource asyncViewResource(URI u) {
				final AsyncViewResource resource = super.asyncViewResource(u);
				authenticationHandler.configure(resource, this);
				return resource;
			}
		};
	}

	@Override
	public IMetadataRestClientV2 getMetadataClientV2() {
		return metadataRestV2Client;
	}

}
