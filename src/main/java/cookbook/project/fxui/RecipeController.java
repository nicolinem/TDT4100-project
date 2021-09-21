package cookbook.project.fxui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import cookbook.project.Cookbook;
import cookbook.project.FavoritePredicate;
import cookbook.project.MyRecipePredicate;
import cookbook.project.Recipe;

public class RecipeController implements Initializable {

	// Dette er kontrolleren til applikajosnens hovedside. Den består at to lister
	// med hver sin kokebok. Den første sider er for "fremhevede oppskrfter"
	// (featured).
	// Tanken bak siden er at applikasjonen kommer med ferdige oppskrifter til
	// brukeren som kan se på oppskriftene og endre antall posjoner, som justerer
	// ingrediensvolumene.
	// Den andre siden er "mine oppskrifter" som er der brukerens egne oppskrifter
	// legges til. Her kommer også oppskriftene man har favoritisert fra hovedisden.
	// Til listene har jeg laget en custom CellFactory, slik at hver celle
	// representerer en oppskrift og viser frem elementer fra oppskriften slik som
	// tittel, infotekst og om
	// den er en favoritt (bildene er bare tilfeldig lagt til for det visuelle)
	// på siden for "mine oppskrifter" kan man sortere om amn vil se alle,
	// originale, eller favoritiserte oppskrifter.
	// fra her kan man også gå til siden for å opprette en ny oppskrift eller
	// redigere en valgt oppskrift

	private Cookbook featured = new Cookbook();
	private Cookbook myRecipesbook = new Cookbook("MyRecipes");
	private ObservableList<Recipe> names = FXCollections.observableArrayList();
	private ObservableList<Recipe> myRecipes = FXCollections.observableArrayList();
	private final IFileReader fileSupport = new FileReader();
	Button selectedButton;

	@FXML
	private ListView<Recipe> listView;
	@FXML
	private ListView<Recipe> myRecipesListView;
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab tabFrontPage;
	@FXML
	private Tab tabMyPage;
	@FXML
	private Button chooseRecipe;
	@FXML
	private Button newRecipe;
	@FXML
	private Button detailedRecipe;
	@FXML
	private Button allRecipesButton;
	@FXML
	private Button myRecipesButton;
	@FXML
	private Button favoriteRecipesButton;

