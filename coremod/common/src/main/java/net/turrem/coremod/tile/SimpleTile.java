package net.turrem.coremod.tile;

import net.turrem.app.mod.ModInstance;
import net.turrem.app.tile.BasicTile;

public class SimpleTile extends BasicTile
{
	private int color;
	
	public SimpleTile(String id, ModInstance mod)
	{
		super(id, mod);
	}
	
	public SimpleTile setColor(int color)
	{
		this.color = color;
		return this;
	}
	
	@Override
	public int getColor()
	{
		return this.color;
	}
}
