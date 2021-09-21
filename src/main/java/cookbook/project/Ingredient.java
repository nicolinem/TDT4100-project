package cookbook.project;

public class Ingredient {

	private String name;
	private double amount;
	private String unit;

	// Ingrediensene bygger opp oppskriftene. De har navn, mengde og enhet.

	public Ingredient(String type, double amount, String unit) {
		if (!validateName(type) || !validateName(unit)) {
			throw new IllegalArgumentException("Ingrediens og enhet kan kun bestå av bokstaver");
		}
		if (amount < 0) {
			throw new IllegalArgumentException("Ingrediensvolumet må være positivt");
		}
		if (amount == 0) {
			this.amount = amount;
			this.unit = null;
		} else {
			this.name = type;
			this.amount = amount;
			this.unit = unit;
		}
	}

	public Ingredient(String type) {
		setName(type);
		amount = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String type) {
		if (validateName(type)) {
			this.name = type;
		} else {
			throw new IllegalArgumentException("Ingrediens kan kun bestå av bokstaver");
		}
	}

	private boolean validateName(String type) {
		String letters = type.replaceAll("\\s", "");
		for (int i = 0; i < letters.length(); i++) {
			if (!(Character.isLetter(letters.charAt(i)))) {
				return false;
			}
		}
		return true;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Ingrediensvolumet må være positivt");
		}
		if (amount == 0) {
			this.unit = null;
		}

		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		if (!validateName(unit)) {
			throw new IllegalArgumentException("Eenhet kan kun bestå av bokstaver");
		}
		this.unit = unit;
	}

	public String toString() {
		if (amount != 0 && unit != null) {
			if (amount % 1 != 0) {
				String amounInString = String.format("%.1f", amount);
				return "" + amounInString + "    " + unit + "\t\t" + name;
			} else {
				String amounInString = String.format("%.0f", amount);
				return "" + amounInString + "    " + unit + "\t\t" + name;
			}

		} else {
			return name + "";
		}
	}

}