	public void changeSceneToDetailedRecipeView(ActionEvent event) throws IOException {
		if ((!listView.getSelectionModel().isEmpty() || !myRecipesListView.getSelectionModel().isEmpty())) {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getClassLoader().getResource("RecipeshowRecipe.fxml"));
			Parent showRecipeParent = loader.load();

			Scene showRecipeScene = new Scene(showRecipeParent);

			ShowRecipeController controller = loader.getController();
			if (tabFrontPage.isSelected()) { // så man vet hvilke liste man får informasjon fra basert på fanen
				controller.initData(listView.getSelectionModel().getSelectedItem(),
						tabPane.getSelectionModel().getSelectedIndex(),
						listView.getSelectionModel().getSelectedIndex());
			} else {
				controller.initData(myRecipesListView.getSelectionModel().getSelectedItem(),
						tabPane.getSelectionModel().getSelectedIndex(),
						myRecipesListView.getSelectionModel().getSelectedIndex());
			}

			Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			window.setScene(showRecipeScene);
			window.show();
		}
	}

	public void setSelectedTab(int tabIndex) {
		tabPane.getSelectionModel().select(tabIndex);
	}

	public void changeSceneToNewRecipeView(ActionEvent event) throws IOException {
		changeToRecipeEditor(event, null, -1, tabPane.getSelectionModel().getSelectedIndex(), false);
	}

	public void changeSceneToEditRecipe(ActionEvent event) throws IOException {
		if (!myRecipesListView.getSelectionModel().isEmpty()) {
			changeToRecipeEditor(event, myRecipesListView.getSelectionModel().getSelectedItem(),
					myRecipesListView.getSelectionModel().getSelectedIndex(),
					tabPane.getSelectionModel().getSelectedIndex(), true);
		}
	}

	private void changeToRecipeEditor(ActionEvent event, Recipe recipe, int recipeIndex, int tabIndex, boolean editing)
			throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("NewRecipe.fxml"));
		Parent newRecipeParent = loader.load();

		Scene newRecipeScene = new Scene(newRecipeParent);
		NewRecipeController controller = loader.getController();
		controller.initData(recipe, recipeIndex, tabIndex, editing);

		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		window.setScene(newRecipeScene);
		window.show();
	}

	public class FormatListCell extends ListCell<Recipe> {
		private HBox content;
		private Pane favorite;
		private Text name;
		private Text info;
		private ImageView img;
		private Text tag;

		public FormatListCell() {
			super();
			name = new Text();
			info = new Text();
			tag = new Text("");
			favorite = new Pane(tag);
			img = new ImageView();
			Pane pane1 = new Pane(name);
			Pane pane2 = new Pane(info);
//			VBox tags = new VBox(favorite);

			VBox vBox = new VBox(pane1, pane2);

			img.setFitHeight(240);
			img.setFitWidth(260);

			content = new HBox(img, favorite, vBox);
			content.setPadding(new Insets(10, 10, 10, -10));
			content.setSpacing(15);

		}

		@Override
		protected void updateItem(Recipe item, boolean empty) {
			super.updateItem(item, empty);

			if (item != null && !empty) { // <== test for null item and empty parameter

//	            	Overskriften til hver celle er navnet på oppskriften:
				name.setText(item.getName());
				name.setY(45);
				name.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 20));
				name.setFill(Color.PERU);

//	                Dette er informasjonen lagret i oppskriften, litt om hva det er
				if (item.getInfo() != null) {
					info.setText(item.getInfo());
					info.setY(50);
					info.setFill(Color.SANDYBROWN);
				} else {
					info.setText("");
				}

				if (item.isFavorite()) {
					String l = "\u2605";
					tag.setText(l);
					tag.setY(45);
					tag.setFill(Color.PERU);
				} else {
					tag.setText("");
				}

				setPrefHeight(260);
				if (this.isSelected()) {
					this.setStyle(
							"-fx-background-color: white;" + "-fx-border-color: white;" + "-fx-border-width: 10;");
				} else {
					this.setStyle(
							"-fx-background-color: bisque;" + "-fx-border-color: white;" + "-fx-border-width: 10;");
				}

				if (item.getImage() != null) {
					try {
						Image im = new Image(item.getImage());
						img.setImage(im);
					} catch (Exception e) {

					}

				}

				setGraphic(content);
			} else {
				this.setStyle("-fx-background-color: white;" + "-fx-border-color: white;" + "-fx-border-width: 10;");
				setGraphic(null);
			}
		}
	}

	public void favoriteSort(ActionEvent event) {
		sortRecipeList(new FavoritePredicate());
		setButtonStyleTrue(favoriteRecipesButton);
		setButtonStyleFalse(allRecipesButton);
		setButtonStyleFalse(myRecipesButton);

	}

	public void myRecipesSort(ActionEvent event) {
		sortRecipeList(new MyRecipePredicate());
		setButtonStyleTrue(myRecipesButton);
		setButtonStyleFalse(allRecipesButton);
		setButtonStyleFalse(favoriteRecipesButton);
	}

	public void allRecipesSort(ActionEvent event) {
		setMy();
		setButtonStyleTrue(allRecipesButton);
		setButtonStyleFalse(myRecipesButton);
		setButtonStyleFalse(favoriteRecipesButton);
	}

	public void setButtonStyleFalse(Button button) {
		button.setStyle("-fx-padding: 8 8 8 8;" + "-fx-background-color: WHITE;" + "-fx-font-weight: bold;"
				+ "-fx-font-size: 1.1em;" + "-fx-background-radius: 19px;" + "-fx-border-color: LIGHTSALMON;"
				+ "-fx-border-radius: 19px;" + "-fx-border-width: 3 3 3 3;");
	}

	public void setButtonStyleTrue(Button button) {
		button.setStyle("-fx-background-color: LIGHTSALMON;" + "-fx-padding: 8 8 8 8;" + "-fx-font-weight: bold;"
				+ "-fx-font-size: 1.1em;" + "-fx-background-radius: 19px;" + "-fx-border-color: LIGHTSALMON;"
				+ "-fx-border-radius: 19px;" + "-fx-border-width: 3 3 3 3;");
	}

	public void sortRecipeList(Predicate<Recipe> predicate) {
		myRecipes.clear();
		for (Recipe recipe : myRecipesbook) {
			if (predicate.test(recipe)) {
				myRecipes.add(recipe);
			}
		}
	}

	public void addNewRecipeToList(Recipe recipe) {
		myRecipesbook.addRecipe(recipe);
		setMy();
	}

	private void setFeatured() {
		names.setAll(featured.getBook());
	}

	private void setMy() {
		myRecipes.setAll(myRecipesbook.getBook());
	}

	public void initialize(URL url, ResourceBundle rb) {
		try {
			fileSupport.getRecipeFromFile("featuredrecipes.txt", featured);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			fileSupport.getRecipeFromFile("myRecipes.txt", myRecipesbook);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		setFeatured();
		setMy();

		listView.setCellFactory(new Callback<ListView<Recipe>, ListCell<Recipe>>() {
			@Override
			public ListCell<Recipe> call(ListView<Recipe> listView) {
				return new FormatListCell();
			}
		});
		myRecipesListView.setCellFactory(new Callback<ListView<Recipe>, ListCell<Recipe>>() {
			@Override
			public ListCell<Recipe> call(ListView<Recipe> myRecipesListView) {
				return new FormatListCell();
			}
		});
		listView.setItems(names);
		myRecipesListView.setItems(myRecipes);
		setButtonStyleTrue(allRecipesButton);
		setButtonStyleFalse(myRecipesButton);
		setButtonStyleFalse(favoriteRecipesButton);
	}

}
