package net.turrem.app.server.world;

import java.util.ArrayList;

import net.turrem.app.Config;

public class TileStorageChunk
{
	public short[] heightmap;
	public ArrayList<TileStratum> strata = new ArrayList<TileStratum>();
	
	public TileStorageChunk()
	{
		this.heightmap = new short[Config.chunkSize * Config.chunkSize];
	}
}
