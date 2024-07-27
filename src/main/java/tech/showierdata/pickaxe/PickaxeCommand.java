package tech.showierdata.pickaxe;

import tech.showierdata.pickaxe.commands.PickaxeCommandController;

public class PickaxeCommand {
	public String name = "";
	public String data = "";
	public String[] arguments = {};
	public PickaxeCommandController handler;

	public PickaxeCommand(String name, String data, String[] arguments, PickaxeCommandController controller) {
   		this.name = name;
    	this.data = data;
		this.arguments = arguments;
		this.handler = controller;
	}

}
