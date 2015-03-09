package net.turrem.app.client.game;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

import org.lwjgl.util.vector.Matrix4f;

import net.turrem.app.client.ClientConfig;
import net.turrem.app.client.Turrem;
import net.turrem.app.client.asset.AssetLoader;
import net.turrem.app.client.asset.GameAsset;
import net.turrem.app.client.font.Font;
import net.turrem.app.client.render.IScreenLayer;
import net.turrem.app.client.render.RenderEngine;
import net.turrem.app.client.render.Transform;
import net.turrem.app.client.render.Transform.MatrixMode;
import net.turrem.app.client.render.fbo.DiffuseFBO;
import net.turrem.app.client.render.fbo.EnumDrawBufferLocs;
import net.turrem.app.client.render.fbo.GeometryFBO;
import net.turrem.app.client.render.fbo.ValueFBO;
import net.turrem.app.client.render.lights.Light;
import net.turrem.app.client.render.lights.PointLight;
import net.turrem.app.client.render.shader.Program;
import net.turrem.app.client.render.shader.ProgramIcon;
import net.turrem.app.client.render.shader.ShaderIcon;
import net.turrem.app.client.render.shader.ShaderType;
import net.turrem.app.client.render.shader.ShaderUniform;
import net.turrem.app.client.render.shading.ShadeGeometry;
import net.turrem.app.client.render.shading.ShadeSSAO;
import net.turrem.app.mod.Mod;

public class RenderGame implements IScreenLayer
{
	private class UpdateGeometryMat implements Runnable
	{
		public Transform transform;
		
		public UpdateGeometryMat(Transform transform)
		{
			this.transform = transform;
		}
		
		@Override
		public void run()
		{
			RenderGame.this.geoPass.uploadMV(this.transform.getResult(0b011));
			RenderGame.this.geoPass.uploadMVP(this.transform.getResult(0b111));
		}
	}
	
	public Timer timer = null;
	public float partial;
	public float time;
	private float lastPartial;
	public Font font;
	
	public ClientGame game;
	
	public final Transform cameraTransform;
	public final Transform lightTransform;
	
	public CameraView view;
	
	public GeometryFBO geometry;
	public ValueFBO valSwap;
	public ValueFBO occlusion;
	public DiffuseFBO light;
	
	public ShadeGeometry geoPass;
	public ShadeSSAO ssaoPass;
	
	public ProgramIcon vignetteShader = new ProgramIcon(RenderEngine.blitvicon, new ShaderIcon(Mod.APP, "shaders.vignette", ShaderType.FRAGMENT), EnumDrawBufferLocs.DIFFUSE);
	public ProgramIcon lightShader = new ProgramIcon(RenderEngine.blitvicon, new ShaderIcon(Mod.APP, "shaders.light", ShaderType.FRAGMENT), EnumDrawBufferLocs.DIFFUSE);
	public ProgramIcon lumblitShader = new ProgramIcon(RenderEngine.blitvicon, new ShaderIcon(Mod.APP, "shaders.lumblit", ShaderType.FRAGMENT), EnumDrawBufferLocs.DIFFUSE);
	
