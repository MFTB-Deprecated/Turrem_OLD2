package net.turrem.utils;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExplore
{
	protected JarFile jar = null;

	private JarExplore(File jarFile) throws IOException
	{
		this.jar = new JarFile(jarFile);
	}

	private JarExplore(JarFile jarFile)
	{
		this.jar = jarFile;
	}

	/**
	 * Gets an array of all classes that are in this jar file without loading them excluding anything in META-INF.
	 * @return The class names
	 */
	public ArrayList<String> getClassNames()
	{
		if (this.jar != null)
		{
			ArrayList<String> found = new ArrayList<String>();

			for (JarEntry je : Collections.list(this.jar.entries()))
			{
				if (!je.isDirectory())
				{
					String name = je.getName();
					if (name.endsWith(".class"))
					{
						String className = getClassName(name);
						if (!className.startsWith("META-INF"))
						{
							found.add(className);
						}
					}
				}
			}

			try
			{
				this.jar.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			return found;
		}
		return null;
	}

	/**
	 * Gets an array of all classes in this jar that are already loaded and accessible to the given class loader.
	 * @param cl The class loader to use
	 * @return The list of loaded classes in this jar
	 */
	public ArrayList<Class<?>> getLoadedClasses(ClassLoader cl)
	{
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		ArrayList<String> classNames = this.getClassNames();
		if (classNames == null)
		{
			return classes;
		}
		for (String clsn : classNames)
		{
			Class<?> clss = null;
			try
			{
				clss = Class.forName(clsn, false, cl);
			}
			catch (ClassNotFoundException e)
			{

			}
			if (clss != null)
			{
				classes.add(clss);
			}
		}
		return classes;
	}

	/**
	 * Converts a relative file path inside a jar file into a class name
	 * @param name The file path
	 * @return The class name and package
	 */
	public static String getClassName(String name)
	{
		String className = name.substring(0, name.lastIndexOf('.'));
		className = className.replace('/', '.');
		return className;
	}

	/**
	 * Create a new instance of JarExplore for the given jar file
	 * @param jarFile The file to parse
	 * @return The instance of JarExplore or null if the file does not exist or is not valid
	 */
	public static JarExplore newInstance(File jarFile)
	{
		if (!jarFile.exists())
		{
			return null;
		}
		try
		{
			return new JarExplore(jarFile);
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * Create a new instance of JarExplore for the given jar file
	 * @param jarFile The file to parse
	 * @return The instance of JarExplore
	 */
	public static JarExplore newInstance(JarFile jarFile)
	{
		return new JarExplore(jarFile);
	}
}
