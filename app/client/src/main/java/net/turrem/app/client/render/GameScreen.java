package net.turrem.app.client.render;

import java.util.EnumMap;

public class GameScreen
{
	public static enum EnumScreenLayers
	{
		GAME_LAYER,
		BASE_UI_LAYER,
		ALERT_UI_LAYER,
		FRONT_UI_LAYER,
		FULL_UI_LAYER;
	}
	
	private EnumMap<EnumScreenLayers, IScreenLayer> layers;
	
	public GameScreen()
	{
		this.layers = new EnumMap<>(EnumScreenLayers.class);
	}
	
	public IScreenLayer getLayer(EnumScreenLayers layer)
	{
		return this.layers.get(layer);
	}
}
