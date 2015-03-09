package net.turrem.app.client.render.shading;

import org.lwjgl.util.vector.Matrix4f;

import net.turrem.app.client.asset.Asset;
import net.turrem.app.client.render.fbo.EnumDrawBufferLocs;
import net.turrem.app.client.render.fbo.GeometryFBO;
import net.turrem.app.client.render.shader.Program;
import net.turrem.app.client.render.shader.ProgramIcon;
import net.turrem.app.client.render.shader.ShaderUniform;
import net.turrem.app.mod.Mod;

public class ShadeGeometry
{
	public static ProgramIcon geoIcon = new ProgramIcon(new Asset(Mod.APP, "shaders.geo"), EnumDrawBufferLocs.DIFFUSE, EnumDrawBufferLocs.NORMAL);
	
	public Program geo;
	public ShaderUniform mvMatrix;
	public ShaderUniform mvpMatrix;
	
	public ShadeGeometry()
	{
		this.mvMatrix = new ShaderUniform("un_mv");
		this.mvpMatrix = new ShaderUniform("un_mvp");
	}
	
	public void create()
	{
		ShadeGeometry.geoIcon.aquire();
		this.geo = ShadeGeometry.geoIcon.object;
	}
	
	public void start(GeometryFBO fbo)
	{
		this.geo.bind();
		fbo.bind();
	}
	
	public void uploadMV(Matrix4f mat)
	{
		this.mvMatrix.setMatrix(mat);
		this.uploadMV();
	}
	
	public void uploadMV()
	{
		this.mvMatrix.upload(this.geo);
	}
	
	public void uploadMVP(Matrix4f mat)
	{
		this.mvpMatrix.setMatrix(mat);
		this.uploadMVP();
	}
	
	public void uploadMVP()
	{
		this.mvpMatrix.upload(this.geo);
	}
	
	public void end(GeometryFBO fbo)
	{
		this.geo.unbind();
		fbo.unbind();
	}
}
