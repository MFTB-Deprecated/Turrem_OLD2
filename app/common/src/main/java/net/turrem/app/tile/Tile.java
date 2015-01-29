package net.turrem.app.tile;

import net.turrem.app.IdentifiableFeature;
import net.turrem.app.mod.Mod;

public abstract class Tile extends IdentifiableFeature
{
	public Tile(String id, Mod mod)
	{
		super(mod, id);
	}
}
