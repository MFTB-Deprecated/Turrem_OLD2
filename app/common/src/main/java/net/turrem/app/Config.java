package net.turrem.app;

public class Config
{
	public static final int chunkBitSize = 4;
	public static final int chunkSize = 1 << chunkBitSize;
	public static final int chunkStorageWidth = 16;
	public static final int turremServerPort = 26555;
	public static final String turremServerHost = "localhost";
	public static final int connectionReadSleep = 2;
	public static final int connectionWriteSleep = 2;
	public static final int connectionTimeoutLimit = 120;
	public static final int connectionInQueueOverflow = 100000;
	public static final int connectionOutQueueOverflow = 100000;
}
