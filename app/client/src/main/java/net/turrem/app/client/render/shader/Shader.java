package net.turrem.app.client.render.shader;

import org.lwjgl.opengl.GL20;

public abstract class Shader
{
	public final ShaderType type;
	
	protected Shader(ShaderType type)
	{
		this.type = type;
	}
	
	public static enum ShaderType
	{
		FRAGMENT("fsh", GL20.GL_FRAGMENT_SHADER),
		VERTEX("vsh", GL20.GL_VERTEX_SHADER);
		
		public final String extension;
		public final int glMode;
		
		ShaderType(String extension, int glMode)
		{
			this.extension = extension;
			this.glMode = glMode;
		}
	}
}
