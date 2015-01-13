package net.turrem.app.tile;

import net.turrem.app.mod.ModInstance;

public abstract class BasicTile extends Tile
{
	public BasicTile(String id, ModInstance mod)
	{
		super(id, mod);
	}
	
	public abstract int getColor();
}
