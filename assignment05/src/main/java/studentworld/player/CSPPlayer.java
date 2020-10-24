package studentworld.player;

import java.util.ArrayList;
import java.util.List;

import gridgames.data.action.Action;
import gridgames.data.action.MoveAction;
import gridgames.display.Display;
import gridgames.grid.Cell;
import studentworld.StudentWorldConstraintSolver;
import studentworld.grid.StudentWorldCell;

public class CSPPlayer extends StudentWorldPlayer {
	
	private StudentWorldConstraintSolver swConstraintSolver;
	private List<Action> moves;
	
	public CSPPlayer(List<Action> actions, Display display, Cell initialCell) {
		super(actions, display, initialCell);
		swConstraintSolver = new StudentWorldConstraintSolver(this);
		moves = new ArrayList<Action>();
	}
	
	@Override
	public Action getAction() {
		addVisitedCell((StudentWorldCell) getCell());
		if(moves.isEmpty()) {
			String solverBoard = swConstraintSolver.getBoardState();
			String targetCell = getTargetCell(solverBoard);
			generateMovesOnPath(targetCell);
		}
		return moves.remove(0);
	}
	
	private void generateMovesOnPath(String targetCell) {
		List<StudentWorldCell> visitedNeighbors;
		MoveAction move;
		int distance;
		int closestDistance = Integer.MAX_VALUE;
		StudentWorldCell moveToNeighbor = null;
		Cell currentCell = getCell();
		int onPathRow = currentCell.getRow();
		int onPathCol = currentCell.getCol();
		int parenIndex = targetCell.indexOf("(");
		int targetRow = Character.getNumericValue(targetCell.charAt(parenIndex+1));
		int targetCol = Character.getNumericValue(targetCell.charAt(parenIndex+3));
		int rowDiff = Math.abs(onPathRow - targetRow);
		int colDiff = Math.abs(onPathCol - targetCol);
		
		while(rowDiff + colDiff != 1) {
			//find visited neighbors
			visitedNeighbors = getVisitedNeighbors(onPathRow, onPathCol);
			//pick closest neighbor to target
			for(StudentWorldCell visitedNeighbor : visitedNeighbors) {
				distance = Math.abs(visitedNeighbor.getRow() - targetRow) + Math.abs(visitedNeighbor.getCol() - targetCol);
				if(distance < closestDistance) {
					moveToNeighbor = visitedNeighbor;
					closestDistance = distance;
				}
			}
			//push move to neighbor
			move = getMoveToNeighbor(moveToNeighbor, onPathRow, onPathCol);
			moves.add(move);
			
			//update variables
			onPathRow = moveToNeighbor.getRow();
			onPathCol = moveToNeighbor.getCol();
			rowDiff = Math.abs(onPathRow - targetRow);
			colDiff = Math.abs(onPathCol - targetCol);
		}
		addMoveToUnvisitedCell(targetRow, targetCol, onPathRow, onPathCol);
	}
	
	private MoveAction getMoveToNeighbor(StudentWorldCell neighbor, int fromRow, int fromCol) {
		int neighborRow = neighbor.getRow();
		int neighborCol = neighbor.getCol();
		
		if(neighborRow < fromRow) {
			return MoveAction.UP;
		} else if(neighborCol > fromCol) {
			return MoveAction.RIGHT;
		} else if(neighborRow > fromRow) {
			return MoveAction.DOWN;
		} else {
			return MoveAction.LEFT;
		}
	}
	
	private void addMoveToUnvisitedCell(int targetRow, int targetCol, int onPathRow, int onPathCol) {
		//if move down
		if(targetRow > onPathRow) {
			moves.add(MoveAction.DOWN);
		}
		//if move up
		else if(targetRow < onPathRow){
			moves.add(MoveAction.UP);
		}
		//if move right
		else if(targetCol > onPathCol) {
			moves.add(MoveAction.RIGHT);
		}
		//if move left
		else {
			moves.add(MoveAction.LEFT);
		}
	}
	
	private List<StudentWorldCell> getVisitedNeighbors(int row, int col) {
		List<StudentWorldCell> visitedNeighbors = new ArrayList<StudentWorldCell>();
		int visitedCellRow;
		int visitedCellCol;
		
		for(StudentWorldCell visitedCell : getVisitedCells()) {
			visitedCellRow = visitedCell.getRow();
			visitedCellCol = visitedCell.getCol();
			if(Math.abs(visitedCellRow - row) + Math.abs(visitedCellCol - col) == 1) {
				visitedNeighbors.add(visitedCell);
			}
		}
		return visitedNeighbors;
	}
	
