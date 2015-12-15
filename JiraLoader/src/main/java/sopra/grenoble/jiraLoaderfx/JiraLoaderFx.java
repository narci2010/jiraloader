package sopra.grenoble.jiraLoaderfx;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * @author cmouilleron
 *
 */
public class JiraLoaderFx extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;

	public JiraLoaderFx() {
		super();
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Jira Loader");
		
		//init layout
		initRootLayout();
	}
	
	public static void launchJavaFx() {
		launch();
	}
	
	/**
	 * Init root layout
	 */
	private void initRootLayout() {
		try {
			//load root layout from XML file
			FXMLLoader loader = new FXMLLoader();
			
			loader.setLocation(ClassLoader.getSystemResource("JiraViewer.fxml"));
			//loader.setLocation(getClass().getResource("JiraViewer.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			
			//give the main app to the controle
			JiraController jiraController = loader.getController();
			jiraController.setMainApp(this);
			
			//show the scene containing the root layout
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public BorderPane getRootLayout() {
		return rootLayout;
	}

	public void setRootLayout(BorderPane rootLayout) {
		this.rootLayout = rootLayout;
	}

}
