package net.turrem.app.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import net.turrem.app.EnumSide;
import net.turrem.app.Global;
import net.turrem.app.client.asset.AssetLoader;
import net.turrem.app.mod.ModLoader;
import net.turrem.app.utils.graphics.ImgUtils;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class Turrem
{
	private static Turrem instance;
	
	private static int width;
	private static int height;
	
	public final Session theSession;
	public final File theGameDir;
	public final AssetLoader theAssetLoader;
	
	public ModLoader modLoader;
	
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
			Display.create();
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
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
	
	public void setIcons() throws IOException
	{
		ArrayList<ByteBuffer> icos = new ArrayList<ByteBuffer>();
		
		File folder = new File(this.theGameDir, "client/resources/appicons");
		
		File[] filelist = folder.listFiles();
		
		for (File icon : filelist)
		{
			BufferedImage img = ImageIO.read(icon);
			icos.add(ImgUtils.imageToBufferDefault(img));
		}
		
		Display.setIcon(icos.toArray(new ByteBuffer[0]));
	}
}
