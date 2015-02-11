package net.turrem.app.client.game;

import net.turrem.app.client.render.IScreenLayer;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.client.render.fbo.DiffuseFBO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RenderGame implements IScreenLayer
{
	public Timer timer = null;
	
	public RenderGame()
	{
		
	}
	
	@Override
	public void render(RenderEngine engine, DiffuseFBO target)
	{
		if (this.timer == null)
		{
			this.timer = new Timer();
			this.timer.resetTimer();
			this.timer.partialTicks += 1;
		}
		
		for (int i = 0; i < this.timer.elapsedTicks; i++)
		{
			this.tickGame();
		}
		
		this.renderGame(this.timer.partialTicks, engine, target);
	}
	
	public void tickGame()
	{
		
	}
	
	public void renderGame(float partial, RenderEngine engine, DiffuseFBO target)
	{
		GLU.gluOrtho2D(0, target.width, 0, target.height);
		GL11.glColor3f(0.2F, 0.2F, 1.0F);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(100, 100);
		GL11.glVertex2f(200, 100);
		GL11.glVertex2f(200, 200);
		GL11.glVertex2f(100, 200);
		GL11.glEnd();
	}
	
	@Override
	public boolean isOutdated()
	{
		return true;
	}
	
	@Override
	public boolean hasContent()
	{
		return true;
	}
	
	@Override
	public boolean pauseRequested()
	{
		return false;
	}
	
}
