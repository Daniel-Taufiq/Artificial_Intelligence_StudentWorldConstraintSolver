package studentworld;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import gridgames.player.Player;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.junit.Before;
import org.junit.Test;

import gridgames.data.action.Action;
import gridgames.data.action.MoveAction;
import gridgames.display.ConsoleDisplay;
import gridgames.display.Display;
import gridgames.grid.Cell;
import studentworld.StudentWorld;
import studentworld.StudentWorldConstraintSolver;
import studentworld.data.StudentWorldPercept;
import studentworld.grid.StudentWorldCell;
import studentworld.player.StudentWorldPlayer;

public class StudentWorldConstraintSolverTest {

	private StudentWorldConstraintSolver swConstraintSolver;
	private StudentWorldPlayer player;
	private Field model;
	private Field smellyCells;
	private Field glowyCells;
	private Field studentCells;
	private Field doorCells;
	private Field solver;
	private Method updateCurrentCellVariables;

	@Before
	public void setUp() throws Exception {
		List<Action> actions = MoveAction.getAllActions();
		Display display = new ConsoleDisplay();
		StudentWorld sw = new StudentWorld(display, 5, 5, 5);
		Cell initialCell = sw.getInitialCell();
		player = new StudentWorldPlayer(actions, display, initialCell);
		swConstraintSolver = new StudentWorldConstraintSolver(player);

		model = StudentWorldConstraintSolver.class.getDeclaredField("model");
		model.setAccessible(true);
		smellyCells = StudentWorldConstraintSolver.class.getDeclaredField("smellyCells");
		smellyCells.setAccessible(true);
		glowyCells = StudentWorldConstraintSolver.class.getDeclaredField("glowyCells");
		glowyCells.setAccessible(true);
		studentCells = StudentWorldConstraintSolver.class.getDeclaredField("studentCells");
		studentCells.setAccessible(true);
		doorCells = StudentWorldConstraintSolver.class.getDeclaredField("doorCells");
		doorCells.setAccessible(true);
		solver = StudentWorldConstraintSolver.class.getDeclaredField("solver");
		solver.setAccessible(true);
		updateCurrentCellVariables = StudentWorldConstraintSolver.class.getDeclaredMethod("updateCurrentCellVariables");
		updateCurrentCellVariables.setAccessible(true);
	}

