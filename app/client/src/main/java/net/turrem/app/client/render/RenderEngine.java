package net.turrem.app.client.render;

import net.turrem.app.client.asset.Asset;
import net.turrem.app.client.render.fbo.EnumDrawBufferLocs;
import net.turrem.app.client.render.shader.Program;
import net.turrem.app.client.render.shader.ProgramIcon;
import net.turrem.app.client.render.shader.ProgramStore;
import net.turrem.app.client.render.shader.ShaderIcon;
import net.turrem.app.client.render.shader.ShaderStore;
import net.turrem.app.client.render.shader.ShaderType;
import net.turrem.app.client.render.verts.VertexBuffer;
import net.turrem.app.client.utils.graphics.GLUtils;
import net.turrem.app.mod.Mod;

public class RenderEngine
{
	public static final int[] quadIndicies = new int[] { 0, 1, 3, 1, 2, 3 };
	
	public static void buffer2DQuad(VertexBuffer positionBuf, VertexBuffer indexBuf, float x, float y, float z, float w, float h)
	{
		float[] verts = new float[12];
		
		for (int i = 0; i < 12; i += 3)
		{
			verts[i] = x;
			verts[i + 1] = y;
			verts[i + 2] = z;
		}
		verts[1] += h;
		verts[4] += h;
		verts[3] += w;
		verts[6] += w;
		
		positionBuf.vertexData(GLUtils.bufferFloats(verts));
		indexBuf.vertexData(GLUtils.bufferInts(RenderEngine.quadIndicies));
	}
	
	public static final RenderEngine instance = new RenderEngine();
	
	public static final ShaderStore shaders = new ShaderStore();
	public static final ProgramStore programs = new ProgramStore();
	
	public static ShaderIcon blitvicon = new ShaderIcon(new Asset(Mod.APP, "shaders.blit"), ShaderType.VERTEX);
	public static ShaderIcon blitficon = new ShaderIcon(new Asset(Mod.APP, "shaders.blit"), ShaderType.FRAGMENT);
	
	public Program blit;
	
	private RenderEngine()
	{
		
	}
	
	public void create()
	{
		ProgramIcon bliti = new ProgramIcon(RenderEngine.blitvicon, RenderEngine.blitficon, EnumDrawBufferLocs.DIFFUSE);
		bliti.aquire();
		this.blit = bliti.object;
	}
}
