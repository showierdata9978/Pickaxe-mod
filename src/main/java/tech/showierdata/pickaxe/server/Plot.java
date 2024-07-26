package tech.showierdata.pickaxe.server;

@SuppressWarnings("ClassCanBeRecord")
public class Plot {
	public final String server;
	public final String owner;
	public final String name;
	public final String id;

	public Plot(String owner, String name, String server, String id) {
		this.owner = owner;
		this.name = name;
		this.server = server;
		this.id = id;
	}
}
