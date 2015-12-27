package sopra.grenoble.jiraLoaderfx;
	
import java.io.PrintStream;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * @author cmouilleron
 * Main JAVAFX class for the application. This class initializes stages and configure {@link System} outputStream in the
 * main textarea zone
 *
 */
public class JiraLoaderFx extends Application {
	
	/**
	 * The main page.
	 */
	private Stage primaryStage;

	/**
	 * Default constructor. Do nothing special
	 */
	public JiraLoaderFx() {
		super();
	}

	/**
	 * Use this function to launch the JAVAFX application
	 */
	public static void launchJavaFx() {
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Jira Loader");
		
		//init layout
		initRootLayout();
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
			
			//redirect system output in text area
			redirectSystemOutputOnTextArea(jiraController.getTextArea());
			
			//show the main page
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Redirect the {@link System} output stream in the main textarea page.
	 * @param textArea : the textarea where output stream should be redirect
	 */
	private void redirectSystemOutputOnTextArea(TextArea textArea) {
		//initialize the outputStream in the textarea
		TextAreaOutputStream outTextAreaOuputStream = new TextAreaOutputStream(textArea, 50);
		PrintStream ps = new PrintStream(outTextAreaOuputStream, true);
		System.setOut(ps);
		System.setErr(ps);			
		//start the flush thread
		outTextAreaOuputStream.startFlushThread();

		//register an eventClose
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				// onclose, stop all custom threads
				outTextAreaOuputStream.stopFlushThread();
			}
		});
	}
	

	/**
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * @param primaryStage
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}


}
