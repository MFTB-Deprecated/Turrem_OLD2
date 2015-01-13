package net.turrem.app.server.world;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.turrem.app.Config;
import net.turrem.app.tile.Tile;
import net.turrem.app.tile.TileRegistry;

public class TestWorldLoad
{
	private BufferedImage heightmap;
	private BufferedImage baseHeightmap;
	private BufferedImage tiles;
	
	private Tile heighForrest;
	private Tile lowForrest;
	private Tile beach;
	private Tile ocean;
	private Tile tundra;
	private Tile snowCap;
	private Tile stream;
	private Tile river;
	private Tile slowRiver;
	private Tile lake;
	private Tile silt;
	
	public TestWorldLoad()
	{
		this.ocean = TileRegistry.getTile("turrem:oceanWater");
		this.beach = TileRegistry.getTile("turrem:beachSand");
		this.lowForrest = TileRegistry.getTile("turrem:lowForrestGrass");
		this.heighForrest = TileRegistry.getTile("turrem:heighForrestGrass");
		this.tundra = TileRegistry.getTile("turrem:tundraGrass");
		this.snowCap = TileRegistry.getTile("turrem:tundraSnow");
		this.stream = TileRegistry.getTile("turrem:streamWater");
		this.river = TileRegistry.getTile("turrem:fastRiverWater");
		this.slowRiver = TileRegistry.getTile("turrem:riverWater");
		this.lake = TileRegistry.getTile("turrem:lakeWater");
		this.silt = TileRegistry.getTile("turrem:silt");
	}
	
	public void loadMapsFromJar() throws IOException
	{
		this.heightmap = ImageIO.read(this.getClass().getResourceAsStream("/height.png"));
		this.baseHeightmap = ImageIO.read(this.getClass().getResourceAsStream("/base_height.png"));
		this.tiles = ImageIO.read(this.getClass().getResourceAsStream("/tiles.png"));
	}
	
	private TileStorageChunk getChunkTiles(int px, int py)
	{
		TileStorageChunk chunk = new TileStorageChunk();
		TileStratum lake = new TileStratum();
		TileStratum ocean = new TileStratum();
		TileStratum river = new TileStratum();
		for (int i = 0; i < Config.chunkSize; i++)
		{
			for (int j = 0; j < Config.chunkSize; j++)
			{
				int k = i + j * Config.chunkSize;
				chunk.heightmap[k] = (short) this.convertHeight(this.heightmap.getRGB(px + i, py + j) & 0xFF);
				Tile top = this.getTile(this.tiles.getRGB(px + i, py + j));
				if (top == this.lake || top == this.ocean)
				{
					int depth = chunk.heightmap[k] - this.convertHeight(this.baseHeightmap.getRGB(px + i, py + j) & 0xFF);
					if (top == this.lake)
					{
						lake.height[k] = (byte) depth;
					}
					else if (top == this.ocean)
					{
						ocean.height[k] = (byte) depth;
					}
				}
				else if (top == this.river)
				{
					river.height[k] = 1;
				}
			}
		}
		return chunk;
	}
	
	private int convertHeight(int grey)
	{
		return grey;
	}
	
	private Tile getTile(int color)
	{
		switch (color)
		{
			case 0x0026FF:
				return this.ocean;
			case 0xFFF0A8:
				return this.beach;
			case 0x31A500:
				return this.lowForrest;
			case 0x21702F:
				return this.heighForrest;
			case 0xFFFFFF:
				return this.snowCap;
			case 0x00CEBD:
				return this.river;
			case 0x0094FF:
				return this.lake;
			default:
				return null;
		}
	}
}
