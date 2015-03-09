package net.turrem.app.client.render;

import java.util.EnumMap;

import org.lwjgl.opengl.GL11;

import net.turrem.app.client.game.ClientGame;
import net.turrem.app.client.game.RenderGame;
import net.turrem.app.client.render.fbo.DiffuseFBO;

public class GameScreen extends RenderScreen
{
	public static enum EnumScreenLayers
	{
		BACKGROUND_LAYER,
		GAME_LAYER,
		BASE_UI_LAYER,
		ALERT_UI_LAYER,
		FRONT_UI_LAYER,
		FULL_UI_LAYER;
	}
	
	private EnumMap<EnumScreenLayers, IScreenLayer> layers;
	
	public RenderGame theGame;
	
	public GameScreen(ClientGame game, DiffuseFBO target)
	{
		super(RenderEngine.instance, target);
		this.theGame = new RenderGame(game);
		this.layers.put(EnumScreenLayers.GAME_LAYER, this.theGame);
	}
	
	public IScreenLayer getLayer(EnumScreenLayers layer)
	{
		return this.layers.get(layer);
	}
	
	public void render()
	{
		GL11.glClearColor(33 / 255.0F, 43 / 255.0F, 63 / 255.0F, 1.0F);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		for (IScreenLayer layer : this.layers.values())
		{
			if (layer != null)
			{
				this.renderLayer(layer);
			}
		}
	}
	
	@Override
	public void end()
	{
	}
}
