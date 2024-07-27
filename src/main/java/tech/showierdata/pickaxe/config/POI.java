package tech.showierdata.pickaxe.config;

import net.minecraft.util.math.Vec3d;

public enum POI {
    SPAWN(0, 0, 0),
    MUSEUM(-23, 1, 6),
    MEDALS(-35, 3, 4),
    SPAWN_SHOP(22, 0, 0),
    BACKPACK_SHOP(-20, 0, 15),
    ELEVATOR_SHOP(30, 0, 41),
    Luigi(19, 0, 14),
    NETHER_HUB(1, -120, 25),
    MESA_SHACK(92, 0, 49),
    TECH_CRAFTING(125, 13, 67),
    TRADER(-23, 0, 38),
    ENCHANT_PROSP(28, 3, 26),
    ENCHANT_EFF(28, 5, -8),
    ENCHANT_LUCK(-19, 9, 16),
    ENCHANT_FESTIVE(15, 2, 64),
    ENCHANT_AUTOFORGE(124, 6, 60),
    ENCHANT_PLATED(124, 13, 76),
    BAZZAR(10, 0, -5),

    DIRT_GOD(27, 0, 50),
    SAND_GOD(111, 5, 60),

    WATER_CRAFTING(112, -1, 204),
    WATER_SHOP(111, -1, 208),
    WATER_FORGE(116, -1, 203),
    O2_FILL(115, -1, 208);

    private final Vec3d position;

    POI(double x, double y, double z) {
        this.position = new Vec3d(x, y, z);
    }

    public Vec3d getPosition() {
        return position;
    }
}
