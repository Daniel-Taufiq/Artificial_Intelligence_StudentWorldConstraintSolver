package studentworld;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.BoolVar;

import gridgames.player.Player;
import studentworld.data.StudentWorldPercept;
import studentworld.grid.StudentWorldCell;
import studentworld.player.StudentWorldPlayer;

public class StudentWorldConstraintSolver {
	private Model model;
	private BoolVar[][] smellyCells;
	private BoolVar[][] glowyCells;
	private BoolVar[][] studentCells;
	private BoolVar[][] doorCells;
	private Solver solver;
	private StudentWorldPlayer player;
	
	public StudentWorldConstraintSolver(StudentWorldPlayer player) {
		model = new Model("studentWorld");
		smellyCells = new BoolVar[5][5];
		glowyCells = new BoolVar[5][5];
		studentCells = new BoolVar[5][5];
		doorCells = new BoolVar[5][5];
		solver = model.getSolver();
		this.player = player;
		
		initializeVariables();
		createConstraints();
	}
	
	public String getBoardState() {
		updateCurrentCellVariables();
		try {
			solver.propagate();
		} catch (ContradictionException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		for(int row=0; row<5; row++) {
			for(int col=0; col<5; col++) {
				sb.append(studentCells[row][col] + "\n");
				sb.append(doorCells[row][col] + "\n");
			}
		}
		return sb.toString();
	}
	
	private void initializeVariables() {
		for(int row=0; row<5; row++) {
			for(int col=0; col<5; col++) {
				smellyCells[row][col] = model.boolVar("smelly ("+row+","+col+")");
				glowyCells[row][col] = model.boolVar("glowy ("+row+","+col+")");
				studentCells[row][col] = model.boolVar("student ("+row+","+col+")");
				doorCells[row][col] = model.boolVar("door ("+row+","+col+")");
			}
		}
	}
	
	private void createConstraints() {			

		for(int row=0; row<5; row++) {
			for(int col=0; col<5; col++) {
				// get potential student neighbors for every cell
				BoolVar[] studentNeighbors = studentArray(row, col);
				BoolVar[] doorNeighbors = doorArray(row, col);
	
				// if the cell is smelly, then at least one OR more of the neighboring cells for a student is true
				model.ifThen(smellyCells[row][col], model.sum(studentNeighbors, ">=", 1));
				// if cell is not smelly, make the student neighbors false
				model.ifThen(model.arithm(smellyCells[row][col], "=", 0), model.sum(studentNeighbors, "=", 0));
				
				// if the cell is glowy, then only one of the neighboring cells is a door
				model.ifThen(glowyCells[row][col], model.sum(doorNeighbors, "=", 1));
				// if cell is not glowy, make the door neighbors false
				model.ifThen(model.arithm(glowyCells[row][col], "=", 0), model.sum(doorNeighbors, "=", 0));
				//model.sum(doorNeighbors, "=", 0).post();
				
				BoolVar[] nonAdjacentDoors;
				ArrayList<BoolVar> nonAdjacentDoorsList = new ArrayList<BoolVar>();
				for (int remainingRow = 0; remainingRow < 4; remainingRow++) {
					for (int remainingCol = 0; remainingCol < 4; remainingCol++) {

						// we don't want to include current cell
						if (remainingRow == row && remainingCol == col) {

						} 
						// we don't want to include above neighbor
						else if (remainingRow == row - 1) {

						} 
						// we don't want to include right neighbor
						else if ( remainingRow == row && remainingCol == col + 1) {

						} 
						// we don't want to include below neighbor
						else if (remainingRow == row + 1) {

						} 
						// we don't want to include left neighbor
						else if (remainingRow == row && remainingCol == col - 1) {

						} 
						// add everything outside adjacent cells to array
						else {
							nonAdjacentDoorsList.add(doorCells[remainingRow][remainingCol]);
						}
					}
				}
				nonAdjacentDoors = nonAdjacentDoorsList.toArray(new BoolVar[nonAdjacentDoorsList.size()]);
				// since we found a glowy cell, we can infer that remaining non-adjacent cells are not doors
				model.ifThen(glowyCells[row][col], model.sum(nonAdjacentDoors, "=", 0));
			}
		}		
	}

	private BoolVar[] glowArray(int row, int col)
	{
		BoolVar[] glowyNeighbors;	// when current cell is a smelly, fill array with studentNeighbors
		ArrayList<BoolVar> glowyNeighborsList = new ArrayList<BoolVar>();
		
		// if neighbor above, add to ArrayList
		if (row > 0) {
			glowyNeighborsList.add(glowyCells[row - 1][col]);
		}
		// if neighbor right, add to ArrayList
		if (col < 4) {
			glowyNeighborsList.add(glowyCells[row][col + 1]);
		}
		// if neighbor below, add to ArrayList
		if (row < 4) {
			glowyNeighborsList.add(glowyCells[row + 1][col]);
		}
		// if neighbor left, add to ArrayList
		if (col > 0) {
			glowyNeighborsList.add(glowyCells[row][col - 1]);
		}
		// convert ArrayList neighbors to array
		glowyNeighbors = glowyNeighborsList.toArray(new BoolVar[glowyNeighborsList.size()]);
		// return studentNeighbors
		return glowyNeighbors;
	}
		
	// if current cell is smelly: get student neighbors
	private BoolVar[] studentArray(int row, int col)
	{
		BoolVar[] studentNeighbors;	// when current cell is a smelly, fill array with studentNeighbors
		ArrayList<BoolVar> studentNeighborsList = new ArrayList<BoolVar>();
		
		// if neighbor above, add to ArrayList
		if (row > 0) {
			studentNeighborsList.add(studentCells[row - 1][col]);
		}
		// if neighbor right, add to ArrayList
		if (col < 4) {
			studentNeighborsList.add(studentCells[row][col + 1]);
		}
		// if neighbor below, add to ArrayList
		if (row < 4) {
			studentNeighborsList.add(studentCells[row + 1][col]);
		}
		// if neighbor left, add to ArrayList
		if (col > 0) {
			studentNeighborsList.add(studentCells[row][col - 1]);
		}
		// convert ArrayList neighbors to array
		studentNeighbors = studentNeighborsList.toArray(new BoolVar[studentNeighborsList.size()]);
		// return studentNeighbors
		return studentNeighbors;
	}
	
	// if current cell is glowy: get door neighbors
	private BoolVar[] doorArray(int row, int col) 
	{
		
		BoolVar[] doorNeighbors;	// when current cell is a glowly, fill neighbors with door
		ArrayList<BoolVar> doorNeighborsList = new ArrayList<BoolVar>();
		
		// if neighbor above, add to ArrayList
		if (row > 0) {
			doorNeighborsList.add(doorCells[row - 1][col]);
		}
		// if neighbor right, add to ArrayList
		if (col < 4) {
			doorNeighborsList.add(doorCells[row][col + 1]);
		}
		// if neighbor below, add to ArrayList
		if (row < 4) {
			doorNeighborsList.add(doorCells[row + 1][col]);
		}
		// if neighbor left, add to ArrayList
		if (col > 0) {
			doorNeighborsList.add(doorCells[row][col - 1]);
		}
		// convert ArrayList neighbors to array
		doorNeighbors = doorNeighborsList.toArray(new BoolVar[doorNeighborsList.size()]);
		// return studentNeighbors
		return doorNeighbors;
	}

	
	private void updateCurrentCellVariables() {
		// get current position		
		// used to obtain percepts
		StudentWorldCell currentCell = (StudentWorldCell) player.getCell();
		int row = currentCell.getRow();
		int col = currentCell.getCol();
		
		// if current cell smells with no glowly cell, set smellyCells to true
		if (currentCell.getPercepts().contains(StudentWorldPercept.SMELL) && !currentCell.getPercepts().contains(StudentWorldPercept.GLOW)) {
			model.arithm(smellyCells[row][col], "=", 1).post(); // smell true
			model.arithm(glowyCells[row][col], "=", 0).post();	// glow false
		}
		// if current cell is glowly with no smelly cell, set glowlyCells to true
		else if(currentCell.getPercepts().contains(StudentWorldPercept.GLOW) && !currentCell.getPercepts().contains(StudentWorldPercept.SMELL))
		{
			model.arithm(smellyCells[row][col], "=", 0).post(); // smell false
			model.arithm(glowyCells[row][col], "=", 1).post();	// glow true
		}
		// if current cell has both percepts: smelly and glowy, add constraints
		else if(currentCell.getPercepts().contains(StudentWorldPercept.SMELL) && currentCell.getPercepts().contains(StudentWorldPercept.GLOW))
		{
			model.arithm(smellyCells[row][col], "=", 1).post(); // smell true
			model.arithm(glowyCells[row][col], "=", 1).post();	// glow true
		}
		// set current cells instance variables (i.e. smellyCells, glowlyCells, etc) with false for current position
		// since our percepts are empty
		else
		{
			// everything is false since we don't have knowledge of any of them.
			model.arithm(glowyCells[row][col], "=", 0).post();
			model.arithm(smellyCells[row][col], "=", 0).post();
		}
		// always set door/student to false
		model.arithm(studentCells[row][col], "=", 0).post();// student false
		model.arithm(doorCells[row][col], "=", 0).post();	// door false
		
		printAllVariables();
	}
	
	
	@SuppressWarnings("unused")
	private void printAllVariables() {
		for(int row=0; row<5; row++) {
			for(int col=0; col<5; col++) {
				System.out.println(smellyCells[row][col]);
				System.out.println(glowyCells[row][col]);
				System.out.println(studentCells[row][col]);
				System.out.println(doorCells[row][col]);
			}
		}
	}
}
