package cookbook.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cookbook.project.fxui.FileReader;
import cookbook.project.fxui.IFileReader;

public class Cookbook implements Iterable<Recipe> {

	// En "kokebok" er en samling oppskrifter med metoder for å legge til, bytte ut
	// og fjerne oppskrifter
	// Den implementerer Iterable for å kunne iterere gjennom oppskriftene

	private String name;
	private List<Recipe> recipes = new ArrayList<>();

	public Cookbook(String name) {
		if (!name.matches("^[ÆØÅæøåa-zA-Z0-9\\s]+$")) {
			throw new IllegalArgumentException("Tittel kan bare bestå av bokstaver og tall");
		}
		this.name = name;
	}

	public Cookbook() {

	}

	public void setName(String name) {
		if (!name.matches("^[ÆØÅæøåa-zA-Z\\s]+$")) {
			throw new IllegalArgumentException("Tittel kan bare bestå av bokstaver og tall");
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Recipe getRecipe(int num) {
		return recipes.get(num);
	}

	public List<Recipe> getBook() {
		return recipes;
	}

	public void addRecipe(Recipe recipe) {
		this.recipes.add(recipe);
	}

	public void removeRecipe(int index) {
		if (index <= recipes.size()) {
			recipes.remove(index);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public boolean isInCookbook(String recipeName) {
		for (Recipe r : recipes) {
			if (r.getName().equals(recipeName)) {
				return true;
			}
		}
		return false;

	}

	public int getRecipeIndex(String recipeName) {
		for (Recipe r : recipes) {
			if (r.getName().equals(recipeName)) {
				int index = recipes.indexOf(r);
				return index;
			}
		}
		return -1;

	}

	public void replaceRecipe(int RecipeIndex, Recipe newRecipe) {
		recipes.remove(RecipeIndex);
		recipes.add(RecipeIndex, newRecipe);
	}

	public static void main(String[] args) throws IOException {
		IFileReader fileSupport = new FileReader();
		Cookbook cookbook = new Cookbook();
		fileSupport.writeRecipesToFile("featuredrecipes.txt", cookbook);
	}

	@Override
	public Iterator<Recipe> iterator() {
		return recipes.iterator();
	}

}
