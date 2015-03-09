package net.turrem.app.client.game.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import net.turrem.app.client.game.ClientGame;
import net.turrem.app.client.game.world.data.ClientWorldData;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.client.render.lights.Light;

public class ClientWorld
{
	public ClientGame theGame;
	public ClientWorldData data;
	public RenderEngine theRender;
	
	public Vector3f ambientLightColor;
	public float ambientLightLevel;
	
	public ArrayList<Light> lights = new ArrayList<Light>();
	
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
