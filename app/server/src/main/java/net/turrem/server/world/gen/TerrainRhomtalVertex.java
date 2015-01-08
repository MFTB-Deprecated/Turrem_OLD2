package net.turrem.server.world.gen;

public class TerrainRhomtalVertex
{
	public float height;
	
	public static TerrainRhomtalVertex midpoint(TerrainRhomtalVertex a, TerrainRhomtalVertex b)
	{
		TerrainRhomtalVertex v = new TerrainRhomtalVertex();
		v.height = (a.height + b.height) / 2;
		return v;
	}
}
