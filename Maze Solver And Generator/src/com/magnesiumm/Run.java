package com.magnesiumm;

import com.magnesiumm.GUI.GUI;

public class Run {
	
	private static GUI gui;
	
	public static void main(String[] args) {
		gui = new GUI();
	}
	
	public static GUI getGUI() {
		return gui;
	}
	
}