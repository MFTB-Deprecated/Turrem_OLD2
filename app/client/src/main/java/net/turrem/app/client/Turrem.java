package net.turrem.app.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import net.turrem.app.EnumSide;
import net.turrem.app.Global;
import net.turrem.app.client.asset.AssetLoader;
import net.turrem.app.client.asset.GameAsset;
import net.turrem.app.client.render.IntroScreen;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.client.render.RenderScreen;
import net.turrem.app.client.render.fbo.DiffuseFBO;
import net.turrem.app.client.utils.graphics.ImgUtils;
import net.turrem.app.mod.ModLoader;

public class Turrem
{
	private static Turrem instance;
	
	private static int width = 640;
	private static int height = 480;
	
	public final Session theSession;
	public final File theGameDir;
	public final AssetLoader theAssetLoader;
	
	public ModLoader modLoader;
	
	public RenderEngine engine;
	
	private RenderScreen screen = null;
	private RenderScreen newScreen = null;
	
	public Turrem(Session session, String dir)
	{
		this.theSession = session;
		this.theGameDir = new File(dir);
		
		new Global(EnumSide.CLIENT, this.theGameDir);
		
		this.theAssetLoader = new AssetLoader(this.theGameDir, this);
		
		instance = this;
	}
	
	public static Turrem instance()
	{
		return instance;
	}
	
	public static int width()
	{
		return width;
	}
	
	public static int height()
	{
		return height;
	}
	
	public static float aspect()
	{
		return (float) width / height;
	}
	
	protected void run()
	{
		this.modLoader = new ModLoader(new File(this.theGameDir, "mods"), EnumSide.CLIENT, this.getClass().getClassLoader());
		
		Keyboard.enableRepeatEvents(false);
		
		try
		{
			this.setIcons();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			Display.setDisplayMode(new DisplayMode(Turrem.width(), Turrem.height()));
			Display.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
		
		this.engine = RenderEngine.instance;
		
		DiffuseFBO target = new DiffuseFBO(Turrem.width, Turrem.height);
		
		this.screen = new IntroScreen(target);
		
		while (!this.closeRequested())
		{
			this.screen.render();
			Display.update();
			Display.sync(60);
			if (this.newScreen != null)
			{
				this.screen.end();
				this.screen = this.newScreen;
				this.newScreen = null;
			}
		}
	}
	
	public boolean closeRequested()
	{
		return Display.isCloseRequested();
	}
	
	public void onCrash(Exception e)
	{
		e.printStackTrace();
		this.shutdown();
	}
	
	public void shutdown()
	{
		Display.destroy();
		System.exit(0);
	}
	
	public void changeScreen(RenderScreen newScreen)
	{
		this.newScreen = newScreen;
	}
	
	public void setIcons() throws IOException
	{
		ArrayList<ByteBuffer> icos = new ArrayList<ByteBuffer>();
		
		GameAsset appicons = GameAsset.fromRaw("icons/");
		
		File[] filelist = appicons.getFile().listFiles();
		
		for (File icon : filelist)
		{
			BufferedImage img = ImageIO.read(icon);
			icos.add(ImgUtils.imageToBufferDefault(img));
		}
		
		Display.setIcon(icos.toArray(new ByteBuffer[0]));
	}
}