	public RenderGame(ClientGame game)
	{
		this.game = game;
		try
		{
			this.font = Font.loadFont(new GameAsset(Mod.APP, "fonts/Arial.fnt"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		this.font.load(AssetLoader.instance());
		
		this.cameraTransform = new Transform();
		this.lightTransform = new Transform();
		this.view = new CameraView();
		
		this.geometry = new GeometryFBO(Turrem.width(), Turrem.height());
		this.geometry.create();
		this.valSwap = new ValueFBO(Turrem.width(), Turrem.height());
		this.valSwap.create();
		this.occlusion = new ValueFBO(Turrem.width(), Turrem.height());
		this.occlusion.create();
		this.light = new DiffuseFBO(Turrem.width(), Turrem.height());
		this.light.create();
		
		this.geoPass = new ShadeGeometry();
		this.ssaoPass = new ShadeSSAO(4, ClientConfig.ssaoQuality, new Random());
		
		this.vignetteShader.aquire();
		this.lightShader.aquire();
	}
	
	@Override
	public void render(RenderEngine engine, DiffuseFBO target)
	{
		if (this.timer == null)
		{
			this.timer = new Timer();
			this.timer.resetTimer();
			this.timer.partialTicks += 1;
		}
		this.lastPartial = this.partial;
		this.partial = this.timer.partialTicks;
		
		float pdif = 0.0F;
		for (int i = 0; i < this.timer.elapsedTicks; i++)
		{
			this.tickGame();
			pdif += 1;
		}
		
		pdif += this.partial - this.lastPartial;
		float dtime = pdif / this.timer.ticksPerSecond;
		
		this.time += dtime;
		
		this.renderGame();
		
		target.bind();
		ShaderUniform un = new ShaderUniform();
		RenderEngine.instance.blit.bind();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.geometry.getDiffuse());
		un.setInt(0);
		un.upload(RenderEngine.instance.blit, "samp_diffuse");
		
		GL11.glEnable(GL11.GL_BLEND);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE, GL11.GL_ZERO);
		
		this.geometry.drawQuad();
		
		GL14.glBlendColor(0.1F, 0.1F, 0.1F, 1.0F);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		GL14.glBlendFuncSeparate(GL11.GL_CONSTANT_COLOR, GL11.GL_SRC_COLOR, GL11.GL_ZERO, GL11.GL_ONE);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.light.getDiffuse());
		
		this.light.drawQuad();
		
		RenderEngine.instance.blit.unbind();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
		
		Program vignette = this.vignetteShader.object;
		if (vignette != null)
		{
			vignette.bind();
			un.setFloat(1.4F);
			un.upload(vignette, "un_outside");
			un.setFloat(0.6F);
			un.upload(vignette, "un_inside");
			un.setFloats(0.0F, 0.0F, 0.0F, 1.0F);
			un.upload(vignette, "un_color");
			
			target.drawQuad();
			
			vignette.unbind();
		}
		GL11.glDisable(GL11.GL_BLEND);
		
		this.timer.update();
	}
	
	public void tickGame()
	{
		
	}
	
	public void renderGame()
	{
		GL11.glDisable(GL11.GL_BLEND);
		ShaderUniform un = new ShaderUniform();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDepthRange(0.0f, 1.0f);
		
		Matrix4f project = this.cameraTransform.get(MatrixMode.PROJECT);
		Transform.createPerspective(project, 60.0F, Turrem.aspect(), 1.0F, 100.0F);
		Matrix4f model = this.cameraTransform.get(MatrixMode.MODEL);
		model.setIdentity();
		
		this.geometry.bind();
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClearDepth(1.0F);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		
		this.geoPass.start(this.geometry);
		this.renderGameGeo(this.cameraTransform, new UpdateGeometryMat(this.cameraTransform));
		this.geoPass.end(this.geometry);
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		this.occlusion.bind();
		GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		
		this.ssaoPass.renderOcclusion(Turrem.width(), Turrem.height(), this.geometry, this.valSwap, this.occlusion, 5.0F, project);
		
		Program lumblit = this.lumblitShader.object;
		this.light.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		lumblit.bind();
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.occlusion.getValue());
		un.setInt(0);
		un.upload(lumblit, "samp_value");
		un.setVector(this.game.theWorld.ambientLightColor.scale(this.game.theWorld.ambientLightLevel));
		un.upload(lumblit, "un_base");
		
		this.occlusion.drawQuad();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		
		Program lightSh = this.lightShader.object;
		
		lightSh.bind();
		PointLight.uploadCommonUniforms(this.cameraTransform, lightSh);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.geometry.getNormal());
		un.setInt(0);
		un.upload(lightSh, "samp_norm");
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.geometry.getDepth());
		un.setInt(1);
		un.upload(lightSh, "samp_depth");
		for (Light light : this.game.theWorld.lights)
		{
			if (light instanceof PointLight)
			{
				((PointLight) light).uploadUniforms(lightSh);
				this.light.drawQuad();
			}
		}
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void renderGameGeo(Transform transform, Runnable onMatrixChange)
	{
		onMatrixChange.run();
		Matrix4f model = transform.get(MatrixMode.MODEL);
		this.game.drawGameGeometry(model, onMatrixChange);
	}
	
	@Override
	public boolean isOutdated()
	{
		return true;
	}
	
	@Override
	public boolean hasContent()
	{
		return true;
	}
	
	@Override
	public boolean pauseRequested()
	{
		return false;
	}
	
}
