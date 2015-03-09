package net.turrem.app.client.render;

import org.lwjgl.opengl.GL11;

import net.turrem.app.client.render.fbo.DiffuseFBO;

public class IntroScreen extends RenderScreen
{
	public IScreenLayer background;
	public IScreenLayer gui;
	
	public long startTime;
	
	public IntroScreen(DiffuseFBO target)
	{
		super(RenderEngine.instance, target);
		
		this.startTime = System.currentTimeMillis();
	}
	
	@Override
	public void render()
	{
		GL11.glClearColor(0 / 255.0F, 0 / 255.0F, 0 / 255.0F, 1.0F);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		if (this.background != null)
		{
			this.renderLayer(this.background);
		}
		if (this.gui != null)
		{
			this.renderLayer(this.gui);
		}
		if (System.currentTimeMillis() - this.startTime > 8000)
		{
			
		}
	}
	
	@Override
	public void end()
	{
	}
}
