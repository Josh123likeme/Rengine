package me.Josh123likeme.Render3D3p0.Render;

import me.Josh123likeme.Render3D3p0.Vector3D;

public class Camera {

	public Vector3D pos;
	public double yaw; //anti clockwise about vertical axis (0 is east, tau is east again)
	public double pitch; //0 is straight down, pi is straight up
	
	public double sw;
	public double sh;
	
	public int iw;
	public int ih;
	
	public double fl;
	
	public double range;
	
	public Camera(double sensorWidth, double sensorHeight, int imageWidth, int imageHeight, double focalLength) {
		
		pos = new Vector3D();
		
		sw = sensorWidth;
		sh = sensorHeight;
		
		iw = imageWidth;
		ih = imageHeight;
		
		fl = focalLength;
		
		range = 20;
		
	}
	
}
