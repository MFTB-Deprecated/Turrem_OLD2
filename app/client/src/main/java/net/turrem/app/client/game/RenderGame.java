package net.turrem.app.client.game;

import java.io.IOException;
import java.util.Random;

import net.turrem.app.client.Turrem;
import net.turrem.app.client.asset.GameAsset;
import net.turrem.app.client.font.Font;
import net.turrem.app.client.render.IScreenLayer;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.client.render.fbo.DiffuseFBO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RenderGame implements IScreenLayer
{
	public Timer timer = null;
	public Font testFont;
	public String testString = "";
	
	public RenderGame()
	{
		try
		{
			this.testFont = Font.loadFont(GameAsset.fromRaw("fonts/Arial.fnt"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		this.testFont.load(Turrem.instance().theAssetLoader);
		int[] chars = this.testFont.glyphs.keys();
		Random rand = new Random();
		for (int i = 0; i < 40; i++)
		{
			this.testString += (char) chars[rand.nextInt(chars.length)];
		}
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
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0, target.width, 0, target.height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glColor3f(0.2F, 0.2F, 1.0F);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(100, 100);
		GL11.glVertex2f(200, 100);
		GL11.glVertex2f(200, 200);
		GL11.glVertex2f(100, 200);
		GL11.glEnd();
		GL11.glColor3f(0.0F, 0.0F, 0.0F);
		this.testFont.renderSegment("This is a test!", 50.0F, 50, 50, true);
		this.testFont.renderSegment(this.testString, 32.0F, 50, 300, true);
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
