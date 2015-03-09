package net.turrem.app.client.render.shader;

import net.turrem.app.utils.stores.BasicStore;

public class ProgramStore extends BasicStore<Program>
{
	@Override
	public void create(BasicStore<Program>.StoreAdd add)
	{
		if (add.requester instanceof ProgramIcon)
		{
			ProgramIcon icon = (ProgramIcon) add.requester;
			Program program = new Program(icon.locs);
			for (ShaderIcon sh : icon.shaders)
			{
				sh.aquire();
				if (sh.hasReceived())
				{
					sh.object.attach(program);
				}
			}
			boolean success = this.displayProgramLinkInfo(icon.toString(), program.link());
			if (success)
			{
				add.give(program);
			}
			else
			{
				add.fail(program);
			}
		}
		else
		{
			throw new IllegalArgumentException("A ProgramStore is for ProgramIcons only!");
		}
	}
	
	private boolean displayProgramLinkInfo(String programName, ProgramLinkInfo info)
	{
		if (info.state == ProgramLinkInfo.Error.NONE)
		{
			System.out.printf("Successfully linked program (%s).%n", programName);
			return true;
		}
		else
		{
			System.err.printf("Failed to link program (%s). Reason: %s%n", programName, info.state.name());
			if (info.log != null && !info.log.isEmpty())
			{
				System.err.println(info.log);
			}
			return false;
		}
	}
}
