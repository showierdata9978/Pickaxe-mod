package tech.showierdata.pickaxe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;

@SuppressWarnings("unused")
public class Backpack {
	final int id;
	final int i;

	final Inventory inventory;
	final ItemStack head;

	public Backpack(int id, int i, Inventory inventory, ItemStack head) {
		this.id = id;
		this.i = i;
		this.inventory = inventory;
		this.head = head;

		assert head.getItem() instanceof SkullItem;


	}
}
