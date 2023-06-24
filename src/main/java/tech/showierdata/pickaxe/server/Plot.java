package tech.showierdata.pickaxe.server;

public class Plot {
	public String server;
	public String owner;
	public String name;
	public String id;

	public Plot(String owner, String name, String server, String id) {
		this.owner = owner;
		this.name = name;
		this.server = server;
		this.id = id;
	}
}
