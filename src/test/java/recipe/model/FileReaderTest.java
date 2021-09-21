package recipe.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import project.Cookbook;
import project.Ingredient;
import project.Recipe;
import project.fxui.FileReader;

public class FileReaderTest {
	private Cookbook cookbook;
	private Recipe recipe1;
	private Recipe recipe2;
	private FileReader fileReader;
	File tempFile;
	Path path;

	@TempDir // For å oprette midlertidige filer som slettes etter testene
	Path tempDir;

	@BeforeEach
	public void setup() {
		fileReader = new FileReader();
		recipe1 = new Recipe("recipe1", 2);
		recipe1.addIngredient("Aspareges", 2, "stk");

		recipe2 = new Recipe("recipe2", 2);
		recipe2.addIngredient("Egg", 3, "Stk");
		recipe2.setInfo("test");
		recipe2.setFavorite();
		List<String> stepsList = new ArrayList<>();
		stepsList.add("steg1");
		recipe2.setSteps(stepsList);

		cookbook = new Cookbook();
		cookbook.addRecipe(recipe1);
		cookbook.addRecipe(recipe2);

		try {
			path = tempDir.resolve("new-file-test");
		} catch (InvalidPathException e) {
			fail("Could not create temporary file");
		}
		tempFile = path.toFile();
	}

	public boolean compareCookbooks(Cookbook c1, Cookbook c2) {
		StringBuffer stringBuffer1 = new StringBuffer();
		StringBuffer stringBuffer2 = new StringBuffer();

		for (Recipe r : c1.getBook()) {
			stringBuffer1.append(r.toString());
			stringBuffer1.append(" ");
		}
		for (Recipe r : c2.getBook()) {
			stringBuffer2.append(r.toString());
			stringBuffer2.append(" ");
		}
		String string1 = stringBuffer1.toString();
		String string2 = stringBuffer2.toString();
		return (string1.equals(string2));
	}

	public boolean compareFileString(Path p1, Path p2) throws IOException {
		byte[] fileToTest = null, tempFileTest = null;

		fileToTest = Files.readAllBytes(p1);
		tempFileTest = Files.readAllBytes(p2);

		return (Arrays.equals(fileToTest, tempFileTest));
	}

	@Test
	public void testGetRecipeFromFile() throws FileNotFoundException {
		Cookbook newCookbook = new Cookbook();
		fileReader.getRecipeFromFile("src/test/java/FileReaderTestFile/testFileReaderFile.txt", newCookbook);
		assertTrue(compareCookbooks(cookbook, newCookbook));
	}

	public void saveRecipeToFile() throws IOException {
		fileReader.writeRecipeToFile(tempFile.getAbsolutePath(), recipe1);
		fileReader.writeRecipeToFile(tempFile.getAbsolutePath(), recipe2);
	}

	@Test
	public void testWriteRecipeToFile() throws IOException {
		saveRecipeToFile();
		assertTrue(compareFileString(Path.of("src/test/java/FileReaderTestFile/testFileReaderFile.txt"), path));

	}

	@Test
	public void testWriteRecipesToFile() throws IOException {
		fileReader.writeRecipesToFile(tempFile.getAbsolutePath(), cookbook);

		assertTrue(compareFileString(Path.of("src/test/java/FileReaderTestFile/testFileReaderFile.txt"), path));

	}

	@Test
	public void testReplaceRecipeInFile() throws IOException {
		recipe1.addIngredient(new Ingredient("Fisk", 3, "dl"));
		saveRecipeToFile();
		fileReader.replaceRecipeInFile(tempFile.getAbsolutePath(), recipe1);

		assertTrue(compareFileString(Path.of("src/test/java/FileReaderTestFile/testFileReaderReplaceFile.txt"), path),
				"Contents should be the same after replacing recipe in file");
	}

	@Test
	public void testReplaceRecipeInFileByIndex() throws IOException {
		recipe1.addIngredient(new Ingredient("Fisk", 3, "dl"));
		saveRecipeToFile();
		fileReader.replaceRecipeInFile(tempFile.getAbsolutePath(), recipe1, 0);

		assertTrue(compareFileString(Path.of("src/test/java/FileReaderTestFile/testFileReaderReplaceFile.txt"), path),
				"Contents should be the same after replacing recipe in file");
	}

	@Test
	public void testFileNotFoundException() {
		assertThrows(FileNotFoundException.class, () -> fileReader.getRecipeFromFile("stopid", cookbook),
				"FileNotFoundException should be thrown when file does not exist!");
	}

	@Test
	public void testRemoveRecipeFromFileByIndex() throws IOException {
		Recipe recipe3 = new Recipe("Recipe3", 2);
		cookbook.addRecipe(recipe3);
		saveRecipeToFile();
		fileReader.writeRecipeToFile(tempFile.getAbsolutePath(), recipe3);
		fileReader.removeRecipeFromFile(tempFile.getAbsolutePath(), 2);

		assertTrue(compareFileString(Path.of("src/test/java/FileReaderTestFile/testFileReaderFile.txt"), path));
	}

	@Test
	public void testRemoveRecipeFromFileByRecipe() throws IOException {
		Recipe recipe3 = new Recipe("Recipe3", 2);
		cookbook.addRecipe(recipe3);
		saveRecipeToFile();
		fileReader.writeRecipeToFile(tempFile.getAbsolutePath(), recipe3);
		fileReader.removeRecipeFromFile(tempFile.getAbsolutePath(), recipe3);
		assertTrue(compareFileString(Path.of("src/test/java/FileReaderTestFile/testFileReaderFile.txt"), path));
	}
}
