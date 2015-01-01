package net.turrem.app.tile;

import net.turrem.app.EnumSide;
import net.turrem.app.mod.ModInstance;

public abstract class Tile
{
	EnumSide side = null;
	private final String id;
	ModInstance mod = null;
	
	public Tile(String id)
	{
		this.id = id;
	}
	
	public String getId()
	{
		return this.mod.identifier + ":" + this.id;
	}
	
	public String getInternalId()
	{
		return this.id;
	}
	
	public ModInstance getMod()
	{
		return this.mod;
	}
	
	public EnumSide getSide()
	{
		return this.side;
	}
}
