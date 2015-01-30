package net.turrem.app.mod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;

import net.turrem.app.EnumSide;
import net.turrem.app.mod.event.OnLoad;
import net.turrem.app.mod.event.OnPostLoad;
import net.turrem.app.mod.event.OnPreLoad;
import net.turrem.app.mod.event.PreRegister;
import net.turrem.utils.JarExplore;

import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
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
	private boolean loaded = false;
	
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
	
	public ClassLoader loadModClasses(ClassLoader parent)
	{
		ArrayList<URL> jarlist = new ArrayList<URL>();
		for (Mod mod : this.mods.keySet())
		{
			File jar = this.getModJarFile(mod);
			if (jar.exists())
			{
				try
				{
					jarlist.add(jar.toURI().toURL());
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.out.printf("The %s jar for mod %s does not exist.", this.side.name().toLowerCase(), mod.identifier);
			}
		}
		URL[] jars = new URL[jarlist.size()];
		jars = jarlist.toArray(jars);
		return URLClassLoader.newInstance(jars, parent);
	}
	
	public void loadMods()
	{
		ArrayListMultimap<ModInstance, Class<?>> claz = ArrayListMultimap.create();
		for (Mod mod : this.mods.keySet())
		{
			try
			{
				claz.putAll(mod, JarExplore.newInstance(this.getModJar(mod)).getLoadedClasses(this.modClassLoader));
			}
			catch (IOException e)
			{
				System.out.printf("Failed to get class list for [%s].%n", mod.identifier);
			}
		}
		this.onPreVisitLoad(claz);
		this.onLoad(claz);
		this.onPostLoad(claz);
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
	
	protected void onLoad(ArrayListMultimap<ModInstance, Class<?>> map)
	{
		for (ModInstance mod : map.keySet())
		{
			List<Class<?>> claz = map.get(mod);
			for (Class<?> clas : claz)
			{
				for (Method met : clas.getDeclaredMethods())
				{
					if (met.isAnnotationPresent(OnLoad.class))
					{
						String name = met.getName();
						if (!Modifier.isStatic(met.getModifiers()))
						{
							System.out.printf("Method %s has @OnLoad, but is not static.%n", name);
						}
						else if (met.getParameterTypes().length != 0)
						{
							System.out.printf("Method %s has @OnLoad, but requires %d parameters. It should not require any parameters.%n", name, met.getParameterTypes().length);
						}
						else
						{
							try
							{
								met.invoke(null);
							}
							catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
							{
								System.out.printf("Method %s has @OnLoad and is correctly declared, but threw %s when invoked.%n", name, e.getClass().getSimpleName());
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	protected void onPostLoad(ArrayListMultimap<ModInstance, Class<?>> map)
	{
		for (ModInstance mod : map.keySet())
		{
			List<Class<?>> claz = map.get(mod);
			for (Class<?> clas : claz)
			{
				for (Method met : clas.getDeclaredMethods())
				{
					if (met.isAnnotationPresent(OnPostLoad.class))
					{
						String name = met.getName();
						if (!Modifier.isStatic(met.getModifiers()))
						{
							System.out.printf("Method %s has @OnPostLoad, but is not static.%n", name);
						}
						else if (met.getParameterTypes().length != 0)
						{
							System.out.printf("Method %s has @OnPostLoad, but requires %d parameters. It should not require any parameters.%n", name, met.getParameterTypes().length);
						}
						else
						{
							try
							{
								met.invoke(null);
							}
							catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
							{
								System.out.printf("Method %s has @OnPostLoad and is correctly declared, but threw %s when invoked.%n", name, e.getClass().getSimpleName());
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
	protected void onPreVisitLoad(ArrayListMultimap<ModInstance, Class<?>> map)
	{
		for (ModInstance mod : map.keySet())
		{
			List<Class<?>> claz = map.get(mod);
			for (Class<?> clas : claz)
			{
				for (Method met : clas.getDeclaredMethods())
				{
					String name = met.getName();
					if (met.isAnnotationPresent(PreRegister.class))
					{
						/*
						if (!Modifier.isStatic(met.getModifiers()))
						{
							System.out.printf("Method %s has @PreRegister, but is not static.%n", name);
						}
						else if (met.getParameterTypes().length != 1)
						{
							System.out.printf("Method %s has @PreRegister, but requires %d parameters. It should require a single parameter.%n", name, met.getParameterTypes().length);
						}
						else if (!met.getParameterTypes()[0].isAssignableFrom(NotedElementRegistryRegistry.class))
						{
							System.out.printf("Method %s has @PreRegister, but takes a parameter that is not assignable from NotedElementVisitorRegistry.%n", name);
						}
						else
						{
							try
							{
								met.invoke(null, registry);
							}
							catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
							{
								System.out.printf("Method %s has @PreRegister and is correctly declared, but threw %s when invoked.%n", name, e.getClass().getSimpleName());
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
						*/
					}
					if (met.isAnnotationPresent(OnPreLoad.class))
					{
						if (!Modifier.isStatic(met.getModifiers()))
						{
							System.out.printf("Method %s has @OnPreLoad, but is not static.%n", name);
						}
						else if (met.getParameterTypes().length != 0)
						{
							System.out.printf("Method %s has @OnPreLoad, but requires %d parameters. It should not require any parameters.%n", name, met.getParameterTypes().length);
						}
						else
						{
							try
							{
								met.invoke(null);
							}
							catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
							{
								System.out.printf("Method %s has @OnPreLoad and is correctly declared, but but threw %s when invoked.%n", name, e.getClass().getSimpleName());
							}
							catch (Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
	
}
