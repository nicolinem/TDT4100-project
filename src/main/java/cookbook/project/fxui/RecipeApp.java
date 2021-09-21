package cookbook.project.fxui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RecipeApp extends Application{
	
	public void start(Stage primaryStage) throws IOException{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Recipe.fxml"));
		Parent root = fxmlLoader.load();
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
		
	}
	
	public static void main(String[] args) {
		launch(RecipeApp.class, args);
	}
}


