package tech.showierdata.pickaxe.config;

/**
 * An enum of MC Colors
 * <h3>Values:</h3>
 * <ul>
 * <li>Azure        (&b)
 * <li>Blue         (&9)
 * <li>Red          (&c)
 * <li>Maroon       (&4)
 * <li>Yellow       (&e)
 * <li>Gold         (&6)
 * <li>Green        (&a)
 * <li>Sage         (&2)
 * <li>Purple       (&d)
 * <li>Light Gray   (&7)
 * <li>Gray         (&8)
 * <li>Black        (&0)
 */
public enum ColorsEnum {
    Azure("§bAzure"),
    Blue("§9Blue"),
    Red("§cRed"),
    Maroon("§4Maroon"),
    Yellow("§eYellow"),
    Gold("§6Gold"),
    Green("§aGreen"),
    Sage("§2Sage"),
    Purple("§dPurple"),
    LightGray("§7Light Gray"),
    Gray("§8Gray"),
    Black("§0Black");

    final String name;

    ColorsEnum(String name) {
        this.name = name;
    }
}
