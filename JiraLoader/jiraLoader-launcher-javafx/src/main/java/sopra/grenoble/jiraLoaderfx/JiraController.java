package sopra.grenoble.jiraLoaderfx;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.domain.BasicProject;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import sopra.grenoble.jiraLoader.JiraLoader;
import sopra.grenoble.jiraLoader.JiraUserConfiguration;
import sopra.grenoble.jiraLoader.jira.connection.IJiraRestClientV2;
import sopra.grenoble.jiraLoader.jira.dao.project.IProjectService;
import sopra.grenoble.jiraLoader.spring.ApplicationContextProvider;

public class JiraController {

	private static final Logger LOG = LoggerFactory.getLogger(JiraController.class);

	@FXML
	private TextField tfLogin;
	@FXML
	private TextField tfPassword;
	@FXML
	private TextField tfExcelFilePath;
	@FXML
	private TextField tfJiraUri;

	@FXML
	private Button btInject;
	@FXML
	private Button btSelect;
	@FXML
	private Button btConnect;

	@FXML
	private ChoiceBox<String> cbProjectChooser;
	@FXML
	private TextArea textArea;

	private JiraLoaderFx mainApp;

	private JiraUserConfiguration configurationBean;

	/*
	 * Jira Loader service
	 */
	private IJiraRestClientV2 jiraConnection;
	private IProjectService projectSrv;
	private JiraLoader jiraLoader;

	/**
	 * Default constructor
	 */
	public JiraController() {
		super();

		// init bean from spring
		configurationBean = ApplicationContextProvider.getApplicationContext().getBean(JiraUserConfiguration.class);
		projectSrv = ApplicationContextProvider.getApplicationContext().getBean(IProjectService.class);
		jiraLoader = ApplicationContextProvider.getApplicationContext().getBean(JiraLoader.class);

	}

	public void appendText(String str) {
		Platform.runLater(() -> textArea.appendText(str));
	}

	@FXML
	private void initialize() {
		tfLogin.setText(configurationBean.getUsername());
		tfJiraUri.setText(configurationBean.getUri());

		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				appendText(String.valueOf((char) b));
			}
		};
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}

	@FXML
	private void injectExcelFile() {
		LOG.info("Starting excel injection in JIRA");

		// update bean with project name
		LOG.info("Change project bean name to : " + cbProjectChooser.getSelectionModel().getSelectedItem());
		configurationBean.setProjectName(cbProjectChooser.getSelectionModel().getSelectedItem());
		// run application

		Runnable task = () -> {
			try {
				jiraLoader.loadingFile(tfExcelFilePath.getText());
			} catch (IOException e) {
				LOG.error("Error while loading excel file in JIRA", e);
			}

		};

		// start the thread
		Thread thread = new Thread(task);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@FXML
	private void openSelectionFile() {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Please select your excel file");

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx");
		chooser.getExtensionFilters().add(extFilter);

		File file = chooser.showOpenDialog(null);

		if (file != null) {
			// update file label
			tfExcelFilePath.setText(file.getPath());
			// activate start injection
			btInject.setDisable(false);
		}
	}

	@FXML
	private void connectToJira() {
		// get a connexion and open it
		jiraConnection = ApplicationContextProvider.getApplicationContext().getBean(IJiraRestClientV2.class);

		// update configuration bean
		configurationBean.setUri(tfJiraUri.getText());
		configurationBean.setUsername(tfLogin.getText());
		configurationBean.setPassword(tfPassword.getText());

		// opening connection
		try {
			jiraConnection.openConnection();
		} catch (URISyntaxException e) {
			LOG.error("Unable to connect to JIRA ", e);
			return;
		}

		// get project for user
		try {
			List<BasicProject> projects = projectSrv.getAllProject();
			LOG.info("Connection with JIRA is successfully opened");

			if (projects != null && !projects.isEmpty()) {
				activateBtAfterConnection();
				ObservableList<String> items = cbProjectChooser.getItems();
				for (BasicProject project : projects) {
					items.add(project.getName());
				}
				cbProjectChooser.getSelectionModel().selectFirst();
			} else {
				LOG.error("No project has been found for this user");
				disableBtBeforeConnection();
			}
		} catch (RuntimeException e) {
			LOG.error("Unable to open connection. Error while opening connection to JIRA ", e);
			disableBtBeforeConnection();
		}
	}

	private void activateBtAfterConnection() {
		this.btInject.setDisable(false);
		this.cbProjectChooser.setDisable(false);
		this.btSelect.setDisable(false);
	}

	private void disableBtBeforeConnection() {
		this.btInject.setDisable(true);
		this.cbProjectChooser.setDisable(true);
		this.cbProjectChooser.getItems().clear();
		this.btSelect.setDisable(true);
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(JiraLoaderFx mainApp) {
		this.mainApp = mainApp;
	}

	public JiraLoaderFx getMainApp() {
		return mainApp;
	}

	public TextField getTfLogin() {
		return tfLogin;
	}

	public void setTfLogin(TextField tfLogin) {
		this.tfLogin = tfLogin;
	}

	public TextField getTfPassword() {
		return tfPassword;
	}

	public void setTfPassword(TextField tfPassword) {
		this.tfPassword = tfPassword;
	}

	public TextField getTfExcelFilePath() {
		return tfExcelFilePath;
	}

	public void setTfExcelFilePath(TextField tfExcelFilePath) {
		this.tfExcelFilePath = tfExcelFilePath;
	}

	public TextField getTfJiraUri() {
		return tfJiraUri;
	}

	public void setTfJiraUri(TextField tfJiraUri) {
		this.tfJiraUri = tfJiraUri;
	}

	public Button getBtInject() {
		return btInject;
	}

	public void setBtInject(Button btInject) {
		this.btInject = btInject;
	}

	public Button getBtSelect() {
		return btSelect;
	}

	public void setBtSelect(Button btSelect) {
		this.btSelect = btSelect;
	}

	public Button getBtConnect() {
		return btConnect;
	}

	public void setBtConnect(Button btConnect) {
		this.btConnect = btConnect;
	}

	// public ChoiceBoxListCell<String> getCbProjectChooser() {
	// return cbProjectChooser;
	// }
	//
	// public void setCbProjectChooser(ChoiceBoxListCell<String>
	// cbProjectChooser) {
	// this.cbProjectChooser = cbProjectChooser;
	// }

	public JiraUserConfiguration getConfigurationBean() {
		return configurationBean;
	}

	public void setConfigurationBean(JiraUserConfiguration configurationBean) {
		this.configurationBean = configurationBean;
	}

}
