package net.turrem.app.client.render.shader;

import net.turrem.app.utils.stores.BasicStore;

public class ShaderStore extends BasicStore<Shader>
{
	@Override
	public void create(BasicStore<Shader>.StoreAdd add)
	{
		if (add.requester instanceof ShaderIcon)
		{
			ShaderIcon sh = (ShaderIcon) add.requester;
			Shader shader = new Shader(sh.type, sh.shader);
			boolean success = this.displayShaderCreateInfo(sh.toString(), shader.create());
			sh.receive(shader, success);
			if (success)
			{
				add.give(shader);
			}
			else
			{
				add.fail(shader);
			}
		}
		else
		{
			throw new IllegalArgumentException("A ShaderStore is for ShaderIcons only!");
		}
	}
	
	private boolean displayShaderCreateInfo(String shaderName, ShaderCreateInfo info)
	{
		if (info.state == ShaderCreateInfo.Error.NONE)
		{
			System.out.printf("Successfully created shader (%s).%n", shaderName);
			return true;
		}
		else
		{
			System.err.printf("Failed to create shader (%s). Reason: %s%n", shaderName, info.state.name());
			if (info.log != null && !info.log.isEmpty())
			{
				System.err.println(info.log);
			}
			else if (info.exeption != null)
			{
				info.exeption.printStackTrace(System.err);
			}
			return false;
		}
	}
}
