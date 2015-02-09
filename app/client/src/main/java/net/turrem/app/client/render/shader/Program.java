package net.turrem.app.client.render.shader;

public class Program
{
	private int program = -1;
	private boolean created = false;
	
	public int getProgram()
	{
		if (this.created)
		{
			return this.program;
		}
		return -1;
	}
}
