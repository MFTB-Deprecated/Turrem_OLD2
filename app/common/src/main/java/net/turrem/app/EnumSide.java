package net.turrem.app;

public enum EnumSide
{
	CLIENT("client"),
	SERVER("sever");
	
	public final String id;
	
	EnumSide(String id)
	{
		this.id = id;
	}
}
