package cookbook.project.fxui;

import java.io.FileNotFoundException;
import java.io.IOException;

import cookbook.project.Cookbook;
import cookbook.project.Recipe;

public interface IFileReader {

	public void writeRecipesToFile(String filename, Cookbook cookbook) throws IOException;

	public void getRecipeFromFile(String filename, Cookbook cookbook) throws FileNotFoundException;

	public void writeRecipeToFile(String filename, Recipe recipe) throws IOException;

	public void replaceRecipeInFile(String filename, Recipe recipe, int recipeIndex) throws IOException;

	public void replaceRecipeInFile(String filename, Recipe recipe) throws IOException;

	public void removeRecipeFromFile(String filename, int recipeIndex) throws IOException;

	public void removeRecipeFromFile(String filename, Recipe recipe) throws IOException;
}
