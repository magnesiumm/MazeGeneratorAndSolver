package com.magnesiumm.logic;

import com.magnesiumm.Run;
import com.magnesiumm.GUI.GUI;
import com.magnesiumm.GUI.MazeJPanel;
import com.magnesiumm.configurationData.actualData.GenerationAlgorithm;
import com.magnesiumm.configurationData.actualData.Mode;
import com.magnesiumm.configurationData.actualData.SolveAlgorithm;
import java.awt.Dimension;
import java.io.IOException;


/**
* This class is responsible for using all the other classes
* in order to solve a maze.
* This class is also instantiated as a Thread.
* This is required is required because the java GUI Event Dispatch Thread needs to be free
* in order to handle scheduled repaint events.
*
* @author  AJ Radik and Victoria Vigorito
* @version 6.0 
*/
public class LogicThread extends Thread{
	
	public static final String solvedMazeExtension = "_SOLVED.txt";
	
	/**
	 * This method is inherited from Thread.
	 * It is called when a new thread of Main
	 * is started by the Control Panel. 
	 * Multithreading is required because the java 
	 * GUI Event Dispatch Thread needs to be free
	 * in order to handle scheduled repaint events.
	 */
	@Override
	public void run() {
		try {
			Mode mode = Run.getGUI().getMode();
			String currentMazeFilePath = Run.getGUI().getCurrentMazeFilePath();
			GenerationAlgorithm[] generationAlgorithms = GenerationAlgorithm.values();
			SolveAlgorithm[] solveAlgorithms = SolveAlgorithm.values();
			while(true) { 
				switch(mode) {
				
				//must have breaks or else code will "fall through" - after first case is found all other cases will execute - check https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
				
					case Demo_Mode:
						GenerationAlgorithm randomGenerationAlgorithm = generationAlgorithms[(int) (Math.random() * generationAlgorithms.length)];
						SolveAlgorithm randomSolveAlgorithm = solveAlgorithms[(int) (Math.random() * solveAlgorithms.length)];
						startMazeSolving(randomGenerationAlgorithm, randomSolveAlgorithm, currentMazeFilePath);
						break;
					
					case Custom_Mode:
						startMazeSolving(Run.getGUI().getGenerationAlgorithm(), Run.getGUI().getSolveAlgorithm(), currentMazeFilePath);
						break;
						
				default:
					break;
				}
				//why wasn't delay working here before // ^
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method begins the logic for the program, within this thread.
	 * @param generationAlgorithm The generation algorithm to generate a maze. Will be ignored if custom maze is imported.
	 * @param solveAlgorithm The solve algorithm to be used.
	 * @param mazeFilePath The file path of the maze to be solved. Can be either a generated maze or a custom one.
	 * @throws IOException Problems importing maze files.
	 * @throws InterruptedException Thread issues.
	 */
	public void startMazeSolving(GenerationAlgorithm generationAlgorithm, SolveAlgorithm solveAlgorithm, String mazeFilePath) throws IOException, InterruptedException {
		Dimension dimension = getDimension();
		Maze maze;
		MazeSolver mazeSolver;
		
		switch(mazeFilePath) {
			
			case GUI.generatedMazeFilePath:
				MazeGenerator mazeGenerator = new MazeGenerator(Run.getGUI(), GUI.generatedMazeFilePath, (int) dimension.getHeight(), (int) dimension.getWidth());
				switch(generationAlgorithm) {
					
					case DFS_random:
						mazeGenerator.DFSgenerate();
						break;
					
					default:
						break;
				
				}
				maze = new Maze(GUI.generatedMazeFilePath);
				Thread.sleep(Run.getGUI().getGenerateSolveDelay());
				break;
				
			default:
				maze = new Maze(mazeFilePath);
				if(mazeFilePath.substring(mazeFilePath.length() - solvedMazeExtension.length()).equals(solvedMazeExtension)) {
					return;
				}
				break;
		
		}	
		
		mazeSolver = new MazeSolver(Run.getGUI(), maze);
		
		switch(solveAlgorithm) {
			
			case DFS:
				mazeSolver.DFS();
				break;
			
			case BFS:
				mazeSolver.BFS();
				break;
			
			default:
				break;
		}	
		Run.getFileOperations().writeNodes(mazeFilePath + solvedMazeExtension, maze.getNodes());
		Thread.sleep(Run.getGUI().getGenerateSolveDelay());
	}
	
	/**
	 * This method is used to decide how many rows and columns to generate a maze with. 
	 * This exists because it ensures that generated mazes will always scale according to the current GUI JPanel
	 * size, as to ensure that all nodes look as close to squares on the screen as possible. This method accounts
	 * for the user set maze size multiplier slider.
	 * @return The dimension that the generated maze should have.
	 */
	public Dimension getDimension() {
		double guiWidth = Run.getGUI().getMazeJPanel().getWidth() - 2*MazeJPanel.buffer;
		double guiHeight = Run.getGUI().getMazeJPanel().getHeight()- 2*MazeJPanel.buffer;
		double guiWidthToGuiHeightRatio = guiWidth / guiHeight;
		
		int rowsAspectRatio = 10;
		int colsAspectRatio = (int) Math.round(rowsAspectRatio * guiWidthToGuiHeightRatio);
		int rows = Run.getGUI().getMazeSizeMultiplier() * rowsAspectRatio + 1;
		int cols = Run.getGUI().getMazeSizeMultiplier() * colsAspectRatio + 1;
		//must be odd
		if(cols % 2 == 0) {
			cols--;
		}
		return new Dimension(cols, rows);
	}

}
