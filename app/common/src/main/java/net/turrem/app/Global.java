package net.turrem.app;

import java.io.File;

public class Global
{
	private static boolean created = false;
	
	private static EnumSide side;
	private static File gameDir;
	
	public Global(EnumSide side, File gameDir)
	{
		if (created)
		{
			throw new IllegalStateException("Global has already been created!");
		}
		created = true;
		Global.side = side;
		Global.gameDir = gameDir;
	}
	
	public static EnumSide side()
	{
		return side;
	}
	
	public static File gameDir()
	{
		return gameDir;
	}
}