	@Test
	public void testInitialSetup() {
		try {
			BoolVar[][] smellyCells = (BoolVar[][])this.smellyCells.get(swConstraintSolver);
			BoolVar[][] glowyCells = (BoolVar[][])this.glowyCells.get(swConstraintSolver);
			BoolVar[][] studentCells = (BoolVar[][])this.studentCells.get(swConstraintSolver);
			BoolVar[][] doorCells = (BoolVar[][])this.doorCells.get(swConstraintSolver);

			for(int row=0; row<5; row++) {
				for(int col=0; col<5; col++) {
					assertEquals("smellyCells not correct after initial setup", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
					assertEquals("glowyCells not correct after initial setup", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
					assertEquals("studentCells not correct after initial setup", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
					assertEquals("doorCells not correct after initial setup", "door ("+row+","+col+") = [0,1]", doorCells[row][col].toString());
				}
			}
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testWithoutPercepts() {
		try {
			Model model = (Model)this.model.get(swConstraintSolver);
			BoolVar[][] smellyCells = (BoolVar[][])this.smellyCells.get(swConstraintSolver);
			BoolVar[][] glowyCells = (BoolVar[][])this.glowyCells.get(swConstraintSolver);
			BoolVar[][] studentCells = (BoolVar[][])this.studentCells.get(swConstraintSolver);
			BoolVar[][] doorCells = (BoolVar[][])this.doorCells.get(swConstraintSolver);
			Solver solver = (Solver)this.solver.get(swConstraintSolver);

			model.arithm(smellyCells[0][0], "=", 0).post();
			model.arithm(glowyCells[0][0], "=", 0).post();
			model.arithm(studentCells[0][0], "=", 0).post();
			model.arithm(doorCells[0][0], "=", 0).post();
			solver.propagate();

			for(int row=0; row<5; row++) {
				for(int col=0; col<5; col++) {
					if(row==0 && col==0) {
						assertEquals("smellyCells not correct after propagation following a cell with no percepts", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a cell with no percepts", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a cell with no percepts", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a cell with no percepts", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if((row==0 && col==1) || (row==1 && col==0)) {
						assertEquals("smellyCells not correct after propagation following a cell with no percepts", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a cell with no percepts", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a cell with no percepts", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a cell with no percepts", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else {
						assertEquals("smellyCells not correct after propagation following a cell with no percepts", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a cell with no percepts", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a cell with no percepts", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a cell with no percepts", "door ("+row+","+col+") = [0,1]", doorCells[row][col].toString());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testWithSmellyCell() {
		try {
			Model model = (Model)this.model.get(swConstraintSolver);
			BoolVar[][] smellyCells = (BoolVar[][])this.smellyCells.get(swConstraintSolver);
			BoolVar[][] glowyCells = (BoolVar[][])this.glowyCells.get(swConstraintSolver);
			BoolVar[][] studentCells = (BoolVar[][])this.studentCells.get(swConstraintSolver);
			BoolVar[][] doorCells = (BoolVar[][])this.doorCells.get(swConstraintSolver);
			Solver solver = (Solver)this.solver.get(swConstraintSolver);

			model.arithm(smellyCells[0][0], "=", 0).post();
			model.arithm(glowyCells[0][0], "=", 0).post();
			model.arithm(studentCells[0][0], "=", 0).post();
			model.arithm(doorCells[0][0], "=", 0).post();
			model.arithm(smellyCells[0][1], "=", 1).post();
			model.arithm(glowyCells[0][1], "=", 0).post();
			model.arithm(studentCells[0][1], "=", 0).post();
			model.arithm(doorCells[0][1], "=", 0).post();
			solver.propagate();

			for(int row=0; row<5; row++) {
				for(int col=0; col<5; col++) {
					if(row==0 && col==0) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==1 && col==0) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==0 && col==1) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = 1", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if((row==0 && col==2) || (row==1 && col==1)) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = [0,1]", doorCells[row][col].toString());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testWithGlowyCell() {
		try {
			Model model = (Model)this.model.get(swConstraintSolver);
			BoolVar[][] smellyCells = (BoolVar[][])this.smellyCells.get(swConstraintSolver);
			BoolVar[][] glowyCells = (BoolVar[][])this.glowyCells.get(swConstraintSolver);
			BoolVar[][] studentCells = (BoolVar[][])this.studentCells.get(swConstraintSolver);
			BoolVar[][] doorCells = (BoolVar[][])this.doorCells.get(swConstraintSolver);
			Solver solver = (Solver)this.solver.get(swConstraintSolver);

			model.arithm(smellyCells[0][0], "=", 0).post();
			model.arithm(glowyCells[0][0], "=", 0).post();
			model.arithm(studentCells[0][0], "=", 0).post();
			model.arithm(doorCells[0][0], "=", 0).post();
			model.arithm(smellyCells[0][1], "=", 0).post();
			model.arithm(glowyCells[0][1], "=", 1).post();
			model.arithm(studentCells[0][1], "=", 0).post();
			model.arithm(doorCells[0][1], "=", 0).post();
			solver.propagate();

			for(int row=0; row<5; row++) {
				for(int col=0; col<5; col++) {
					if(row==0 && col==0) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==1 && col==0) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==0 && col==1) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 1", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if((row==0 && col==2) || (row==1 && col==1)) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertNotEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 1", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = [0,1]", doorCells[row][col].toString());
					} else {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertNotEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 1", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertNotEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 1", doorCells[row][col].toString());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testWithSmellyCellAndPropagatedSafety() {
		try {
			Model model = (Model)this.model.get(swConstraintSolver);
			BoolVar[][] smellyCells = (BoolVar[][])this.smellyCells.get(swConstraintSolver);
			BoolVar[][] glowyCells = (BoolVar[][])this.glowyCells.get(swConstraintSolver);
			BoolVar[][] studentCells = (BoolVar[][])this.studentCells.get(swConstraintSolver);
			BoolVar[][] doorCells = (BoolVar[][])this.doorCells.get(swConstraintSolver);
			Solver solver = (Solver)this.solver.get(swConstraintSolver);

			model.arithm(smellyCells[0][0], "=", 0).post();
			model.arithm(glowyCells[0][0], "=", 0).post();
			model.arithm(studentCells[0][0], "=", 0).post();
			model.arithm(doorCells[0][0], "=", 0).post();
			model.arithm(smellyCells[0][1], "=", 1).post();
			model.arithm(glowyCells[0][1], "=", 0).post();
			model.arithm(studentCells[0][1], "=", 0).post();
			model.arithm(doorCells[0][1], "=", 0).post();
			model.arithm(smellyCells[1][0], "=", 0).post();
			model.arithm(glowyCells[1][0], "=", 0).post();
			model.arithm(studentCells[1][0], "=", 0).post();
			model.arithm(doorCells[1][0], "=", 0).post();
			solver.propagate();

			for(int row=0; row<5; row++) {
				for(int col=0; col<5; col++) {
					if(row==0 && col==0) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==1 && col==0) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==0 && col==1) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = 1", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==1 && col==1) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==0 && col==2) {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 1", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if((row==0 && col==3) || (row==1 && col==2)){
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = 1", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = [0,1]", doorCells[row][col].toString());
					} else if(row==2 && col==0){
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else {
						assertEquals("smellyCells not correct after propagation following a smelly cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a smelly cell", "glowy ("+row+","+col+") = [0,1]", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a smelly cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a smelly cell", "door ("+row+","+col+") = [0,1]", doorCells[row][col].toString());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testWithSmellyGlowyAndPropagation() {
		try {
			Model model = (Model)this.model.get(swConstraintSolver);
			BoolVar[][] smellyCells = (BoolVar[][])this.smellyCells.get(swConstraintSolver);
			BoolVar[][] glowyCells = (BoolVar[][])this.glowyCells.get(swConstraintSolver);
			BoolVar[][] studentCells = (BoolVar[][])this.studentCells.get(swConstraintSolver);
			BoolVar[][] doorCells = (BoolVar[][])this.doorCells.get(swConstraintSolver);
			Solver solver = (Solver)this.solver.get(swConstraintSolver);

			model.arithm(smellyCells[0][0], "=", 0).post();
			model.arithm(glowyCells[0][0], "=", 0).post();
			model.arithm(studentCells[0][0], "=", 0).post();
			model.arithm(doorCells[0][0], "=", 0).post();
			model.arithm(smellyCells[0][1], "=", 0).post();
			model.arithm(glowyCells[0][1], "=", 1).post();
			model.arithm(studentCells[0][1], "=", 0).post();
			model.arithm(doorCells[0][1], "=", 0).post();
			model.arithm(smellyCells[1][0], "=", 1).post();
			model.arithm(glowyCells[1][0], "=", 1).post();
			model.arithm(studentCells[1][0], "=", 0).post();
			model.arithm(doorCells[1][0], "=", 0).post();
			solver.propagate();

			for(int row=0; row<5; row++) {
				for(int col=0; col<5; col++) {
					if(row==0 && col==0) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==0 && col==1) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = 0", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 1", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==1 && col==0) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = 1", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 1", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==1 && col==1) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 1", doorCells[row][col].toString());
					} else if(row==2 && col==0){
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 1", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==2 && col==1){
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = 1", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 1", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==1 && col==2){
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 1", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==0 && col==2){
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = 0", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else if(row==3 && col==0) {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = 1", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					} else {
						assertEquals("smellyCells not correct after propagation following a glowy cell", "smelly ("+row+","+col+") = [0,1]", smellyCells[row][col].toString());
						assertEquals("glowyCells not correct after propagation following a glowy cell", "glowy ("+row+","+col+") = 0", glowyCells[row][col].toString());
						assertEquals("studentCells not correct after propagation following a glowy cell", "student ("+row+","+col+") = [0,1]", studentCells[row][col].toString());
						assertEquals("doorCells not correct after propagation following a glowy cell", "door ("+row+","+col+") = 0", doorCells[row][col].toString());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}

	@Test
	public void testUpdateCurrentCellVariables() {
		BoolVar[][] smellyCells;
		BoolVar[][] glowyCells;
		BoolVar[][] studentCells;
		BoolVar[][] doorCells;
		Solver solver;
		try {
			StudentWorldCell currentCell;
			Random r = new Random();
			int randomRow;
			int randomCol;

			for(int i=0; i<10; i++) {
				swConstraintSolver = new StudentWorldConstraintSolver(player);
				smellyCells = (BoolVar[][])this.smellyCells.get(swConstraintSolver);
				glowyCells = (BoolVar[][])this.glowyCells.get(swConstraintSolver);
				studentCells = (BoolVar[][])this.studentCells.get(swConstraintSolver);
				doorCells = (BoolVar[][])this.doorCells.get(swConstraintSolver);
				solver = (Solver)this.solver.get(swConstraintSolver);
				do {
					randomRow = r.nextInt(5);
					randomCol = r.nextInt(5);
				} while(randomRow+randomCol <= 1);
				currentCell = new StudentWorldCell(randomRow,randomCol);
				currentCell.addPercept(StudentWorldPercept.SMELL);
				currentCell.addPercept(StudentWorldPercept.GLOW);
				player.getGamePlayer().setCell(currentCell);
				updateCurrentCellVariables.invoke(swConstraintSolver);
				solver.propagate();

				assertEquals("smellyCells not correct after propagation following updateCurrentCellVariables call", "smelly ("+randomRow+","+randomCol+") = 1", smellyCells[randomRow][randomCol].toString());
				assertEquals("glowyCells not correct after propagation following updateCurrentCellVariables call", "glowy ("+randomRow+","+randomCol+") = 1", glowyCells[randomRow][randomCol].toString());
				assertEquals("studentCells not correct after propagation following updateCurrentCellVariables call", "student ("+randomRow+","+randomCol+") = 0", studentCells[randomRow][randomCol].toString());
				assertEquals("doorCells not correct after propagation following updateCurrentCellVariables call", "door ("+randomRow+","+randomCol+") = 0", doorCells[randomRow][randomCol].toString());
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		} catch(Exception e) {
			e.printStackTrace();
			fail("check the console for the exception stack trace");
		}
	}
}
