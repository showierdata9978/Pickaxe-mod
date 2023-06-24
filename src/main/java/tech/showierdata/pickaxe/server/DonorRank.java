package tech.showierdata.pickaxe.server;

import tech.showierdata.pickaxe.Pickaxe;

public enum DonorRank implements Rank {
	
    NOBLE("Noble"),
    EMPEROR("Emperor"),
    MYTHIC("Mythic"),
    OVERLORD(Pickaxe.DIAMOND_CHAR + "Overlord" + Pickaxe.DIAMOND_CHAR);
	
    private final String displayName;

    DonorRank(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}