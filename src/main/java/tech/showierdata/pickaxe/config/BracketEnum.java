package tech.showierdata.pickaxe.config;

/**
 * Different border options for Message Stacking
 * <h3>Values:</h3>
 * <ul>
 * <li>Square
 * <li>Curly
 * <li>Angled
 * <li>Round
 * <li>None
 * <li>Custom
 */
public enum BracketEnum {
    Square("Square"),
    Curly("Curly"),
    Angled("Angled"),
    Round("Round"),
    None("None"),
	Custom("Custom");

    final String name;

    BracketEnum(String name) {
        this.name = name;

		//this.MessageStackPattern = this.getMessageStackPattern();
    }
}
