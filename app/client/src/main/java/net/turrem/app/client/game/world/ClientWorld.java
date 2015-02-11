package net.turrem.app.client.game.world;

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
	
	public int getHeight(int x, int z, int empty)
	{
		return empty;
	}
	
	public int getHeight(int x, int z)
	{
		return this.getHeight(x, z, -1);
	}
}
