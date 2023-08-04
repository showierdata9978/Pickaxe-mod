package tech.showierdata.pickaxe;

import net.minecraft.util.math.Vec3d;

public class Constants {

	public static final String PICKAXE_STRING = "Pickaxe Mod";


    @SuppressWarnings("unused")
    public static final char DIAMOND_CHAR = '◆';
	public static final Vec3d Spawn = new Vec3d(7085, 200, 4115);
	public static final int PLOT_ID = 50644;
    public static final String SERVER_IP = "mcdiamondfire.com";

    public static final int nodes = 7;
    public static final String NODE_IP = getNode(PLOT_ID);

    public static String getNode(int id) throws ArrayIndexOutOfBoundsException {
        //get the first number of the id
        int first = Integer.parseInt(Integer.toString(id).substring(0, 1));

        if (first > nodes) {
            throw new ArrayIndexOutOfBoundsException("Node " + first + " does not exist");
        }

        String node = "node" + first;
        return node + "." + SERVER_IP;
    }

    public static  final  int NATRAL_OVERCLOCK_VALUE = 3;
    public static final int MANUAL_OVERCLOCK_VALUE = 4;
    public static final int NATRAL_SAGE_VALUE = 1;
    public static  final int MANUAL_SAGE_VALUE = 2;



}