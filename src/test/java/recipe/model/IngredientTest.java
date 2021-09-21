package recipe.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import project.Ingredient;

public class IngredientTest {
	private double epsilon = 0.000001d;
	Ingredient ingredient;

	@BeforeEach
	public void setup() {
		ingredient = new Ingredient("Egg", 2, "stk");
	}

	private static void checkInvalidConstructor(String name, double volume, String unit) {
		assertThrows(IllegalArgumentException.class, () -> {
			new Ingredient(name, volume, unit);
		});
	}

	@Test
	void testConstructor() {
		Assertions.assertEquals(2, ingredient.getAmount(), epsilon);
		Assertions.assertEquals("Egg", ingredient.getName());
		Assertions.assertEquals("stk", ingredient.getUnit());
		checkInvalidConstructor("Ae12", 3, "gram");
		checkInvalidConstructor("Ae", -2, "gram");
		checkInvalidConstructor("Ae", 3, "43");
		assertThrows(IllegalArgumentException.class, () -> {
			new Ingredient("ae3");
		});
		Ingredient testZeroAmount = new Ingredient("test", 0, "test");
		Assertions.assertEquals(null, testZeroAmount.getUnit());
	}

	@Test
	void testSetName() {
		assertThrows(IllegalArgumentException.class, () -> {
			ingredient.setName("a42");
		});
		Assertions.assertDoesNotThrow(() -> {
			ingredient.setName("Fisk");
		});
		Assertions.assertEquals("Fisk", ingredient.getName());
	}

	@Test
	void testSetUnit() {
		assertThrows(IllegalArgumentException.class, () -> {
			ingredient.setUnit("a42");
		});
		Assertions.assertDoesNotThrow(() -> {
			ingredient.setName("gram");
		});
		Assertions.assertEquals("gram", ingredient.getName());
	}

	@Test
	void testSetAmount() {
		assertThrows(IllegalArgumentException.class, () -> {
			ingredient.setAmount(-2);
		});

		Assertions.assertDoesNotThrow(() -> {
			ingredient.setAmount(3);
		});
		Assertions.assertEquals(3, ingredient.getAmount());
		ingredient.setAmount(0);
		Assertions.assertEquals(0, ingredient.getAmount());
		Assertions.assertEquals(null, ingredient.getUnit());
	}

}
