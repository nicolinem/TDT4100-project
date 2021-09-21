package recipe.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.FavoritePredicate;
import project.Ingredient;
import project.MyRecipePredicate;
import project.Recipe;

public class RecipeTest {
	Recipe recipe;
	Ingredient ingredient;

	@BeforeEach
	public void setup() {
		recipe = new Recipe("Omelett", 2);
		ingredient = new Ingredient("Egg", 3, "stk");
	}

	private static void checkInvalidConstructor(String name, int portions) {
		assertThrows(IllegalArgumentException.class, () -> {
			new Recipe(name, portions);
		});
	}

	@Test
	void testConstructor() {
		Assertions.assertEquals(2, recipe.getPortions());
		Assertions.assertEquals("Omelett", recipe.getName());
		checkInvalidConstructor("32#", 2);
		checkInvalidConstructor("Eple", -1);
		checkInvalidConstructor("Eple", 0);

	}

	@Test
	void testSetName() {
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.setName("##&ge");
		});
		Assertions.assertDoesNotThrow(() -> {
			recipe.setName("Eggerøre");
		});
		Assertions.assertEquals("Eggerøre", recipe.getName());
	}

	@Test
	void testSetPortions() {
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.setPortions(-2);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.setPortions(0);
		});
		Assertions.assertDoesNotThrow(() -> {
			recipe.setPortions(4);
		});
		Assertions.assertEquals(4, recipe.getPortions());
	}

	@Test
	void testAddIngredient() {
		recipe.addIngredient(ingredient);
		recipe.addIngredient("Melk", 2, "SS");
		Assertions.assertTrue(recipe.contains(ingredient), "Oppskriften burde ha denne ingrediensen");
		Assertions.assertTrue(recipe.contains("Melk"), "Oppskriften burde ha denne ingrediensen");

		assertThrows(IllegalArgumentException.class, () -> {
			recipe.addIngredient(ingredient);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.addIngredient("Melk", 2, "dl");
		});
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.addIngredient("Egg", 4, "stk");
		});

		Assertions.assertEquals(2, recipe.getIngredients().size(), "Oppskriften burde inneholde 2 ingredienser");

	}

	@Test
	void testSetInfo() {
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.setInfo("Ugyldig tegn: #");
		});
		Assertions.assertDoesNotThrow(() -> {
			recipe.setInfo("Dette er en test. æøå 321");
		});
		Assertions.assertEquals("Dette er en test. æøå 321", recipe.getInfo());
	}

	@Test
	void testSetTags() {
		Assertions.assertEquals("[0, 0, 0, 0, 0]", Arrays.toString(recipe.getTags()));
		recipe.setFavorite();
		Assertions.assertEquals("[1, 0, 0, 0, 0]", Arrays.toString(recipe.getTags()), "Etter gjort til favoritt");
		recipe.setOriginalRecipe();
		Assertions.assertEquals("[1, 1, 0, 0, 0]", Arrays.toString(recipe.getTags()),
				"Etter markert som egen oppskrift");
		recipe.removeFavorite();
		Assertions.assertEquals("[0, 1, 0, 0, 0]", Arrays.toString(recipe.getTags()), "Etter fjernet som favoritt");
		recipe.removeOriginalRecipe();
		Assertions.assertEquals("[0, 0, 0, 0, 0]", Arrays.toString(recipe.getTags()),
				"Etter fjernet som egen oppskrift");

		FavoritePredicate fp = new FavoritePredicate();
		MyRecipePredicate mrp = new MyRecipePredicate();

		Assertions.assertFalse(fp.test(recipe));
		Assertions.assertFalse(mrp.test(recipe));

	}

	@Test
	void testSetSteps() {
		List<String> steg = new ArrayList<>(Arrays.asList("Steg 1", "Steg 2"));
		String steg3 = "Steg 3";

		recipe.setSteps(steg);
		recipe.addStep(steg3);
		Assertions.assertTrue(recipe.getSteps().contains("Steg 1"));
		Assertions.assertTrue(recipe.getSteps().contains("Steg 2"));
		Assertions.assertTrue(recipe.getSteps().contains("Steg 3"));
		Assertions.assertEquals(3, recipe.getSteps().size());

	}

	@Test
	void testChangePortionSize() {
		recipe.addIngredient(ingredient);
		recipe.changePortionSize(4);
		Assertions.assertEquals(4, recipe.getPortions());
		Assertions.assertEquals(6d, recipe.getIngredients().get(0).getAmount(), 0.000001d);
		recipe.changePortionSize(1);
		Assertions.assertEquals(1.5d, recipe.getIngredients().get(0).getAmount(), 0.000001d);
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.changePortionSize(-1);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			recipe.changePortionSize(0);
		});
	}

	@Test
	void testSetImage() {
		Assertions.assertEquals(
				"file:///C:/Users/nicol/git/tdt4100-prosjekt-nicolimo/project/src/main/resources/hh/Worlds-Best-Vegetarian-Omelette-Quick-and-Easy.jpg",
				recipe.getImage());
		recipe.setImage("bildestreng, dette hånderes ikke i denn klassen");
		Assertions.assertEquals("bildestreng, dette hånderes ikke i denn klassen", recipe.getImage());

	}

}
