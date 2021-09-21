package project.fxui;

import java.io.FileNotFoundException;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import project.Cookbook;
import project.Ingredient;
import project.Recipe;

public class NewRecipeController implements Initializable {

	// Dette er kontrolleren for å lage nye oppskrifter, det er den samme
	// kontrolleren som brukes for å redigere oppskrifter.
	// Når man lager en ny oppskrift skrives det til filen med original oppskrift.
	// Redigerer man en oppskrift så lages det en ny oppdatert oppskrift som
	// erstatter den gamle i kokeboken,
	// og derettes skrives til filen.

	private final IFileReader fileSupport = new FileReader();
	private ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
	private ObservableList<String> steps = FXCollections.observableArrayList();
	private Recipe recipe;
	private int recipeIndex;
	private int tabIndex;
	private boolean editing;

	@FXML
	TextField newIngredientName;
	@FXML
	TextField newRecipeTitle;
	@FXML
	TextField newIngredientVol;
	@FXML
	TextField portionSize;
	@FXML
	TextArea recipeInfo;
	@FXML
	private ComboBox<String> newIngredientUnit;
	@FXML
	ListView<Ingredient> ingredientListView;
	@FXML
	ListView<String> stepsListView;
	@FXML
	Button newRecipeButton;
	@FXML
	Button backButton;
	@FXML
	Button addIngredientButton;
	@FXML
	Button removeIngredientButton;
	@FXML
	Button updateRecipeButton;
	@FXML
	Label errorMessageLabel;
	@FXML
	Button deleteRecipe;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		deleteRecipe.setVisible(false);
		ingredientListView.setItems(ingredients);
		errorMessageLabel.setText("");
		newIngredientUnit.getItems().addAll("g", "dl", "ss", "ts", "stk");
		newIngredientUnit.setEditable(true);
		for (int i = 0; i < 8; i++) {
			steps.add("");
		}
		stepsListView.setFixedCellSize(50);
		stepsListView.setItems(steps);
		stepsListView.setCellFactory(TextFieldListCell.forListView());
	}

	public void newIngredientButtonPushed(ActionEvent event) throws IOException {
		for (Ingredient i : ingredients) {
			if (i.getName().equals(newIngredientName.getText())) {
				errorMessageLabel.setText("Denne ingrediensen finnes allerede i oppskriften");
				return;
			}
		}
		try {
			if (newIngredientVol.getText() != null && !newIngredientVol.getText().equals("")) {
				Ingredient newIngredient = new Ingredient(newIngredientName.getText(),
						(Double.parseDouble(newIngredientVol.getText())),
						(newIngredientUnit.getSelectionModel().getSelectedItem()));
				ingredients.add(newIngredient);
			} else {
				Ingredient newIngredient = new Ingredient(newIngredientName.getText());
				ingredients.add(newIngredient);
			}
			newIngredientVol.setText(null);
			newIngredientName.setText(null);
			newIngredientUnit.getSelectionModel().clearSelection();

			errorMessageLabel.setText("");
		} catch (NumberFormatException e) {
			errorMessageLabel.setText("Ugyldig input: Ingrediensvolum må være et tall");
		} catch (IllegalArgumentException e) {
			errorMessageLabel.setText("Ugyldig input: En ingrediens kan bare bestå av bokstaver");
		} catch (NullPointerException e) {
			errorMessageLabel.setText("må ha en tittel på ingrediensen");
		}

	}

	public void initData(Recipe recipe, int recipeIndex, int tabIndex, boolean editing) {

		this.tabIndex = tabIndex;
		this.editing = editing;
		if (recipe != null && recipeIndex != -1) {
			this.recipe = recipe;

			this.recipeIndex = recipeIndex;
			newRecipeTitle.setText(recipe.getName());
			portionSize.setText(String.valueOf(recipe.getPortions()));
			ingredients.addAll(recipe.getIngredients());
			newRecipeButton.setVisible(false);
			updateRecipeButton.setVisible(true);
			deleteRecipe.setVisible(true);
			updateRecipeButton.toFront();
		}
	}

	public void deleteButtonPushed() {
		int selectedIndex = ingredientListView.getSelectionModel().getSelectedIndex();
		ingredients.remove(selectedIndex);
	}

	public void createNewRecipeButtonPushed(ActionEvent event) throws IOException {
		try {
			Recipe newRecipe = createRecipe();
			fileSupport.writeRecipeToFile("myRecipes.txt", newRecipe);
			backButton.fire();

		} catch (Exception e) {
			errorMessageLabel.setText(e.getLocalizedMessage());
		}
	}

	public void updateRecipeButtonPushed(ActionEvent event) {
		try {
			Recipe newRecipe = createRecipe();
			try {
				fileSupport.replaceRecipeInFile("myRecipes.txt", newRecipe, recipeIndex);
			} catch (IOException e) {
				errorMessageLabel.setText(e.getMessage());
			}
			recipe.removeFavorite();
			recipe.removeOriginalRecipe();
			try {
				fileSupport.replaceRecipeInFile("featuredrecipes.txt", recipe);
			} catch (IOException e) {
				errorMessageLabel.setText(e.getMessage());
			}
			backButton.fire();

		} catch (IllegalArgumentException e) {
			errorMessageLabel.setText(e.getMessage());
		}

	}

	public Recipe createRecipe() {
		Cookbook c = new Cookbook();
		Cookbook d = new Cookbook();
		try {
			fileSupport.getRecipeFromFile("myRecipes.txt", c);
			fileSupport.getRecipeFromFile("featuredrecipes.txt", d);
		} catch (FileNotFoundException e1) {
			errorMessageLabel.setText(e1.getMessage());
		}
		;
		if (portionSize.getText().isBlank()) {
			throw new IllegalArgumentException(
					"Du har felt som mangler! Sørg for at oppskriften har tittel og antall porsjoner");
		}
		if (!editing) {
			if (c.isInCookbook(newRecipeTitle.getText()) || d.isInCookbook(newRecipeTitle.getText())) {
				throw new IllegalArgumentException("Denne oppskriftstittelen finnes allerede");
			}
		}
		if (editing && !recipe.isOriginalRecipe() && recipe.getName().equals(newRecipeTitle.getText())) {
			throw new IllegalArgumentException("Oppdater oppskriftsnavnet:)");
		}
		try {
			Recipe newRecipe = new Recipe(newRecipeTitle.getText(), Integer.parseInt(portionSize.getText()));
			if (ingredients.size() == 0) {
				throw new IllegalStateException("Du mangler ingredienser:/");
			} else {
				for (Ingredient ingredient : ingredients) {
					newRecipe.addIngredient(ingredient);
				}
			}
			if (!recipeInfo.getText().isBlank()) {
				newRecipe.setInfo(recipeInfo.getText());
			}
			for (String s : steps) {
				if (!s.isBlank()) {
					newRecipe.addStep(s);
				}
			}
			if (editing) {
				newRecipe.setImage(recipe.getImage());
				newRecipe.setTags(recipe.getTags());
			}
			newRecipe.setOriginalRecipe();

			return newRecipe;
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Ingrediensvolum må være et tall");
		}

	}

	public void deleteRecipeButtonPushed(ActionEvent event) {
		try {
			fileSupport.removeRecipeFromFile("myRecipes.txt", recipeIndex);
		} catch (IOException e1) {
			errorMessageLabel.setText(e1.getMessage());
		}
		backButton.fire();
		if (editing && !recipe.isOriginalRecipe()) {
			recipe.removeFavorite();
			try {
				fileSupport.replaceRecipeInFile("featuredrecipes.txt", recipe);
			} catch (IOException e) {
				errorMessageLabel.setText(e.getMessage());
			}
		}
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

}
