package net.turrem.app.tile;

import net.turrem.app.IdentifiableModFeature;
import net.turrem.app.mod.ModInstance;

public abstract class Tile extends IdentifiableModFeature
{
	public Tile(String id, ModInstance mod)
	{
		super(id, mod);
	}
}
