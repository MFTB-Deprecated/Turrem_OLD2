package net.turrem.app.mod;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.jar.JarFile;

import net.turrem.app.EnumSide;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class ModLoader
{
	private static final JsonParser parser = new JsonParser();
	
	private static ModLoader instance;
	
	private HashMap<Mod, ModInstance> mods = new HashMap<Mod, ModInstance>();
	private final EnumSide side;
	private final File modsDir;
	
	public final ClassLoader theGameLoader;
	
	public ModLoader(File modsDir, EnumSide side, ClassLoader gameLoader)
	{
		this.theGameLoader = gameLoader;
		this.modsDir = modsDir;
		this.side = side;
		instance = this;
	}
	
	public static ModLoader instance()
	{
		return instance;
	}
	
	public ModInstance getMod(Mod mod)
	{
		return this.mods.get(mod);
	}
	
	public ClassLoader getClassLoader(Mod mod)
	{
		if (Mod.APP.equals(mod))
		{
			return this.theGameLoader;
		}
		ModInstance modi = this.getMod(mod);
		if (modi == null)
		{
			return null;
		}
		return modi.getClassLoader();
	}
	
	public void buildModList()
	{
		if (!this.modsDir.exists())
		{
			this.modsDir.mkdir();
			return;
		}
		for (File modDir : this.modsDir.listFiles())
		{
			try
			{
				ModInstance mod = this.buildMod(modDir);
				this.mods.put(mod.mod, mod);
			}
			catch (IOException e)
			{
				
			}
		}
	}
	
	private ModInstance buildMod(File modDir) throws IOException, JsonParseException
	{
		Mod mod = new Mod(modDir.getName(), null);
		ModInstance modi = new ModInstance();
		for (File file : modDir.listFiles())
		{
			String name = file.getName();
			if (name.equals("mod.info") && file.isFile())
			{
				try
				{
					mod = this.parseModInfo(mod, modi, file);
					modi.hasInfo = true;
				}
				catch (JsonParseException e)
				{
					System.out.printf("The mod.info file for %s was not valid JSON.%n", mod.identifier);
				}
				catch (IllegalStateException | ClassCastException e)
				{
					System.out.printf("The mod.info file for %s was valid JSON but was not a valid mod descriptor.%n", mod.identifier);
				}
			}
			else if (name.equals("assets") && file.isDirectory())
			{
				modi.hasAssets = true;
			}
			else if (name.equals(EnumSide.CLIENT.id + ".jar") && file.isFile())
			{
				modi.hasJar[0] = true;
			}
			else if (name.equals(EnumSide.SERVER.id + ".jar") && file.isFile())
			{
				modi.hasJar[1] = true;
			}
		}
		modi.mod = mod;
		return modi;
	}
	
	private Mod parseModInfo(Mod mod, ModInstance modi, File info) throws IOException, JsonParseException, IllegalStateException, ClassCastException
	{
		JsonElement rootel = parser.parse(Files.toString(info, Charsets.UTF_8));
		JsonObject root = rootel.getAsJsonObject();
		JsonElement version = root.get("version");
		if (version != null)
		{
			mod = new Mod(mod.identifier, version.getAsString());
		}
		JsonElement name = root.get("name");
		if (name != null)
		{
			modi.name = name.getAsString();
		}
		else
		{
			modi.name = mod.identifier;
		}
		return mod;
	}
	
	protected JarFile getModJar(String id) throws IOException
	{
		return new JarFile(this.getModJarFile(id));
	}
	
	protected File getModJarFile(String id)
	{
		String jar = "/";
		switch (this.side)
		{
			case SERVER:
				jar += "server";
				break;
			case CLIENT:
				jar += "client";
				break;
		}
		jar += ".jar";
		return new File(this.modsDir, id + jar);
	}
	
	protected JarFile getModJar(Mod mod) throws IOException
	{
		return this.getModJar(mod.identifier);
	}
	
	protected File getModJarFile(Mod mod)
	{
		return this.getModJarFile(mod.identifier);
	}
}
