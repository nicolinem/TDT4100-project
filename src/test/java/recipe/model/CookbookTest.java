package recipe.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.Cookbook;
import project.Recipe;

public class CookbookTest {
	Cookbook cookbook;
	Recipe recipe1;
	Recipe recipe2;
	Recipe recipe3;

	@BeforeEach
	public void setup() {
		cookbook = new Cookbook("kokebok");
		recipe1 = new Recipe("recipe1", 2);
		recipe2 = new Recipe("recipe2", 2);
		recipe3 = new Recipe("recipe3", 2);
	}

	@Test
	void testSetName() {
		assertThrows(IllegalArgumentException.class, () -> {
			cookbook.setName("42");
		});
		Assertions.assertDoesNotThrow(() -> {
			cookbook.setName("Min kokebok");
		});

	}

	@Test
	void testAddRecipe() {
		cookbook.addRecipe(recipe1);
		cookbook.addRecipe(recipe2);
		Assertions.assertTrue(cookbook.isInCookbook("recipe1"));
		Assertions.assertTrue(cookbook.isInCookbook("recipe2"));
		cookbook.removeRecipe(1);
		Assertions.assertEquals(1, cookbook.getBook().size());
		cookbook.addRecipe(recipe3);
		Assertions.assertEquals(1, cookbook.getRecipeIndex("recipe3"));

	}

	@Test
	void testReplaceRecipe() {
		cookbook.addRecipe(recipe1);
		cookbook.addRecipe(recipe2);
		cookbook.replaceRecipe(1, recipe3);
		Assertions.assertEquals(recipe3, cookbook.getRecipe(1), "Etter byttet oppskrift");
		Assertions.assertEquals(2, cookbook.getBook().size(), "Etter byttet oppskrift skal være samme lengre");

	}

}
