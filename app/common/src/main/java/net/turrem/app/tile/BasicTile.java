package net.turrem.app.tile;

import net.turrem.app.mod.Mod;

public abstract class BasicTile extends Tile
{
	public BasicTile(String id, Mod mod)
	{
		super(id, mod);
	}
	
	public abstract int getColor();
}
