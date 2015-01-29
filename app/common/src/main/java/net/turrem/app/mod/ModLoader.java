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

import com.google.common.collect.ArrayListMultimap;

public class ModLoader
{
	private static ModLoader instance;
	private HashMap<Mod, ModInstance> mods = new HashMap<Mod, ModInstance>();
	private final EnumSide side;
	private final File modsDir;
	private boolean loaded = false;
	
	public ModLoader(File modsDir, EnumSide side)
	{
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
	
	public void buildModList()
	{
		if (!this.modsDir.exists())
		{
			this.modsDir.mkdir();
			return;
		}
		for (File modDir : this.modsDir.listFiles())
		{
			Mod mod = new Mod(modDir.getName(), null);
			ModInstance modi = new ModInstance();
			boolean hasInfo = false;
			boolean hasAssets = false;
			boolean hasJar = false;
			for (File file : modDir.listFiles())
			{
				String name = file.getName();
				if (name.equals("mod.info") && file.isFile())
				{
					hasInfo = true;
				}
				else if (name.equals("assets") && file.isDirectory())
				{
					hasAssets = true;
				}
				else if (name.equals(this.side.id + ".jar"))
				{
					hasJar = true;
				}
			}
			if (hasJar)
			{
				
			}
			else
			{
				
			}
		}
	}
	
	public void loadModClasses(ClassLoader parent)
	{
		ArrayList<URL> jarlist = new ArrayList<URL>();
		for (ModInstance mod : this.mods.values())
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
		this.modClassLoader = URLClassLoader.newInstance(jars, parent);
	}
	
	public void loadMods()
	{
		ArrayListMultimap<ModInstance, Class<?>> claz = ArrayListMultimap.create();
		for (ModInstance mod : this.mods.values())
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
		return new File(this.modDirectory, id + jar);
	}
	
	protected JarFile getModJar(ModInstance mod) throws IOException
	{
		return this.getModJar(mod.identifier);
	}
	
	protected File getModJarFile(ModInstance mod)
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
