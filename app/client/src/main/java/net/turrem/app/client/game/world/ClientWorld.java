package net.turrem.app.client.game.world;

import net.turrem.app.Config;
import net.turrem.app.client.game.ClientGame;
import net.turrem.app.client.game.world.data.ClientWorldData;
import net.turrem.app.client.render.RenderEngine;

public class ClientWorld
{
	public ClientGame theGame;
	public ClientWorldData data;
	public RenderEngine theRender;
	
	public ClientWorld(ClientGame game, ClientWorldData data)
	{
		this.theGame = game;
		this.theRender = game.theRender;
	}
	
	public Chunk getChunk(int chunkx, int chunkz)
	{
		if (this.chunks == null)
		{
			return null;
		}
		return this.chunks.getChunk(chunkx, chunkz);
	}
	
	public int getHeight(int x, int z, int empty)
	{
		Chunk c = this.getChunk(x >> Config.chunkBitSize, z >> Config.chunkBitSize);
		if (c == null)
		{
			return empty;
		}
		return c.getHeight(x, z);
	}
	
	public int getHeight(int x, int z)
	{
		return this.getHeight(x, z, -1);
	}
}
