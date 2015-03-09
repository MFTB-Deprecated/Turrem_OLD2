package net.turrem.app.client.render.shader;

public class ProgramLinkInfo
{
	public static enum Error
	{
		NONE,
		PROGRAM_NOT_CREATED,
		LINK_ERROR;
	}
	
	public ProgramLinkInfo(Error state, String log)
	{
		this.state = state;
		this.log = log;
	}
	
	public final Error state;
	public final String log;
}