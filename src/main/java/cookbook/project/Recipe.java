package cookbook.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cookbook.project.fxui.FileReader;

public class Recipe {

	// En oppskrift har har tittel, antall porsjoner og en liste med ingredienser.
	// Den har også felt for å legge til informasjon om oppskriften og
	// en liste med steg-for-steg instrukser. "tags" er en array med 5 elementer,
	// men bare de to første blir brukt. Dette var for å legge til
	// plass i tilfelle jeg ville implementere flere tags senere. For øyeblikket
	// brukes de for å sortere om oppskriften er en "favoritt" eller
	// en "original oppskrift" som brukeren selv har laget.
	// Dersom jeg senere endret hvordan jeg håndterer disse emneknaggene har jeg
	// egne predicates slik at jeg ikke trenger å endre koden i kontrollerene selv
	// om jeg endrer implementereingen av tags.
	// Oppskriftene har en bildefil, men dette er egentlig ikke en del av selve
	// programmet, jeg bare la det inn for at programmet så pent ut i begynnelsen.
	// Jeg innså fort at jeg ikke
	// kom til å legge inn metoder for å la brukeren legge inn bilder, siden det
	// virket som mye å sette seg inn i, så det er bare for det visuelle.

	private String recipeName;
	private int portions;

	private List<Ingredient> ingredienser = new ArrayList<>();
	private List<String> steps = new ArrayList<>();
	private String info;
	private String imageFileName = ("file:///C:/Users/nicol/git/tdt4100-prosjekt-nicolimo/cookbook.project/src/main/resources/hh/Worlds-Best-Vegetarian-Omelette-Quick-and-Easy.jpg");
	private int[] tags = { 0, 0, 0, 0, 0 };
	private FavoritePredicate fp = new FavoritePredicate();
	private MyRecipePredicate mrp = new MyRecipePredicate();

	public Recipe(String name, int portions) {
		if (name.equals("") || name == null) {
			throw new NullPointerException(
					"Du har felt som mangler! Sørg for at oppskriften har tittel og antall porsjoner");
		}
		if (portions <= 0) {
			throw new IllegalArgumentException("Porsjoner må være større enn 0");
		}
		if (!name.matches("^[ÆØÅæøåa-zA-Z0-9\\s]+$")) {
			throw new IllegalArgumentException("Tittel kan bare bestå av bokstaver og tall");
		} else {
			this.recipeName = name;
			this.portions = portions;

		}

	}

	public void setName(String recipeName) {
		if (!recipeName.matches("^[ÆØÅæøåa-zA-Z0-9\\s]+$")) {
			throw new IllegalArgumentException("Tittel kan bare bestå av bokstaver og tall");
		}
		this.recipeName = recipeName;
	}

	public String getName() {
		return recipeName;
	}

	public void setPortions(int p) {
		if (p <= 0) {
			throw new IllegalArgumentException("Porsjoner må være større enn 0");
		}
		this.portions = p;
	}

	public int getPortions() {
		return portions;
	}

	public void addIngredient(String type, double amount, String unit) {
		if (this.contains(type)) {
			throw new IllegalArgumentException("Denne ingredienser finnes allerede i oppskriften");
		}
		ingredienser.add(new Ingredient(type, amount, unit));

	}

	public void addIngredient(Ingredient ingredient) {
		if (this.contains(ingredient.getName())) {
			throw new IllegalArgumentException("Denne ingredienser finnes allerede i oppskriften");
		}
		ingredienser.add(ingredient);
	}

	public List<Ingredient> getIngredients() {
		return ingredienser;
	}

	public void setInfo(String tekst) {
		if (tekst.matches("^[ÆØÅæøåa-zA-Z\\d_.,-?!\s]+$")) {
			info = tekst;
		} else {
			throw new IllegalArgumentException("Infoteksen kan bare bestå av bokstaver, tall og !?- ");
		}
	}

	public String getInfo() {
		return info;
	}

	public void setImage(String imagefile) {
		imageFileName = imagefile;
	}

	public String getImage() {
		return imageFileName;
	}

	public void setSteps(List<String> steps) {
		this.steps.addAll(steps);
	}

	public void addStep(String step) {
		steps.add(step);
	}

	public List<String> getSteps() {
		return steps;
	}

	public boolean contains(String type) {
		for (Ingredient d : ingredienser) {
			if (d.getName().equals(type)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(Ingredient ingredient) {
		if (ingredienser.contains(ingredient)) {
			return true;
		}
		return false;
	}

	public int[] getTags() {
		return tags;
	}

	public void setTags(int[] tags) {
		this.tags = tags;
	}

	public boolean isFavorite() {
		return fp.test(this);
	}

	public void setFavorite() {
		this.tags[0] = 1;
	}

	public void removeFavorite() {
		this.tags[0] = 0;
	}

	public boolean isOriginalRecipe() {
		return mrp.test(this);
	}

	public void setOriginalRecipe() {
		tags[1] = 1;
	}

	public void removeOriginalRecipe() {
		tags[1] = 0;
	}

	public void changePortionSize(int portions) {
		if (portions <= 0) {
			throw new IllegalArgumentException("Kan ikke sette porsjonene til null eller negativ");
		}
		double ratio = ((Double.valueOf(portions) / (double) this.portions));

		for (Ingredient d : ingredienser) {
			double vol = d.getAmount();
			double amount = ratio * vol;
			d.setAmount(amount);
		}

		this.portions = portions;
	}

	public String toString() {
		String ing = "";
		for (Ingredient d : ingredienser) {
			ing += d + "\n";
		}
		return ing;
	}

	public static void main(String[] args) throws IOException {
		// Recipe recipe1 = new Recipe("recipe1", 2);
		// recipe1.addIngredient("Aspareges", 2, "stk");
		// recipe1.addIngredient(new Ingredient("Fisk", 3, "dl"));

		// Recipe recipe2 = new Recipe("recipe2", 2);
		// recipe2.addIngredient("Egg", 3, "Stk");
		// recipe2.setInfo("test");
		// recipe2.setFavorite();
		// List<String> stepsList = new ArrayList<>();
		// stepsList.add("steg1");
		// recipe2.setSteps(stepsList);

		// Cookbook cookbook = new Cookbook();
		// cookbook.addRecipe(recipe1);
		// cookbook.addRecipe(recipe2);
		FileReader filereader = new FileReader();
		filereader.writeRecipesToFile("src/test/java/FileReaderTestFile/testFileReaderReplaceFile.txt", cookbook);
	}
}
