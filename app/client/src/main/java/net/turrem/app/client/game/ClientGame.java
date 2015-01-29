package net.turrem.app.client.game;

import java.io.IOException;

import net.turrem.app.client.Turrem;
import net.turrem.app.client.game.world.ClientWorld;
import net.turrem.app.client.render.RenderEngine;

public class ClientGame
{
	public ClientWorld theWorld;
	protected Turrem theTurrem;
	public RenderEngine theRender;
	
	public ClientGame(RenderEngine engine, Turrem turrem)
	{
		this.theTurrem = turrem;
		this.theRender = engine;
		this.theWorld = new ClientWorld(this);
		
		try
		{
			this.theWorld.startNetwork(turrem.theSession.username);
		}
		catch (IOException e)
		{
			System.out.println(e);
			this.theWorld.end();
		}
	}
	
	public void tick()
	{
		
	}
	
	public void render()
	{
		
	}
}
