package me.Josh123likeme.Render3D3p0.InputListener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseWitness implements MouseListener, MouseMotionListener {

	private int mouseX, mouseY;
	private boolean dragging;
	
	private boolean leftHeld;
	private boolean pendingLeftClicked;
	private boolean leftClicked;
	
	private boolean rightHeld;
	private boolean pendingRightClicked;
	private boolean rightClicked;
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		dragging = true;
		
		mouseX = e.getX();
		mouseY = e.getY();
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		dragging = false;
		
		mouseX = e.getX();
		mouseY = e.getY();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
			
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;	
			
		if (e.getButton() == MouseEvent.BUTTON1) pendingLeftClicked = true;
		else if (e.getButton() == MouseEvent.BUTTON3) pendingRightClicked = true;
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	public int getMouseX() {
		
		return mouseX;
		
	}
	
	public int getMouseY() {
		
		return mouseY;
		
	}
	
	public boolean isDragging() {
		
		return dragging;
		
	}
	
	public void purgeClickedButtons() {
		
		leftClicked = pendingLeftClicked;
		pendingLeftClicked = false;
		
		rightClicked = pendingRightClicked;
		pendingRightClicked = false;
		
	}
	
	public boolean isLeftHeld() {
		
		return leftHeld;
		
	}
	
	public boolean isLeftClicked() {
		
		return leftClicked;
		
	}
	
	public boolean isRightHeld() {
		
		return rightHeld;
		
	}
	
	public boolean isRightClicked() {
		
		return rightClicked;
		
	}
	
}
