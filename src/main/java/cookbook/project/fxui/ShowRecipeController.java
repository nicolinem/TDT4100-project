package cookbook.project.fxui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import cookbook.project.Ingredient;
import cookbook.project.Recipe;

public class ShowRecipeController implements Initializable {

	// Dette er kontrolleren til siden for å se detaljene i en oppskrift. Her har
	// man mulighet for å endre antall porsjoner og sette/fjerne oppskrifter som
	// favoritter.
	// Når en oppskrift fra femhevede oppskrifter favoritiseres skrives den til
	// "mine oppskrifter" Og den fjernes derifra når "favoritisert" fjernes.

	private Recipe selectedRecipe;
	private int ingPortion;
	private ObservableList<Ingredient> ingredient = FXCollections.observableArrayList();
	private int tabIndex;
	private FileReader filesupport = new FileReader();

	@FXML
	private Label oppskriftsTittel;
	@FXML
	private Label portions;
	@FXML
	private ListView<Ingredient> ingredientListView;
	@FXML
	private Text infoText;

	@FXML
	private VBox stepsVBox;

	@FXML
	private Button favoriteButton;

	public void initData(Recipe recipe, int tabIndex, int recipeIndex) {
		this.tabIndex = tabIndex;
		selectedRecipe = recipe;
		this.ingPortion = recipe.getPortions();
		if (selectedRecipe.getName() != null) {
			oppskriftsTittel.setText(selectedRecipe.getName());
		} else {
			oppskriftsTittel.setText("oppskrift");
		}
		if (selectedRecipe.getInfo() != null) {
			infoText.setText(recipe.getInfo());
		}
		portions.setText(String.valueOf(recipe.getPortions()));
		for (Ingredient d : recipe.getIngredients()) {
			ingredient.add(d);
		}
		if (recipe.getSteps() != null) {
			for (String ste : recipe.getSteps()) {
				stepsVBox.getChildren().add(new Text(ste));
				Separator separator = new Separator();
				stepsVBox.getChildren().add(separator);
			}
		}
		if (selectedRecipe.isFavorite()) {
			setButtonStyleTrue();
		}

	}

	public void favoriteButtonPushed(ActionEvent event) {
		if (!selectedRecipe.isFavorite()) {
			selectedRecipe.setFavorite();
			if (!selectedRecipe.isOriginalRecipe()) {
				try {
					filesupport.writeRecipeToFile("myRecipes.txt", selectedRecipe);
					filesupport.replaceRecipeInFile("featuredrecipes.txt", selectedRecipe);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				try {
					filesupport.replaceRecipeInFile("myRecipes.txt", selectedRecipe);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			setButtonStyleTrue();
		} else {
			setButtonStyleFalse();
			selectedRecipe.removeFavorite();
			if (!selectedRecipe.isOriginalRecipe()) {
				try {
					filesupport.removeRecipeFromFile("myRecipes.txt", selectedRecipe);
					filesupport.replaceRecipeInFile("featuredrecipes.txt", selectedRecipe);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				try {
					filesupport.replaceRecipeInFile("myRecipes.txt", selectedRecipe);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void increasePortion(ActionEvent event) {
		if (ingPortion > 0) {
			changePortionSize(ingPortion + 1);
		}
	}

	public void decreasePortion(ActionEvent event) {
		if (ingPortion != 1 && ingPortion != 0) {
			changePortionSize(ingPortion - 1);
		}
	}

	public void setButtonStyleFalse() {
		favoriteButton.setStyle("-fx-padding: 8 8 8 8;" + "-f-fx-background-color: WHITE;" + "-fx-font-weight: bold;"
				+ "-fx-font-size: 1.1em;" + "-fx-background-radius: 19px;" + "-fx-border-color: LIGHTSALMON;"
				+ "-fx-border-radius: 19px;" + "-fx-border-width: 3 3 3 3;");
	}

	public void setButtonStyleTrue() {
		favoriteButton.setStyle("-fx-background-color: LIGHTSALMON;" + "-fx-padding: 8 8 8 8;"
				+ "-fx-font-weight: bold;" + "-fx-font-size: 1.1em;" + "-fx-background-radius: 19px;");
	}

	private void changePortionSize(int portion) {
		selectedRecipe.changePortionSize(portion);
		ingredient.setAll(selectedRecipe.getIngredients());
		this.ingPortion = portion;
		portions.setText(String.valueOf(ingPortion));
	}

	public void changeScreenBackToListView(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("Recipe.fxml"));
		Parent listViewParent = fxmlLoader.load();
		Scene firstPageScene = new Scene(listViewParent);

		RecipeController controller = fxmlLoader.getController();
		controller.setSelectedTab(tabIndex);

		Stage firstPage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		firstPage.setScene(firstPageScene);
		firstPage.show();

	}

	public class FormatIngredientListCell extends ListCell<Ingredient> {
		private Text ingredientName;

		FormatIngredientListCell() {
			ingredientName = new Text();

		}

		@Override
		public void updateItem(Ingredient ingredient, boolean empty) {
			super.updateItem(ingredient, empty);
			if (ingredient != null && !empty) {
				ingredientName.setText(ingredient.toString());
				setStyle("-fx-background-color: bisque;" + "-fx-background-insets: 0;");
				setGraphic(ingredientName);
			} else {
				setStyle("-fx-background-color: bisque;");
			}
		}
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		ingredientListView.setCellFactory(new Callback<ListView<Ingredient>, ListCell<Ingredient>>() {
			@Override
			public ListCell<Ingredient> call(ListView<Ingredient> ingredientListView) {
				return new FormatIngredientListCell();
			}
		});
		ingredientListView.setItems(ingredient);

	}

}
