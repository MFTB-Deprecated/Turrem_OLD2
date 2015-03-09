package net.turrem.app.client.render.shader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.turrem.app.client.asset.Asset;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Shader
{
	public final ShaderType type;
	public final Asset asset;
	private boolean created = false;
	private boolean isMarkedForDelete = false;
	private int shader = -1;
	
	public Shader(ShaderType type, Asset asset)
	{
		this.type = type;
		this.asset = asset;
	}
	
	public String loadSource() throws IOException
	{
		InputStream in = this.asset.getAsset(this.type.extension).getInput();
		if (in == null)
		{
			throw new FileNotFoundException("Could not find shader source: " + this.asset);
		}
		return IOUtils.toString(in);
	}
	
	public ShaderCreateInfo create()
	{
		this.updateDeleteStatus();
		if (this.created)
		{
			return new ShaderCreateInfo(ShaderCreateInfo.Error.ALREADY_CREATED, null, null);
		}
		String source;
		try
		{
			source = this.loadSource();
		}
		catch (FileNotFoundException e)
		{
			return new ShaderCreateInfo(ShaderCreateInfo.Error.SOURCE_MISSING, null, e);
		}
		catch (IOException e)
		{
			return new ShaderCreateInfo(ShaderCreateInfo.Error.IO_EXCEPTION, null, e);
		}
		this.shader = GL20.glCreateShader(this.type.glMode);
		this.created = true;
		GL20.glShaderSource(this.shader, source);
		GL20.glCompileShader(this.shader);
		if (GL20.glGetShaderi(this.shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			int loglength = GL20.glGetShaderi(this.shader, GL20.GL_INFO_LOG_LENGTH);
			String log = GL20.glGetShaderInfoLog(this.shader, loglength);
			this.delete();
			return new ShaderCreateInfo(ShaderCreateInfo.Error.COMPILE_ERROR, log, null);
		}
		return new ShaderCreateInfo(ShaderCreateInfo.Error.NONE, null, null);
	}
	
	public boolean attach(Program program)
	{
		this.updateDeleteStatus();
		if (!this.created)
		{
			return false;
		}
		int pid = program.getProgram();
		if (pid <= 0)
		{
			return false;
		}
		GL20.glAttachShader(pid, this.shader);
		return true;
	}
	
	public void detach(Program program)
	{
		this.updateDeleteStatus();
		if (!this.created)
		{
			return;
		}
		int pid = program.getProgram();
		if (pid <= 0)
		{
			return;
		}
		GL20.glDetachShader(pid, this.shader);
	}
	
	public void delete()
	{
		if (this.created)
		{
			this.isMarkedForDelete = true;
			GL20.glDeleteShader(this.shader);
			this.updateDeleteStatus();
		}
	}
	
	private void updateDeleteStatus()
	{
		if (this.isMarkedForDelete)
		{
			if (!GL20.glIsShader(this.shader))
			{
				this.isMarkedForDelete = false;
				this.shader = -1;
				this.created = false;
			}
		}
	}
	
	public int getShader()
	{
		this.updateDeleteStatus();
		if (this.created)
		{
			return this.shader;
		}
		return -1;
	}
	
	@Override
	public String toString()
	{
		return this.type.name() + " " + this.asset;
	}
}
