package me.Josh123likeme.Render3D3p0.World;

import me.Josh123likeme.Render3D3p0.Main;
import me.Josh123likeme.Render3D3p0.Vector3D;

public class Triangle {

	public Vector3D A;
	public Vector3D B;
	public Vector3D C;
	
	public int c;
	
	public Triangle(Vector3D a, Vector3D b, Vector3D c) {
		
		A = a;
		B = b;
		C = c;
		
		this.c = 0xFF << 24 | Main.random.nextInt();
		
	}
	
}
