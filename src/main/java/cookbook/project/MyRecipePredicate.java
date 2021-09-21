package cookbook.project;

import java.util.function.Predicate;

public class MyRecipePredicate implements Predicate<Recipe> {

	@Override
	public boolean test(Recipe t) {
		if (t.getTags()[1] == 1) {
			return true;
		}
		return false;
	}

}
