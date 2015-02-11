package net.turrem.app.client.game;

import java.io.IOException;

import net.turrem.app.client.Turrem;
import net.turrem.app.client.game.world.ClientWorld;
import net.turrem.app.client.game.world.data.ClientWorldData;
import net.turrem.app.client.network.GameConnection;
import net.turrem.app.client.network.server.ServerPacket;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.network.NetworkConnection;

public class ClientGame
{
	public ClientWorld theWorld;
	public GameConnection theConnection;
	private NetworkConnection network;
	protected Turrem theTurrem;
	public RenderEngine theRender;
	
	public boolean isRunning;
	
	private Timer timer;
	
	public ClientGame(RenderEngine engine, Turrem turrem)
	{
		this.theTurrem = turrem;
		this.theRender = engine;
	}
	
	public void run()
	{
		this.isRunning = true;
		this.timer = new Timer();
		this.timer.ticksPerSecond = 20.0F;
		
		Exception error = null;
		
		try
		{
			this.startNetwork(this.theTurrem.theSession.username);
		}
		catch (IOException e)
		{
			error = e;
			this.isRunning = false;
		}
		
		while (this.isRunning)
		{
			try
			{
				this.runLoop();
			}
			catch (Exception e)
			{
				error = e;
				break;
			}
		}
		
		if (error != null)
		{
			this.theTurrem.onCrash(error);
		}
	}
	
	public void runLoop()
	{
		this.timer.update();
		for (int i = 0; i < this.timer.elapsedTicks; i++)
		{
			
		}
	}
	
	public void enterWorld(ClientWorldData data)
	{
	}
	
	public void startNetwork(String username) throws IOException
	{
		this.network = null;//new NetworkConnection(new Socket(Config.turremServerHost, Config.turremServerPort));
		this.theConnection = null;//new GameConnection(this.network, this);
	}
	
	public void processPacket(ServerPacket pack)
	{
		
	}
	
	public void end()
	{
		this.isRunning = false;
	}
}
