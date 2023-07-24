package me.Josh123likeme.Render3D3p0;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferStrategy;
import java.io.File;

import me.Josh123likeme.Render3D3p0.InputListener.*;
import me.Josh123likeme.Render3D3p0.Parsers.STLParser;
import me.Josh123likeme.Render3D3p0.Render.*;
import me.Josh123likeme.Render3D3p0.World.Model;
import me.Josh123likeme.Render3D3p0.World.World;

public class Game extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	public static final int INITIAL_WIDTH = 400, INITIAL_HEIGHT = 400;
	
	private Thread thread;
	private boolean running = false;
	
	public MouseWitness mouseWitness;
	public KeyboardWitness keyboardWitness;
	
	private double deltaFrame;
	private int fps;
	
	private boolean paused = false;
	
	private double xSens = 0.0014; //radians per pixel
	private double ySens = 0.0014; //radians per pixel
	
	private Renderer renderer;
	
	private Camera camera;
	private World world;

	public Game() {
		
		renderer = new Renderer();
		
		camera = new Camera(0.032/9*16, 0.032, INITIAL_HEIGHT/9*16, INITIAL_HEIGHT, 0.017);
		
		camera.pos.add(new Vector3D(0,0,30));
		
		world = new World(2);
		
		STLParser stlp = new STLParser();
		Model model = stlp.parseSTL(new File("C:\\Users\\Joshua Darley\\Downloads\\bread.stl"));
		world.triangles = model.triangles;
		
		initInputs();
		
		new Window(INITIAL_WIDTH, INITIAL_HEIGHT, "Render 3D 3.0", this);
		
	}
	
	private void initInputs() {
		
		mouseWitness = new MouseWitness();
		keyboardWitness = new KeyboardWitness();
		
		addMouseListener(mouseWitness);
		addMouseMotionListener(mouseWitness);
		addKeyListener(keyboardWitness);
		
		requestFocus();
		
	}
	
	public synchronized void start() {
		
		thread = new Thread(this);
		thread.start();
		running = true;
		
	}
	
	public synchronized void stop() {
		
		try 
		{
			thread.join();
			running = false;
		}
		
		catch(Exception e) {e.printStackTrace();}
		
	}
	
	public void run() {
		
		double targetfps = 10000d;
		long targetDeltaFrame = Math.round((1d / targetfps) * 1000000000);
		long lastSecond = System.nanoTime();
		int frames = 0;
		
		long lastFrame = System.nanoTime();
		
		while (running) {
			
			frames++;
			
			if (lastSecond + 1000000000 < System.nanoTime()) {
				
				fps = frames;
				
				frames = 0;
				
				lastSecond = System.nanoTime();
				
				targetDeltaFrame = Math.round((1d / targetfps) * 1000000000);
				
			}
			
			//starting to push frame
			
			long nextTime = System.nanoTime() + targetDeltaFrame;
			
			deltaFrame = ((double) (System.nanoTime() - lastFrame)) / 1000000000;
			
			lastFrame = System.nanoTime();
			
			preFrame();
			
			paint();
			
			//finished pushing frame
			
			keyboardWitness.purgeTypedKeys();
			mouseWitness.purgeClickedButtons();
			
			while (nextTime > System.nanoTime());
			
		}
		stop();
		
	}
	
	private void preFrame() {
	
		camera.iw = getWidth();
		camera.ih = getHeight();
		
		camera.sw = camera.sh * ((double) camera.iw / camera.ih);
		
		if (keyboardWitness.getTypedKeys().contains(27)) paused = !paused;
		
		if (!paused) {
			
			camera.yaw -= xSens * ((double) mouseWitness.getMouseX() - getWidth() / 2);
			camera.pitch -= ySens * ((double) mouseWitness.getMouseY() - getHeight() / 2);
			
			if (camera.yaw < 0) camera.yaw = 2*Math.PI + camera.yaw;
			if (camera.yaw > 2*Math.PI) camera.yaw %= 2*Math.PI;
			if (camera.pitch < 0) camera.pitch = 0;
			if (camera.pitch > Math.PI) camera.pitch = Math.PI;
			
			lockMouse(getWidth() / 2, getHeight() / 2);
			
		}
		
		Vector3D dir = new Vector3D();
		
		Vector3D forward = new Vector3D(Math.cos(camera.yaw)*Math.sin(camera.pitch),
				Math.sin(camera.yaw)*Math.sin(camera.pitch),
				-Math.cos(camera.pitch));
		
		Vector3D right = new Vector3D(-forward.Z,0,forward.X);
		
		if (keyboardWitness.getHeldKeys().contains(87)) {
			
			dir.add(forward);
			
		}
		else if (keyboardWitness.getHeldKeys().contains(83)) {
			
			dir.subtract(forward);
			
		}
		
		if (keyboardWitness.getHeldKeys().contains(65)) {
			
			dir.subtract(right);
			
		}
		else if (keyboardWitness.getHeldKeys().contains(68)) {
			
			dir.add(right);
			
		}
		
		if (keyboardWitness.getHeldKeys().contains(32)) {
			
			dir.add(new Vector3D(0,0,1));
			
		}
		else if (keyboardWitness.getHeldKeys().contains(17)) {
			
			dir.add(new Vector3D(0,0,-1));
			
		}
		
		boolean sprinting = false;
		
		if (keyboardWitness.getHeldKeys().contains(16)) sprinting = true;

		dir.scale(2*deltaFrame*(sprinting ? 5 : 1));
		
		camera.pos.add(dir);
		
		if (keyboardWitness.getHeldKeys().contains(61)) {
			
			camera.fl *= 1.05;
			
		}
		else if (keyboardWitness.getHeldKeys().contains(45)) {
			
			camera.fl /= 1.05;
			
		}
		
	}

	private void paint() {
	
		BufferStrategy bufferStrategy = this.getBufferStrategy();
		if(bufferStrategy == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics graphics = bufferStrategy.getDrawGraphics();
		
		//basic black background to stop flashing
		graphics.setColor(Color.black); 
		graphics.fillRect(0, 0, getWidth(), getHeight());
		
		//put rendering stuff here

		renderer.render(graphics, world, camera);
		
		graphics.setColor(Color.white);
		
		int h = 10;
		int hs = 11;
		
		graphics.drawString("fps: " + fps + " (" + (deltaFrame * 1000) + "ms)", 0, h); h += hs;
		graphics.drawString("yaw: " + camera.yaw + " pitch: " + camera.pitch, 0, h); h += hs;
		graphics.drawString("cam pos: " + camera.pos.format(), 0, h); h += hs;
		
		h += hs;
		
		String totalCulledPercentage = round(100 - 100 * ((double) renderer.facesDrawn / world.triangles.size()), 4);
		
		graphics.drawString("surfaces drawn: " + renderer.facesDrawn + "/" + world.triangles.size() + " (" + totalCulledPercentage + "% culled )" , 0, h); h += hs;
		graphics.drawString("frustum culling: " + renderer.facesFrustumCulled + " total culled", 0, h); h += hs;
		graphics.drawString("backface culling: " + renderer.facesBackfaceCulled + " total culled", 0, h); h += hs;
		
		h += hs;
		
		graphics.drawString("frustumCullTime: " + ((double) renderer.frustumCullTime / 1000000) + "ms", 0, h); h += hs;
		graphics.drawString("distCalcTime: " + ((double) renderer.distCalcTime / 1000000) + "ms", 0, h); h += hs;
		graphics.drawString("sortTime: " + ((double) renderer.sortTime / 1000000) + "ms", 0, h); h += hs;
		graphics.drawString("drawTime: " + ((double) renderer.drawTime / 1000000) + "ms", 0, h); h += hs;
		
		//this pushes the graphics to the window
		bufferStrategy.show();
		
	}
	
	private String round(double value, int sigFigs) {
		
		double offset = Math.pow(10, sigFigs);
		
		return "" + (double) Math.round(value * offset) / offset;

	}
	
	public double getDeltaFrame() {
		
		return deltaFrame;
		
	}
	
	/**
	 * locks the mouse to the centre of the screen
	 */
	public void lockMouse(int x, int y) {
		
		if (!isFocusOwner()) return;

		int newX = getLocationOnScreen().x + x;
		int newY = getLocationOnScreen().y + y;
		
		Point p = new Point(newX, newY);
		
	    GraphicsEnvironment ge = 
	        GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();

	    // Search the devices for the one that draws the specified point.
	    for (GraphicsDevice device : gs) { 
	    	
	        GraphicsConfiguration[] configurations =
	            device.getConfigurations();
	        
	        for (GraphicsConfiguration config : configurations) {
	            Rectangle bounds = config.getBounds();
	            
	            if(bounds.contains(p)) {
	                // Set point to screen coordinates.
	                Point b = bounds.getLocation(); 
	                Point s = new Point(p.x - b.x, p.y - b.y);

	                try {
	                    Robot r = new Robot(device);
	                    r.mouseMove(s.x, s.y);
	                    
	                } catch (AWTException e) {
	                    e.printStackTrace();
	                }

	                return;
	            }
	            
	        }
	        
	    }
	    // Couldn't move to the point, it may be off screen.
	    return;
	}
	
}
