package project;

import java.util.function.Predicate;

public class FavoritePredicate implements Predicate<Recipe> {

	@Override
	public boolean test(Recipe recipe) {
		if (recipe.getTags()[0] == 1) {
			return true;
		}
		return false;
	}

}
