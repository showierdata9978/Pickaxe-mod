package tech.showierdata;

public class PickaxeCommand {
	public String name = "";
	public String data = "";
	public String[] arguments = {};
	public boolean handled = false;

	public PickaxeCommand(String name, String data, String[] arguments) {
   		this.name = name;
    	this.data = data;
		this.arguments = arguments;
	}

	public PickaxeCommand(String name, String data, boolean handled,  String[] arguments) {
		this.name = name;
    	this.data = data;
		this.handled = handled;
		this.arguments = arguments;
	}
}
