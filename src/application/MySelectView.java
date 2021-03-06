package application;

import java.io.File;
import java.util.List;

import it.Daniele.gestore.calendario.controller.Controller;
import it.Daniele.gestore.calendario.controller.MyController;
import it.Daniele.gestore.settings.model.AppSettings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class MySelectView extends BorderPane implements EventHandler<ActionEvent> {
	private static String WELCOME_MESSAGE = "Welcome dear";
	
	private TextField selectedFile;
	private Button browseButton;
	private Stage stage;
	private List<File> sourceFiles;
	private Button submit;
	
	private AppSettings appSettings;
	
	public MySelectView(Stage stage) {
		super();
		
		this.appSettings = new AppSettings();
		this.stage = stage;
		this.sourceFiles = appSettings.getPrefFiles();
		
		this.setTop(this.getWelcomeMessage());
		this.setCenter(this.getInput());
	}

	private Node getInput() {
		VBox result = new VBox();
		
		result.getChildren().add(this.getSelection());
		result.getChildren().add(this.getSubmitArea());
		
		result.setAlignment(Pos.CENTER);
		
		return result;
	}

	private Node getSubmitArea() {
		HBox result = new HBox();
		
		ImageView tikImg = new ImageView(new Image("file:img/tik.png"));
		tikImg.setFitHeight(24);
		tikImg.setFitWidth(24);
		
		this.submit = new Button("Avvia", tikImg);
		this.submit.setOnAction(this);
		
		result.getChildren().add(this.submit);
		result.setPadding(new Insets(10,0,0,0));
		result.setAlignment(Pos.CENTER);
		
		return result;
	}

	private Node getWelcomeMessage() {
		HBox result = new HBox();
		
		Text msg = new Text(MySelectView.WELCOME_MESSAGE);
		msg.setTextAlignment(TextAlignment.CENTER);
		msg.setFont(Font.font("Times New Roman", 28));
		msg.setFill(Paint.valueOf("RED"));
		
		result.getChildren().add(msg);
		result.setAlignment(Pos.CENTER);
		result.setPadding(new Insets(150,0,0,0));
		
		return result;
	}

	private Node getSelection() {
		HBox result = new HBox();
		
		this.selectedFile = new TextField(this.sourceFiles.toString().replace("[", "").replace("]", ""));
		this.selectedFile.setPrefColumnCount(25);
		this.selectedFile.setEditable(false);
		
		this.browseButton = new Button("Browse");
		this.browseButton.setOnAction(this::browseHandler);
		
		result.getChildren().add(this.selectedFile);
		result.getChildren().add(this.browseButton);
		result.setAlignment(Pos.CENTER);
		
		return result;
	}

	@Override
	public void handle(ActionEvent arg0) {
		it.Daniele.gestore.remote.RemoteControl.main(null);
		
		if(this.sourceFiles == null) Controller.myAlert(AlertType.WARNING, "Can't continue without a file!", ButtonType.CLOSE);
		else {
			this.setRootView();
		}
	}
	
	public void browseHandler(ActionEvent arg0) {
		FileChooser inFile = new FileChooser();
		inFile.setTitle("Select file");
		inFile.setSelectedExtensionFilter(new ExtensionFilter(".txt", ".dat"));
		
		if(inFile.showOpenMultipleDialog(this.stage) != null) this.sourceFiles.addAll(inFile.showOpenMultipleDialog(this.stage));
		
		if(this.sourceFiles != null) this.selectedFile.setText(this.sourceFiles.toString().replace("[", "").replace("]", ""));
	}
	
	public List<File> getSourceFiles(){
		return this.sourceFiles;
	}
	
	void setRootView() {
		try {
			Controller controller = new MyController(this.sourceFiles);
			
			Pane root = new MyRootView(this.stage,controller);			
			Scene scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			this.stage.setScene(scene);
			this.stage.show();
		}
		catch(IllegalArgumentException e) {
			Controller.myAlert(AlertType.ERROR, "Errore:\nuno o pi� file selezionati non validi", ButtonType.CLOSE);
		}
	}
}
