package net.turrem.server.world.gen;

/**
 * The class stores a grid of triangles for use in terrain generation. The
 * triangles are subdivided repeatedly as in a fractal with hash based random
 * changes being layered on each time. In world space this grid would be a
 * rhombus which combined with its fractal natures gives the class its name,
 * Rhomtal. The system is based on <a
 * href="http://algorithmicbotany.org/papers/mountains.gi93.pdf">"A Fractal
 * Model of Mountains with Rivers"</a>
 */
public class TerrainRhomtal
{
	public final int baseHash;
	public final int posx;
	public final int posz;
	/** Equivelent to Log2(width - 1) */
	public final int logwidth;
	/** The number of verticies on any side of the final rhomtal */
	public final int width;
	
	private final int size;
	
	private int iteration = 0;
	/** The sqrt(number of verts) for the current iteration */
	private int itwidth;
	/** Equivelent to the next iteration's itgap */
	private int itmid;
	/**
	 * In any iteration adding this to the row/column of an existing vertex will
	 * give the row/column of the next existing vertex.
	 */
	private int itgap;
	
	public TerrainRhomtalVertex[] verts;
	public TerrainRhomtalEdge[] edges; //The index for a edge is the same as the edge's midpoint's index in the verts array
	
	public TerrainRhomtal(int posx, int posz, int logwidth, int basehash, TerrainRhomtalUnit initial)
	{
		this.posx = posx;
		this.posz = posz;
		this.logwidth = logwidth;
		this.baseHash = basehash;
		
		this.width = (1 << this.logwidth) + 1; //convert width as 2^x to actual width
		this.itwidth = 2; //the inital verts are a 2x2 grid
		this.itgap = this.width - 1;
		this.itmid = this.itgap / 2;
		this.size = this.width * this.width; //this gets used alot so it is stored
		
		this.verts = new TerrainRhomtalVertex[this.size];
		this.edges = new TerrainRhomtalEdge[this.size];
		
		this.verts[0] = initial.vertTL; //first vertex
		this.verts[this.width - 1] = initial.vertTR; //end of first row
		this.verts[this.size - this.width] = initial.vertBL; //start of last row
		this.verts[this.size - 1] = initial.vertBR; //last vert
		
		this.edges[this.itmid] = initial.edgeTop;
		this.edges[this.itmid * this.width] = initial.edgeLeft;
		this.edges[this.itmid * this.width + this.itmid] = initial.edgeDiagonal;
		this.edges[this.itmid * this.width + this.width - 1] = initial.edgeDiagonal;
		this.edges[this.size - this.width + this.itmid] = initial.edgeBottom;
	}
	
	private void iterate()
	{
		for (int i = 0; i < this.itwidth - 1; i++)
		{
			for (int j = 0; j < this.itwidth - 1; j++)
			{
				int a = i * this.itgap + (j * this.itgap) * this.width;
				int b = a + this.itgap;
				int c = b + this.itgap * this.width;
				int d = c - this.itgap;
				
				int ab = this.iterateEdge(a, b);
				int bc = this.iterateEdge(b, c);
				int ca = this.iterateEdge(c, a);
				int ad = this.iterateEdge(a, d);
				int dc = this.iterateEdge(d, c);
				
				this.iterateTriangle(a, b, c, ab, bc, ca);
				this.iterateTriangle(c, d, a, ca, ad, dc);
			}
		}
		this.iteration++;
		this.itwidth += this.itwidth - 1;
		this.itgap = this.itmid;
		this.itmid = this.itgap / 2;
	}
	
	private int iterateEdge(int start, int end)
	{
		int sx = start % this.width;
		int sy = start / this.width;
		int ex = end % this.width;
		int ey = end / this.width;
		int mx = (sx + ex) / 2;
		int my = (sy + ey) / 2;
		int mid = mx + my * this.width;
		
		this.verts[mid] = TerrainRhomtalVertex.midpoint(this.verts[start], this.verts[end]);
		
		return mid;
	}
	
	private void iterateTriangle(int a, int b, int c, int ab, int bc, int ca)
	{
		
	}
}
