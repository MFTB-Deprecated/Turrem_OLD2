package net.turrem.app.tile;

import java.util.HashMap;

public class TileRegistry
{
	private static HashMap<String, Tile> tiles = new HashMap<String, Tile>();
	
	public static Tile getTile(String id)
	{
		return tiles.get(id);
	}
	
	public static void register(Tile tile)
	{
		if (tile instanceof BasicTile || tile instanceof DynamicTile)
		{
			String id = tile.getRaw();
			if (tiles.containsKey(id))
			{
				System.out.printf("A tile with id %s already exists, it will be overridden!");
			}
			tiles.put(id, tile);
		}
		System.out.printf("Failed to register a tile (id: %s) because it did not extend BasicTile or DynamicTile.%n", tile.getRaw());
	}
}
