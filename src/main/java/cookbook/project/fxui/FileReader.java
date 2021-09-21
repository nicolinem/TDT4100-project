package cookbook.project.fxui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import project.Cookbook;
import project.Ingredient;
import project.Recipe;

public class FileReader implements IFileReader {

	// Oppskriftene skrives til en fil der hver linje representerer èn oppskrift.
	// Man kan skrive inn en hell kokebok eller bare en oppskrift.
	// Det er også metoder for å bytte ut oppskrifter i en fil

	@Override
	public void writeRecipesToFile(String filename, Cookbook cookbook) throws IOException {
		int index = 0;
		try (FileWriter fileWriter = new FileWriter(filename)) {
			for (Recipe recipe : cookbook.getBook()) {
				String newLine = recipe.getName() + ";" + recipe.getPortions() + ";";
				for (Ingredient ingredient : recipe.getIngredients()) {
					newLine += ingredient.getName() + "&" + ingredient.getAmount() + "&" + ingredient.getUnit() + "%";
				}
				newLine += ";" + recipe.getImage();
				if (recipe.getInfo() != null) {
					newLine += ";" + recipe.getInfo();
				}

				if (recipe.getSteps().size() != 0) {
					newLine += "#";
					for (String steps : recipe.getSteps()) {
						if (!steps.equals("")) {
							newLine += steps + "@";
						}
					}
				}
				newLine += "#" + Arrays.toString(recipe.getTags());
				if (index < cookbook.getBook().size()) {
					newLine += "\n";
				}
				fileWriter.write(newLine);
				index++;
			}
			fileWriter.close();
		}
	}

	@Override
	public void writeRecipeToFile(String filename, Recipe recipe) throws IOException {
		try {
			FileWriter fileWriter = new FileWriter(filename, true);

			String recipeToFile = recipe.getName() + ";" + recipe.getPortions() + ";";
			for (Ingredient ingredient : recipe.getIngredients()) {
				recipeToFile += (ingredient.getName() + "&" + ingredient.getAmount() + "&" + ingredient.getUnit()
						+ "%");
			}
			recipeToFile += ";" + recipe.getImage();
			if (recipe.getInfo() != null) {
				recipeToFile += ";" + recipe.getInfo();
			}

			if (recipe.getSteps().size() != 0) {
				recipeToFile += "#";
				for (String steps : recipe.getSteps()) {
					if (!steps.equals("")) {
						recipeToFile += steps + "@";
					}
				}
			}
			recipeToFile += "#" + Arrays.toString(recipe.getTags());
			recipeToFile += "\n";

			fileWriter.write(recipeToFile);

			fileWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getRecipeFromFile(String filename, Cookbook cookbook) throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNextLine()) {
				String recipe = scanner.nextLine();
				String[] checkSteps = recipe.split("#");

				String[] recipeElements = checkSteps[0].split(";");
				String recipeTitle = recipeElements[0];
				String portions = recipeElements[1];
				int intPortions = Integer.parseInt(portions);
				Recipe newRecipe = new Recipe(recipeTitle, intPortions);
				newRecipe.setImage(recipeElements[3]);
				if (recipeElements.length > 4) {
					newRecipe.setInfo(recipeElements[4]);
				}
				String[] ingredients = recipeElements[2].split("%");
				for (String ing : ingredients) {
					String[] ingredientElements = ing.split("&");
					if (ingredientElements.length == 3) {
						Ingredient newIngredient = new Ingredient(ingredientElements[0],
								Double.parseDouble(ingredientElements[1]), ingredientElements[2]);
						newRecipe.addIngredient(newIngredient);
					} else if (ingredientElements.length == 1) {
						Ingredient newIngredient = new Ingredient(ingredientElements[0]);
						newRecipe.addIngredient(newIngredient);
					}

				}
				if (checkSteps.length > 2) {
					String[] steps = checkSteps[1].split("@");
					List<String> stepsAsList = Arrays.asList(steps);
					newRecipe.setSteps(stepsAsList);

					String[] tags = (checkSteps[2].split("[\\[\\]]")[1]).split(", ");
					int[] binaryTags = new int[5];
					for (int i = 0; i < 5; i++) {
						binaryTags[i] = Integer.parseInt(tags[i]);
					}
					newRecipe.setTags(binaryTags);
				} else {
					String[] tags = (checkSteps[1].split("[\\[\\]]")[1]).split(", ");
					int[] binaryTags = new int[5];
					for (int i = 0; i < 5; i++) {
						binaryTags[i] = Integer.parseInt(tags[i]);
					}
					newRecipe.setTags(binaryTags);
				}

				cookbook.addRecipe(newRecipe);

			}
//			scanner.close();
		}
	}

	@Override
	public void replaceRecipeInFile(String filename, Recipe recipe, int recipeIndex) throws IOException {
		FileReader filesupport = new FileReader();
		Cookbook cookbook = new Cookbook();
		filesupport.getRecipeFromFile(filename, cookbook);
		cookbook.replaceRecipe(recipeIndex, recipe);
		filesupport.writeRecipesToFile(filename, cookbook);
	}

	@Override
	public void replaceRecipeInFile(String filename, Recipe recipe) throws IOException {
		FileReader filesupport = new FileReader();
		Cookbook cookbook = new Cookbook();
		filesupport.getRecipeFromFile(filename, cookbook);
		int index = cookbook.getRecipeIndex(recipe.getName());
		cookbook.replaceRecipe(index, recipe);
		filesupport.writeRecipesToFile(filename, cookbook);
	}

	@Override
	public void removeRecipeFromFile(String filename, int recipeIndex) throws IOException {
		FileReader filesupport = new FileReader();
		Cookbook cookbook = new Cookbook();
		filesupport.getRecipeFromFile(filename, cookbook);
		cookbook.removeRecipe(recipeIndex);
		filesupport.writeRecipesToFile(filename, cookbook);
	}

	@Override
	public void removeRecipeFromFile(String filename, Recipe recipe) throws IOException {
		FileReader filesupport = new FileReader();
		Cookbook cookbook = new Cookbook();
		filesupport.getRecipeFromFile(filename, cookbook);

		for (int j = 0; j < cookbook.getBook().size(); j++) {
			if (cookbook.getBook().get(j).getName().equals(recipe.getName())) {
				cookbook.removeRecipe(j);
			}
		}
		filesupport.writeRecipesToFile(filename, cookbook);
	}

	public static void main(String[] args) {

	}

}
