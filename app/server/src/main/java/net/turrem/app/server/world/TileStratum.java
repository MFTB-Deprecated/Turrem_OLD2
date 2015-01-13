package net.turrem.app.server.world;

import net.turrem.app.Config;
import net.turrem.app.tile.Tile;

public class TileStratum
{
	public byte[] height = new byte[Config.chunkSize * Config.chunkSize];
	public Tile type;
}