	private boolean visitedCellsContains(int row, int col) {
		for(StudentWorldCell visitedCell : getVisitedCells()) {
			if(visitedCell.getRow() == row && visitedCell.getCol() == col) {
				return true;
			}
		}
		return false;
	}
	
	private String getTargetCell(String solverBoard) {
		List<String> fringeCells;
		String closestSafeFringeCell;
		String closestRiskyFringeCell;
		String doorCell = getDoorCell(solverBoard);
		Cell currentCell = getCell();
		
		//if door cell is known, go there
		if(doorCell != null) {
			return doorCell;
		}
		
		fringeCells = getFringeCells(solverBoard);
		closestSafeFringeCell = getClosestSafeFringeCell(fringeCells, currentCell);
		//otherwise, if safe fringe cell is known, go there
		if(closestSafeFringeCell != null && !closestSafeFringeCell.isEmpty()) {
			return closestSafeFringeCell;
		}
		
		closestRiskyFringeCell = getClosestRiskyFringeCell(fringeCells, currentCell);
		//otherwise, if unknown fringe cell is known, go there
		if(closestRiskyFringeCell != null && !closestRiskyFringeCell.isEmpty()) {
			return closestRiskyFringeCell;
		}
		
		return null;
	}
	
	private List<String> getFringeCells(String solverBoard) {
		int parenIndex;
		int row;
		int col;
		
		List<String> fringeCells = new ArrayList<String>();
		String[] allCellInfo = solverBoard.split("\n");
		for(String cellInfo : allCellInfo) {
			if(cellInfo.startsWith("student")) {
				parenIndex = cellInfo.indexOf("(");
				row = Character.getNumericValue(cellInfo.charAt(parenIndex+1));
				col = Character.getNumericValue(cellInfo.charAt(parenIndex+3));		
				if(isOnFringe(row, col)) {
					fringeCells.add(cellInfo);
				}
			}
		}
		return fringeCells;
	}
	
	private boolean isOnFringe(int row, int col) {
		int visitedRow;
		int visitedCol;
		
		//if the cell has been visited, it is not on the fringe
		if(visitedCellsContains(row, col)) {
			return false;
		}
		
		//if the cell is adjacent to a visited cell, it is on the fringe
		for(Cell visitedCell : getVisitedCells()) {
			visitedRow = visitedCell.getRow();
			visitedCol = visitedCell.getCol();
			if(Math.abs(visitedRow - row) + Math.abs(visitedCol - col) == 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private String getDoorCell(String solverBoard) {
		String[] allCellInfo = solverBoard.split("\n");
		for(String cellInfo : allCellInfo) {
			if(cellInfo.startsWith("door") && cellInfo.endsWith("1")) {
				return cellInfo;
			}
		}
		return null;
	}
	
	private String getClosestRiskyFringeCell(List<String> fringeCells, Cell currentCell) {
		return getClosestFringeCell(fringeCells, currentCell, false);
	}
	
	private String getClosestSafeFringeCell(List<String> fringeCells, Cell currentCell) {
		return getClosestFringeCell(fringeCells, currentCell, true);
	}
	
	private String getClosestFringeCell(List<String> fringeCells, Cell currentCell, boolean isSafe) {
		String closestSafeCell = null;
		int closestDistance = Integer.MAX_VALUE;
		int currentRow = currentCell.getRow();
		int currentCol = currentCell.getCol();
		int parenIndex;
		int row;
		int col;
		int distance;
		String cellStatus;
		
		if(isSafe) {
			cellStatus = "0";
		} else {
			cellStatus = "]";
		}
		
		for(String cellInfo : fringeCells) {
			//if sell is safe
			if(cellInfo.startsWith("student") && cellInfo.endsWith(cellStatus)) {
				parenIndex = cellInfo.indexOf("(");
				row = Character.getNumericValue(cellInfo.charAt(parenIndex+1));
				col = Character.getNumericValue(cellInfo.charAt(parenIndex+3));
				distance = Math.abs(currentRow - row) + Math.abs(currentCol - col);
				//if distance is less than closestDistance
				if(distance < closestDistance) {
					closestSafeCell = cellInfo;
					closestDistance = distance;
				}
			}
		}
		return closestSafeCell;
	}
}
