/*
 /*
 * File: GraphicsContest.java
 * --------------------------
 */

import acm.program.*;
import acm.graphics.*;
import acm.util.*;
import java.awt.event.*;
import java.awt.*;

public class GraphicsContest extends GraphicsProgram implements GraphicsContestConstants {
	
	private RandomGenerator rgen = new RandomGenerator();
	private String turns = "";
	private int numberOfTurns;
	private int[][] cubePosition = new int[FACES][SEVEN + 1];
	private int pauseTime = REGULAR_SPEED;
	private double[][] vertexMatrix = new double[VERTEX_SEVEN + 1][Z + 1];
	private boolean solving;
	
	//graphics instance variables:
	private boolean solveButtonPressed = false;
	private boolean stepByStep = false;
	private boolean nextMoveButtonClicked;
	private boolean howToUse = false;
	private boolean userScramble = false;
	private boolean negXRotation = false;
	private boolean posXRotation = false;
	private boolean negYRotation = false;
	private boolean posYRotation = false;

	
	
	public static void main(String[] args) {
		new GraphicsContest().start(args);
	}
	
	public void run() {
		initializeAllFaces();
		addGraphics();
		rotateOnX(2 * THETA / 3);
		rotateOnY(2 * THETA / 3);
		addMouseListeners();
		waitForButtonClick();
	}
	
	
	//solve method
	private void solve() {
		solving = true;
		getWhiteCross();
		pause(SECONDARY_PAUSE_TIME);
		getCorners();
		addDivider();
		pause(SECONDARY_PAUSE_TIME);
		while(middleLayerNotSolved()) {	
			for(int i = 0; i < 4; i++) getEdges();
			checkEdges();
			if(numberOfTurns > BREAK_POINT) {
				printError();
				break;
			}
		}
		pause(SECONDARY_PAUSE_TIME);
		getYellow();
		addDivider();
		pause(SECONDARY_PAUSE_TIME);
		finishSolve();
		updateSolutionLabel();
		printNumberOfTurns();
		solving = false;
	}

	
	private void waitForButtonClick() {
		while(true) {
			waitForClick();
			pause(ROTATION_PAUSE_TIME);
			if(negXRotation) {
				rotateOnX(-THETA);
				negXRotation = false;
			} else if(posXRotation) {
				rotateOnX(THETA);
				posXRotation = false;
			} else if(negYRotation) {
				rotateOnY(-THETA);
				negYRotation = false;
			} else if(posYRotation) {
				rotateOnY(THETA);
				posYRotation = false;
			} else if(solveButtonPressed) {
				if(!cubeSolved()) {
					solve();
				}
				solveButtonPressed = false;
			}
		}
	}
	
	
	private boolean cubeSolved() {
		for(int j = 0; j < BLUE; j++) {
			for(int i = 0; i < SEVEN; i++) {
				if(cubePosition[j][i] != j) return false;
			}
		}
		return true;
	}
	
	
	private void initializeAllFaces() {
		initializeFace(WHITE);
		initializeFace(YELLOW);
		initializeFace(RED);
		initializeFace(ORANGE);
		initializeFace(GREEN);
		initializeFace(BLUE);
	}
	
	
	private void printArray() {
		GObject oldMatrix = getElementAt(3 * getWidth() / 4, getHeight() / 2 + LABEL_DISPLACEMENT);
		if(oldMatrix == null) {
			GCompound arrayLabels = new GCompound();
			for(int j = 0; j < FACES; j++) {	
				String cubeFace = "";
				for(int i = 0; i < PIECES_PER_SIDE; i++) {
					cubeFace += cubePosition[j][i] + " ";
				}
				GLabel individualFaceArray = new GLabel(cubeFace);
				arrayLabels.add(individualFaceArray, (getWidth() - individualFaceArray.getWidth()) / 2, 
										  getHeight() / 2 + j * LABEL_DISPLACEMENT);
			}
			add(arrayLabels, getWidth() / 4, 0);
		}
	}
	
	private void deleteArray() {
		GObject oldMatrix = getElementAt(3 * getWidth() / 4, getHeight() / 2 + LABEL_DISPLACEMENT);
		if(oldMatrix != null) remove(oldMatrix);
	}
	
	
	private void printError() {
		GLabel error = new GLabel("Error");
		add(error, (getWidth() - error.getWidth()) / 2, 
						LABEL_DISPLACEMENT * (7 + numberOfTurns / MOVES_PER_ROW));
	}

	private void deleteError() {
		GObject oldErrorMessage = getElementAt(getWidth() / 2, 
				LABEL_DISPLACEMENT * (7 + numberOfTurns / MOVES_PER_ROW));
		if(oldErrorMessage != null) remove(oldErrorMessage);
	}
	
	
	private void initializeFace(int colorID) {
		for(int i = 0; i < PIECES_PER_SIDE; i++) {
			cubePosition[colorID][i] = colorID;
		}
	}

	
	private void randomScramble() {
		String scramble = "Scramble: ";
		for(int i = 0; i < SCRAMBLE_MOVES; i++) {
			int move = rgen.nextInt(0, RIGHT_TWO);
			scramble = convertToLetter(move, scramble);
		}
		GLabel scrambleLabel = new GLabel(scramble);
		add(scrambleLabel, (getWidth() - scrambleLabel.getWidth()) / 2, getHeight() - 5 * LABEL_DISPLACEMENT);
	}
	
	private void deleteOldScrambleLabel() {
		GObject oldLabel = getElementAt(getWidth() / 2, getHeight() - 5 * LABEL_DISPLACEMENT);
		if(oldLabel != null) remove(oldLabel);
	}
	
	
	private void updateArray() {
		if(negXRotation) {
			rotateOnX(-THETA);
			negXRotation = false;
		} else if(posXRotation) {
			rotateOnX(THETA);
			posXRotation = false;
		} else if(negYRotation) {
			rotateOnY(-THETA);
			negYRotation = false;
		} else if(posYRotation) {
			rotateOnY(THETA);
			posYRotation = false;
		}
		addCube();
		GObject oldMatrix = getElementAt(3 * getWidth() / 4, getHeight() / 2 + LABEL_DISPLACEMENT);
		if(oldMatrix != null) {
			remove(oldMatrix);
			GCompound arrayLabels = new GCompound();
			for(int j = 0; j < FACES; j++) {	
				String cubeFace = "";
				for(int i = 0; i < PIECES_PER_SIDE; i++) {
					cubeFace += cubePosition[j][i] + " ";
				}
				GLabel individualFaceArray = new GLabel(cubeFace);
				arrayLabels.add(individualFaceArray, (getWidth() - individualFaceArray.getWidth()) / 2, 
										  getHeight() / 2 + j * LABEL_DISPLACEMENT);
			}
			add(arrayLabels, getWidth() / 4, 0);
		}
	}

	
	private void updateSolutionLabel() {
		GObject oldMovesLabel = getElementAt(getWidth() / 2, 
				LABEL_DISPLACEMENT * (4 + (numberOfTurns - 1) / MOVES_PER_ROW));
		if(oldMovesLabel != null) remove(oldMovesLabel);
		GLabel movesDone = new GLabel(turns);
		add(movesDone, (getWidth() - movesDone.getWidth()) / 2, 
				LABEL_DISPLACEMENT * (4 + (numberOfTurns - 1) / MOVES_PER_ROW));
	}
	
	private void deleteOldSolutionLabels() {
		for(int i = 0; i < numberOfTurns / MOVES_PER_ROW + 1; i++) {
			GObject oldSolutionLabel = getElementAt(getWidth() / 2, 
					LABEL_DISPLACEMENT * (4 + i));
			if(oldSolutionLabel != null) remove(oldSolutionLabel);
		}
	}
	

	private void printNumberOfTurns() {
		GObject oldMovesLabel = getElementAt(getWidth() / 2, 
											 LABEL_DISPLACEMENT * (5 + numberOfTurns / MOVES_PER_ROW));
		if(oldMovesLabel != null) remove(oldMovesLabel);
		GLabel movesDone = new GLabel("Number Of Moves to Solution: " + numberOfTurns);
		add(movesDone, (getWidth() - movesDone.getWidth()) / 2, 
						LABEL_DISPLACEMENT * (5 + numberOfTurns / MOVES_PER_ROW));
	}
	
	private void deleteOldNumberOfMovesLabel() {
		GObject oldMovesLabel = getElementAt(getWidth() / 2, 
											 LABEL_DISPLACEMENT * (5 + numberOfTurns / MOVES_PER_ROW));
		if(oldMovesLabel != null) remove(oldMovesLabel);
	}
	
	
	private String convertToLetter(int moveID, String scramble) {
		switch(moveID) {
			case 0: up();
					scramble += "U ";
					return scramble;
			case 1: down();
					scramble += "D ";
					return scramble;
			case 2: front();
					scramble += "F ";
					return scramble;
			case 3: back();
					scramble += "B ";
					return scramble;
			case 4: left();
					scramble += "L ";
					return scramble;
			case 5: right();
					scramble += "R ";
					return scramble;
			case 6: upPrime();
					scramble += "U' ";
					return scramble;
			case 7: downPrime();
					scramble += "D' ";
					return scramble;
			case 8: frontPrime();
					scramble += "F' ";
					return scramble;
			case 9: backPrime();
					scramble += "B' ";
					return scramble;
			case 10: leftPrime();
					 scramble += "L' ";
					 return scramble;
			case 11: rightPrime();
			 		 scramble += "R' ";
			 		 return scramble;
			case 12: upTwo();
					 scramble += "U2 ";
					 return scramble;
			case 13: downTwo();
					 scramble += "D2 ";
					 return scramble;
			case 14: frontTwo();
					 scramble += "F2 ";
					 return scramble;
			case 15: backTwo();
			 		 scramble += "B2 ";
			 		 return scramble;
			case 16: leftTwo();
					 scramble += "L2 ";
					 return scramble;
			case 17: rightTwo();
					 scramble += "R2 ";
					 return scramble;
			default: return "WUT";
		}
	}
	
	
	private void addDivider() {
		turns += "|| ";
		updateSolutionLabel();
	}
	
	
	private boolean middleLayerNotSolved() {
		if(cubePosition[RED][THREE] == RED && cubePosition[RED][SEVEN] == RED &&
			cubePosition[ORANGE][THREE] == ORANGE && cubePosition[ORANGE][SEVEN] == ORANGE &&
			cubePosition[GREEN][THREE] == GREEN && cubePosition[GREEN][SEVEN] == GREEN &&
			cubePosition[BLUE][THREE] == BLUE && cubePosition[BLUE][SEVEN] == BLUE) {
			return false;
		}
		return true;
	}
	





	// define turn methods (white always faces up, red faces forward):

	private void sideRotations(int firstColor,
			int secondColor, int thirdColor,
			int fourthColor, int pieceNumber) {
		int firstColorColor = cubePosition[firstColor][pieceNumber];
		cubePosition[firstColor][pieceNumber] = cubePosition[secondColor][pieceNumber];
		cubePosition[secondColor][pieceNumber] = cubePosition[thirdColor][pieceNumber];
		cubePosition[thirdColor][pieceNumber] = cubePosition[fourthColor][pieceNumber];
		cubePosition[fourthColor][pieceNumber] = firstColorColor;
	}

	private void clockwiseFaceRotation(int colorID) {
		int originalZero = cubePosition[colorID][ZERO];
		int originalOne = cubePosition[colorID][ONE];
		cubePosition[colorID][ZERO] = cubePosition[colorID][SIX];
		cubePosition[colorID][SIX] = cubePosition[colorID][FOUR];
		cubePosition[colorID][FOUR] = cubePosition[colorID][TWO];
		cubePosition[colorID][TWO] = originalZero;
		cubePosition[colorID][ONE] = cubePosition[colorID][SEVEN];
		cubePosition[colorID][SEVEN] = cubePosition[colorID][FIVE];
		cubePosition[colorID][FIVE] = cubePosition[colorID][THREE];
		cubePosition[colorID][THREE] = originalOne;
	}

	private void up() {
		clockwiseFaceRotation(W);
		sideRotations(R, B, O, G, ZERO);
		sideRotations(R, B, O, G, ONE);
		sideRotations(R, B, O, G, TWO);
	}

	private void down() {
		clockwiseFaceRotation(Y);
		sideRotations(R, G, O, B, SIX);
		sideRotations(R, G, O, B, FIVE);
		sideRotations(R, G, O, B, FOUR);
	}

	private void front() {
		clockwiseFaceRotation(R);
		int originalWSix = cubePosition[W][SIX];
		int originalWFive = cubePosition[W][FIVE];
		int originalWFour = cubePosition[W][FOUR];
		cubePosition[W][SIX] = cubePosition[G][FOUR];
		cubePosition[W][FIVE] = cubePosition[G][THREE];
		cubePosition[W][FOUR] = cubePosition[G][TWO];
		cubePosition[G][FOUR] = cubePosition[Y][TWO];
		cubePosition[G][THREE] = cubePosition[Y][ONE];
		cubePosition[G][TWO] = cubePosition[Y][ZERO];
		cubePosition[Y][TWO] = cubePosition[B][ZERO];
		cubePosition[Y][ONE] = cubePosition[B][SEVEN];
		cubePosition[Y][ZERO] = cubePosition[B][SIX];
		cubePosition[B][ZERO] = originalWSix;
		cubePosition[B][SEVEN] = originalWFive;
		cubePosition[B][SIX] = originalWFour;
	}

	private void back() {
		clockwiseFaceRotation(O);
		int originalWZero = cubePosition[W][ZERO];
		int originalWOne = cubePosition[W][ONE];
		int originalWTwo = cubePosition[W][TWO];
		cubePosition[W][ZERO] = cubePosition[B][TWO];
		cubePosition[W][ONE] = cubePosition[B][THREE];
		cubePosition[W][TWO] = cubePosition[B][FOUR];
		cubePosition[B][TWO] = cubePosition[Y][FOUR];
		cubePosition[B][THREE] = cubePosition[Y][FIVE];
		cubePosition[B][FOUR] = cubePosition[Y][SIX];
		cubePosition[Y][FOUR] = cubePosition[G][SIX];
		cubePosition[Y][FIVE] = cubePosition[G][SEVEN];
		cubePosition[Y][SIX] = cubePosition[G][ZERO];
		cubePosition[G][SIX] = originalWZero;
		cubePosition[G][SEVEN] = originalWOne;
		cubePosition[G][ZERO] = originalWTwo;
	}

	private void left() {
		clockwiseFaceRotation(G);
		int originalWZero = cubePosition[W][ZERO];
		int originalWSeven = cubePosition[W][SEVEN];
		int originalWSix = cubePosition[W][SIX];
		cubePosition[W][ZERO] = cubePosition[O][FOUR];
		cubePosition[W][SEVEN] = cubePosition[O][THREE];
		cubePosition[W][SIX] = cubePosition[O][TWO];
		cubePosition[O][FOUR] = cubePosition[Y][ZERO];
		cubePosition[O][THREE] = cubePosition[Y][SEVEN];
		cubePosition[O][TWO] = cubePosition[Y][SIX];
		cubePosition[Y][ZERO] = cubePosition[R][ZERO];
		cubePosition[Y][SEVEN] = cubePosition[R][SEVEN];
		cubePosition[Y][SIX] = cubePosition[R][SIX];
		cubePosition[R][ZERO] = originalWZero;
		cubePosition[R][SEVEN] = originalWSeven;
		cubePosition[R][SIX] = originalWSix;
	}

	private void right() {
		clockwiseFaceRotation(B);
		int originalWTwo = cubePosition[W][TWO];
		int originalWThree = cubePosition[W][THREE];
		int originalWFour = cubePosition[W][FOUR];
		cubePosition[W][TWO] = cubePosition[R][TWO];
		cubePosition[W][THREE] = cubePosition[R][THREE];
		cubePosition[W][FOUR] = cubePosition[R][FOUR];
		cubePosition[R][TWO] = cubePosition[Y][TWO];
		cubePosition[R][THREE] = cubePosition[Y][THREE];
		cubePosition[R][FOUR] = cubePosition[Y][FOUR];
		cubePosition[Y][TWO] = cubePosition[O][SIX];
		cubePosition[Y][THREE] = cubePosition[O][SEVEN];
		cubePosition[Y][FOUR] = cubePosition[O][ZERO];
		cubePosition[O][SIX] = originalWTwo;
		cubePosition[O][SEVEN] = originalWThree;
		cubePosition[O][ZERO] = originalWFour;
	}

	private void upPrime() {
		up();
		up();
		up();
	}

	private void downPrime() {
		down();
		down();
		down();
	}

	private void frontPrime() {
		front();
		front();
		front();
	}

	private void backPrime() {
		back();
		back();
		back();
	}

	private void leftPrime() {
		left();
		left();
		left();
	}

	private void rightPrime() {
		right();
		right();
		right();
	}

	private void upTwo() {
		up();
		up();
	}

	private void downTwo() {
		down();
		down();
	}

	private void frontTwo() {
		front();
		front();
	}

	private void backTwo() {
		back();
		back();
	}

	private void leftTwo() {
		left();
		left();
	}

	private void rightTwo() {
		right();
		right();
	}
	
	private void move(int moveID) {
		if(numberOfTurns % MOVES_PER_ROW == 0 && numberOfTurns != 0) turns = "";
		if(stepByStep) {
			while(true) {
				waitForClick();
				pause(ROTATION_PAUSE_TIME);
				if(nextMoveButtonClicked) {
					nextMoveButtonClicked = false;
					break;
				} else if(negXRotation) {
					rotateOnX(-THETA);
					negXRotation = false;
				} else if(posXRotation) {
					rotateOnX(THETA);
					posXRotation = false;
				} else if(negYRotation) {
					rotateOnY(-THETA);
					negYRotation = false;
				} else if(posYRotation) {
					rotateOnY(THETA);
					posYRotation = false;
				}
			}
		}
		switch(moveID) {
			case 0: pause(pauseTime);
					up();
					updateArray();
					turns += "U ";
					numberOfTurns++;
					break;
			case 1: pause(pauseTime);
					down();
					updateArray();
					turns += "D ";
					numberOfTurns++;
					break;
			case 2: pause(pauseTime);
					front();
					updateArray();
					turns += "F ";
					numberOfTurns++;
					break;
			case 3: pause(pauseTime);
					back();
					updateArray();
					turns += "B ";
					numberOfTurns++;
					break;
			case 4: pause(pauseTime);
					left();
					updateArray();
					turns += "L ";
					numberOfTurns++;
					break;
			case 5: pause(pauseTime);
					right();
					updateArray();
					turns += "R ";
					numberOfTurns++;
					break;
			case 6: pause(pauseTime);
					upPrime();
					updateArray();
					turns += "U' ";
					numberOfTurns++;
					break;
			case 7: pause(pauseTime);
					downPrime();
					updateArray();
					turns += "D' ";
					numberOfTurns++;
					break;
			case 8: pause(pauseTime);
					frontPrime();
					updateArray();
					turns += "F' ";
					numberOfTurns++;
					break;
			case 9: pause(pauseTime);
					backPrime();
					updateArray();
					turns += "B' ";
					numberOfTurns++;
					break;
			case 10: pause(pauseTime);
					 leftPrime();
					 updateArray();
					 turns += "L' ";
					 numberOfTurns++;
					 break;
			case 11: pause(pauseTime);
					 rightPrime();
			 		 updateArray();
			 		 turns += "R' ";
			 		 numberOfTurns++;
					 break;
			case 12: pause(pauseTime);
					 upTwo();
					 updateArray();
					 turns += "U2 ";
					 numberOfTurns++;
					 break;
			case 13: pause(pauseTime);
					 downTwo();
					 updateArray();
					 turns += "D2 ";
					 numberOfTurns++;
					 break;
			case 14: pause(pauseTime);
					 frontTwo();
					 updateArray();
					 turns += "F2 ";
					 numberOfTurns++;
					 break;
			case 15: pause(pauseTime);
					 backTwo();
			 		 updateArray();
			 		 turns += "B2 ";
			 		 numberOfTurns++;
					 break;
			case 16: pause(pauseTime);
					 leftTwo();
					 updateArray();
					 turns += "L2 ";
					 numberOfTurns++;
					 break;
			case 17: pause(pauseTime);
					 rightTwo();
					 updateArray();
					 turns += "R2 ";
					 numberOfTurns++;
					 break;
			default: 
		}
		updateSolutionLabel();
	}
	
	private void invertMove(int moveID) {
		switch(moveID) {
			case 0: move(DOWN);
					break;
			case 1: move(UP);
					break;
			case 2: move(FRONT);
					break;
			case 3: move(BACK);
					break;
			case 4: move(RIGHT);
					break;
			case 5: move(LEFT);
					break;
			case 6: move(DOWN_PRIME);
					break;
			case 7: move(UP_PRIME);
					break;
			case 8: move(FRONT_PRIME);
					break;
			case 9: move(BACK_PRIME);
					break;
			case 10: move(RIGHT_PRIME);
					 break;
			case 11: move(LEFT_PRIME);
					 break;
			case 12: move(DOWN_TWO);
					 break;
			case 13: move(UP_TWO);
					 break;
			case 14: move(FRONT_TWO);
					 break;
			case 15: move(BACK_TWO);
					 break;
			case 16: move(RIGHT_TWO);
					 break;
			case 17: move(LEFT_TWO);
		}
	}
	
	private void moveWithNoLabel(int moveID) {
		switch(moveID) {
			case 0: up();
					updateArray();
					break;
			case 1: down();
					updateArray();
					break;
			case 2: front();
					updateArray();
					break;
			case 3: back();
					updateArray();
					break;
			case 4: left();
					updateArray();
					break;
			case 5: right();
					updateArray();
					break;
			case 6: upPrime();
					updateArray();
					break;
			case 7: downPrime();
					updateArray();
					break;
			case 8: frontPrime();
					updateArray();
					break;
			case 9: backPrime();
					updateArray();
					break;
			case 10: leftPrime();
					 updateArray();
					 break;
			case 11: rightPrime();
			 		 updateArray();
					 break;
			case 12: upTwo();
					 updateArray();
					 break;
			case 13: downTwo();
					 updateArray();
					 break;
			case 14: frontTwo();
					 updateArray();
					 break;
			case 15: backTwo();
			 		 updateArray();
					 break;
			case 16: leftTwo();
					 updateArray();
					 break;
			case 17: rightTwo();
					 updateArray();
					 break;
			default: 
		}
	}

	
	//white cross
	private void getWhiteCross() {
		while(cubePosition[WHITE][ONE] != 0 || cubePosition[WHITE][THREE] != 0 ||
			  cubePosition[WHITE][FIVE] != 0 || cubePosition[WHITE][SEVEN] != 0) {
			getWhiteCrossPiece();
			move(UP);
		}
		fixCrossColors();
	}

	private void getWhiteCrossPiece() {
		while(cubePosition[WHITE][ONE] != WHITE) {
			if(cubePosition[YELLOW][ONE] == WHITE) {
				move(DOWN_TWO);
				move(BACK_TWO);

			} else if(cubePosition[YELLOW][THREE] == WHITE) {
				move(DOWN);
				move(BACK_TWO);

			} else if(cubePosition[YELLOW][FIVE] == WHITE) {
				move(BACK_TWO);

			} else if(cubePosition[YELLOW][SEVEN] == WHITE) {
				move(DOWN_PRIME);
				move(BACK_TWO);

			} else if(cubePosition[RED][ONE] == WHITE) {
				move(FRONT_PRIME);
				move(UP_PRIME);
				move(LEFT_PRIME);

			} else if(cubePosition[RED][THREE] == WHITE) {
				move(UP);
				move(RIGHT);

			} else if(cubePosition[RED][FIVE] == WHITE) {
				move(FRONT_PRIME);
				move(UP);
				move(RIGHT);

			} else if(cubePosition[RED][SEVEN] == WHITE) {
				move(UP_PRIME);
				move(LEFT_PRIME);

			} else if(cubePosition[ORANGE][ONE] == WHITE) {
				move(BACK);
				move(UP_PRIME);
				move(LEFT);

			} else if(cubePosition[ORANGE][THREE] == WHITE) {
				move(UP_PRIME);
				move(LEFT);

			} else if(cubePosition[ORANGE][FIVE] == WHITE) {
				move(BACK_PRIME);
				move(UP_PRIME);
				move(LEFT);

			} else if(cubePosition[ORANGE][SEVEN] == WHITE) {
				move(UP);
				move(RIGHT_PRIME);

			} else if(cubePosition[GREEN][ONE] == WHITE) {
				move(LEFT_PRIME);
				move(BACK_PRIME);

			} else if(cubePosition[GREEN][THREE] == WHITE) {
				move(UP_TWO);
				move(FRONT);

			} else if(cubePosition[GREEN][FIVE] == WHITE) {
				move(LEFT);
				move(BACK_PRIME);
				move(LEFT_PRIME);

			} else if(cubePosition[GREEN][SEVEN] == WHITE) {
				move(BACK_PRIME);

			} else if(cubePosition[BLUE][ONE] == WHITE) {
				move(RIGHT);
				move(BACK);

			} else if(cubePosition[BLUE][THREE] == WHITE) {
				move(BACK);

			} else if(cubePosition[BLUE][FIVE] == WHITE) {
				move(RIGHT_PRIME);
				move(BACK);
				move(RIGHT);

			} else if(cubePosition[BLUE][SEVEN] == WHITE) {
				move(UP_TWO);
				move(FRONT_PRIME);
			}
		}
	}
		
	private void fixCrossColors() {
		alignWhiteOne();
		fixWhiteEdges();
	}

	private void alignWhiteOne() {
		while(cubePosition[ORANGE][ONE] != ORANGE) {
			move(UP);
		}
	}

	private void fixWhiteEdges() {
		if(cubePosition[BLUE][ONE] != BLUE) {
			if(cubePosition[BLUE][ONE] == RED) {
				move(RIGHT_TWO);
				move(DOWN_PRIME);
				move(FRONT_TWO);
				if(cubePosition[RED][FIVE] == GREEN) {
					move(DOWN_PRIME);
					move(LEFT_TWO);
					move(DOWN_TWO);
					move(RIGHT_TWO);
				} else {
					move(DOWN);
					move(RIGHT_TWO);
				}
			} else {
				if(cubePosition[GREEN][ONE] == BLUE) {
					move(LEFT_TWO);
					move(RIGHT_TWO);
					move(DOWN_TWO);
					move(LEFT_TWO);
					move(RIGHT_TWO);
				} else {
					move(RIGHT_TWO);
					move(DOWN_TWO);
					move(LEFT_TWO);
					move(DOWN);
					move(FRONT_TWO);
					move(DOWN);
					move(RIGHT_TWO);
				}
			}
		}
		if(cubePosition[RED][ONE] == GREEN) {
			move(FRONT_TWO);
			move(DOWN_PRIME);
			move(LEFT_TWO);
			move(DOWN);
			move(FRONT_TWO);
		}
		
	}

	
	
	//white corners
	private void getCorners() {
		for(int i = 0; i < 8; i++) solveCorners();
	}

	private void solveCorners() {
		//find corner
			//easy difficulty ones
		if(cubePosition[RED][SIX] == WHITE) {
			if(cubePosition[GREEN][FOUR] == RED) {
				move(DOWN);
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT);

			} else if(cubePosition[GREEN][FOUR] == GREEN) {
				move(FRONT_PRIME);
				move(DOWN_PRIME);
				move(FRONT);

			} else if(cubePosition[GREEN][FOUR] == BLUE) {
				move(RIGHT);
				move(DOWN_TWO);
				move(RIGHT_PRIME);

			} else if(cubePosition[GREEN][FOUR] == ORANGE) {
				move(BACK);
				move(DOWN_PRIME);
				move(BACK_PRIME);
			}
		}

		if(cubePosition[RED][FOUR] == WHITE) {
			if(cubePosition[BLUE][SIX] == BLUE) {
				move(FRONT);
				move(DOWN);
				move(FRONT_PRIME);

			} else if(cubePosition[BLUE][SIX] == GREEN) {
				move(LEFT_PRIME);
				move(DOWN_TWO);
				move(LEFT);

			} else if(cubePosition[BLUE][SIX] == RED) {
				move(DOWN_PRIME);
				move(LEFT);
				move(DOWN);
				move(LEFT_PRIME);

			} else if(cubePosition[BLUE][SIX] == ORANGE) {
				move(BACK_PRIME);
				move(DOWN);
				move(BACK);
			}
		}

		if(cubePosition[BLUE][SIX] == WHITE) {
			if(cubePosition[RED][FOUR] == BLUE) {
				move(DOWN_TWO);
				move(RIGHT);
				move(DOWN_PRIME);
				move(RIGHT_PRIME);

			} else if(cubePosition[RED][FOUR] == GREEN) {
				move(LEFT);
				move(DOWN_PRIME);
				move(LEFT_PRIME);

			} else if(cubePosition[RED][FOUR] == RED) {
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT);

			} else if(cubePosition[RED][FOUR] == ORANGE) {
				move(BACK);
				move(DOWN_TWO);
				move(BACK_PRIME);
			}
		}

		if(cubePosition[BLUE][FOUR] == WHITE) {
			if(cubePosition[ORANGE][SIX] == BLUE) {
				move(DOWN_PRIME);
				move(FRONT);
				move(DOWN);
				move(FRONT_PRIME);

			} else if(cubePosition[ORANGE][SIX] == GREEN) {
				move(LEFT_PRIME);
				move(DOWN);
				move(LEFT);

			} else if(cubePosition[ORANGE][SIX] == RED) {
				move(FRONT_PRIME);
				move(DOWN_TWO);
				move(FRONT);

			} else if(cubePosition[ORANGE][SIX] == ORANGE) {
				move(RIGHT);
				move(DOWN);
				move(RIGHT_PRIME);
			}
		}

		if(cubePosition[ORANGE][SIX] == WHITE) {
			if(cubePosition[BLUE][FOUR] == BLUE) {
				move(BACK_PRIME);
				move(DOWN_PRIME);
				move(BACK);

			} else if(cubePosition[BLUE][FOUR] == GREEN) {
				move(LEFT);
				move(DOWN_TWO);
				move(LEFT_PRIME);

			} else if(cubePosition[BLUE][FOUR] == RED) {
				move(FRONT);
				move(DOWN_PRIME);
				move(FRONT_PRIME);

			} else if(cubePosition[BLUE][FOUR] == ORANGE) {
				move(DOWN);
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
			}
		}

		if(cubePosition[ORANGE][FOUR] == WHITE) {
			if(cubePosition[GREEN][SIX] == BLUE) {
				move(RIGHT_PRIME);
				move(DOWN_TWO);
				move(RIGHT);

			} else if(cubePosition[GREEN][SIX] == GREEN) {
				move(BACK);
				move(DOWN);
				move(BACK_PRIME);

			} else if(cubePosition[GREEN][SIX] == RED) {
				move(FRONT_PRIME);
				move(DOWN);
				move(FRONT);

			} else if(cubePosition[GREEN][SIX] == ORANGE) {
				move(DOWN_PRIME);
				move(RIGHT);
				move(DOWN);
				move(RIGHT_PRIME);
			}
		}

		if(cubePosition[GREEN][SIX] == WHITE) {
			if(cubePosition[ORANGE][FOUR] == BLUE) {
				move(RIGHT);
				move(DOWN_PRIME);
				move(RIGHT_PRIME);

			} else if(cubePosition[ORANGE][FOUR] == GREEN) {
				move(DOWN);
				move(FRONT_PRIME);
				move(DOWN_PRIME);
				move(FRONT);

			} else if(cubePosition[ORANGE][FOUR] == RED) {
				move(FRONT);
				move(DOWN_TWO);
				move(FRONT_PRIME);

			} else if(cubePosition[ORANGE][FOUR] == ORANGE) {
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
			}
		}

		if(cubePosition[GREEN][FOUR] == WHITE) {
			if(cubePosition[RED][SIX] == BLUE) {
				move(RIGHT_PRIME);
				move(DOWN);
				move(RIGHT);

			} else if(cubePosition[RED][SIX] == GREEN) {
				move(DOWN);
				move(LEFT_PRIME);
				move(DOWN_TWO);
				move(LEFT);

			} else if(cubePosition[RED][SIX] == RED) {
				move(LEFT);
				move(DOWN);
				move(LEFT_PRIME);

			} else if(cubePosition[RED][SIX] == ORANGE) {
				move(BACK_PRIME);
				move(DOWN_TWO);
				move(BACK);
			}
		}

			//medium difficulty
		if(cubePosition[RED][ZERO] == WHITE) {
			if(cubePosition[WHITE][SIX] == BLUE) {
				move(FRONT_PRIME);
				move(RIGHT);
				move(DOWN_TWO);
				move(RIGHT_PRIME);
				move(FRONT);

			} else if(cubePosition[WHITE][SIX] == GREEN) {
				move(FRONT_PRIME);
				move(DOWN_PRIME);
				move(FRONT);
				move(DOWN);
				move(FRONT_PRIME);
				move(DOWN_PRIME);
				move(FRONT);

			} else if(cubePosition[WHITE][SIX] == RED) {
				move(FRONT_PRIME);
				move(DOWN_PRIME);
				move(FRONT_TWO);
				move(DOWN_TWO);
				move(FRONT_PRIME);

			} else if(cubePosition[WHITE][SIX] == ORANGE) {
				move(FRONT_PRIME);
				move(DOWN_PRIME);
				move(FRONT);
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
			}
		}

		if(cubePosition[RED][TWO] == WHITE) {
			if(cubePosition[WHITE][FOUR] == BLUE) {
				move(FRONT);
				move(DOWN);
				move(FRONT_PRIME);
				move(DOWN_PRIME);
				move(FRONT);
				move(DOWN);
				move(FRONT_PRIME);

			} else if(cubePosition[WHITE][FOUR] == GREEN) {
				move(FRONT);
				move(LEFT_PRIME);
				move(DOWN_TWO);
				move(LEFT);
				move(FRONT_PRIME);

			} else if(cubePosition[WHITE][FOUR] == RED) {
				move(FRONT);
				move(DOWN_TWO);
				move(FRONT_TWO);
				move(DOWN);
				move(FRONT);

			} else if(cubePosition[WHITE][FOUR] == ORANGE) {
				move(FRONT);
				move(BACK_PRIME);
				move(DOWN);
				move(FRONT_PRIME);
				move(BACK);
			}
		}

		if(cubePosition[BLUE][ZERO] == WHITE) {
			if(cubePosition[WHITE][FOUR] == BLUE) {
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT_TWO);
				move(DOWN_TWO);
				move(RIGHT_PRIME);

			} else if(cubePosition[WHITE][FOUR] == GREEN) {
				move(RIGHT_PRIME);
				move(LEFT);
				move(DOWN_PRIME);
				move(LEFT_PRIME);
				move(RIGHT);

			} else if(cubePosition[WHITE][FOUR] == RED) {
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT);
				move(DOWN);
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT);

			} else if(cubePosition[WHITE][FOUR] == ORANGE) {
				move(RIGHT_PRIME);
				move(BACK);
				move(DOWN_TWO);
				move(BACK_PRIME);
				move(RIGHT);
			}
		}

		if(cubePosition[BLUE][TWO] == WHITE) {
			if(cubePosition[WHITE][TWO] == BLUE) {
				move(RIGHT);
				move(DOWN_TWO);
				move(RIGHT_TWO);
				move(DOWN);
				move(RIGHT);

			} else if(cubePosition[WHITE][TWO] == GREEN) {
				move(RIGHT);
				move(LEFT_PRIME);
				move(DOWN);
				move(LEFT);
				move(RIGHT_PRIME);

			} else if(cubePosition[WHITE][TWO] == RED) {
				move(RIGHT);
				move(FRONT_PRIME);
				move(DOWN_TWO);
				move(FRONT);
				move(RIGHT_PRIME);

			} else if(cubePosition[WHITE][TWO] == ORANGE) {
				move(RIGHT);
				move(DOWN);
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT);
				move(DOWN);
				move(RIGHT_PRIME);
			}
		}

		if(cubePosition[ORANGE][ZERO] == WHITE) {
			if(cubePosition[WHITE][TWO] == BLUE) {
				move(BACK_PRIME);
				move(DOWN_PRIME);
				move(BACK);
				move(DOWN);
				move(BACK_PRIME);
				move(DOWN_PRIME);
				move(BACK);

			} else if(cubePosition[WHITE][TWO] == GREEN) {
				move(BACK_PRIME);
				move(LEFT);
				move(DOWN_TWO);
				move(LEFT_PRIME);
				move(BACK);

			} else if(cubePosition[WHITE][TWO] == RED) {
				move(BACK_PRIME);
				move(FRONT);
				move(DOWN_PRIME);
				move(FRONT_PRIME);
				move(BACK);

			} else if(cubePosition[WHITE][TWO] == ORANGE) {
				move(BACK_PRIME);
				move(DOWN_PRIME);
				move(BACK_TWO);
				move(DOWN_TWO);
				move(BACK_PRIME);
			}
		}

		if(cubePosition[ORANGE][TWO] == WHITE) {
			if(cubePosition[WHITE][ZERO] == BLUE) {
				move(BACK);
				move(RIGHT_PRIME);
				move(DOWN_TWO);
				move(RIGHT);
				move(BACK_PRIME);

			} else if(cubePosition[WHITE][ZERO] == GREEN) {
				move(BACK);
				move(DOWN);
				move(BACK_PRIME);
				move(DOWN_PRIME);
				move(BACK);
				move(DOWN);
				move(BACK_PRIME);

			} else if(cubePosition[WHITE][ZERO] == RED) {
				move(FRONT_PRIME);
				move(BACK);
				move(DOWN);
				move(FRONT);
				move(BACK_PRIME);

			} else if(cubePosition[WHITE][ZERO] == ORANGE) {
				move(BACK);
				move(DOWN_TWO);
				move(BACK_TWO);
				move(DOWN);
				move(BACK);
			}
		}

		if(cubePosition[GREEN][ZERO] == WHITE) {
			if(cubePosition[WHITE][ZERO] == BLUE) {
				move(LEFT_PRIME);
				move(RIGHT);
				move(DOWN_PRIME);
				move(LEFT);
				move(RIGHT_PRIME);

			} else if(cubePosition[WHITE][ZERO] == GREEN) {
				move(LEFT_PRIME);
				move(DOWN_TWO);
				move(LEFT_TWO);
				move(DOWN_PRIME);
				move(LEFT_PRIME);

			} else if(cubePosition[WHITE][ZERO] == RED) {
				move(LEFT_PRIME);
				move(FRONT);
				move(DOWN_TWO);
				move(LEFT);
				move(FRONT_PRIME);

			} else if(cubePosition[WHITE][ZERO] == ORANGE) {
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
				move(DOWN);
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
			}
		}

		if(cubePosition[GREEN][TWO] == WHITE) {
			if(cubePosition[WHITE][SIX] == BLUE) {
				move(LEFT);
				move(RIGHT_PRIME);
				move(DOWN);
				move(LEFT_PRIME);
				move(RIGHT);

			} else if(cubePosition[WHITE][SIX] == GREEN) {
				move(LEFT);
				move(DOWN_TWO);
				move(LEFT_TWO);
				move(DOWN);
				move(LEFT);

			} else if(cubePosition[WHITE][SIX] == RED) {
				move(LEFT);
				move(DOWN);
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
				move(DOWN);
				move(LEFT_PRIME);

			} else if(cubePosition[WHITE][SIX] == ORANGE) {
				move(LEFT);
				move(BACK_PRIME);
				move(DOWN_TWO);
				move(BACK);
				move(LEFT_PRIME);
			}
		}

			//hard difficulty
		if(cubePosition[YELLOW][ZERO] == WHITE) {
			if(cubePosition[RED][SIX] == BLUE) {
				move(DOWN_TWO);
				move(RIGHT);
				move(DOWN_TWO);
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT);
				move(DOWN);
				move(RIGHT_PRIME);

			} else if(cubePosition[RED][SIX] == GREEN) {
				move(LEFT);
				move(DOWN_TWO);
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
				move(DOWN);
				move(LEFT_PRIME);

			} else if(cubePosition[RED][SIX] == RED) {
				move(DOWN);
				move(RIGHT_PRIME);
				move(DOWN_TWO);
				move(RIGHT);
				move(DOWN);
				move(RIGHT_PRIME);
				move(DOWN_PRIME);
				move(RIGHT);

			} else if(cubePosition[RED][SIX] == ORANGE) {
				move(DOWN_PRIME);
				move(LEFT_PRIME);
				move(DOWN_TWO);
				move(LEFT);
				move(DOWN);
				move(LEFT_PRIME);
				move(DOWN_PRIME);
				move(LEFT);
			}
		}
		if(cubePosition[YELLOW][TWO] == WHITE) {
			move(DOWN_PRIME);
		}

		if(cubePosition[YELLOW][FOUR] == WHITE) {
			move(DOWN_TWO);
		}

		if(cubePosition[YELLOW][SIX] == WHITE) {
			move(DOWN);
		}

			//already on white
		if(cubePosition[WHITE][ZERO] == WHITE && cubePosition[GREEN][ZERO] != GREEN) {
			move(BACK);
			move(DOWN);
			move(BACK_PRIME);
		}

		if(cubePosition[WHITE][TWO] == WHITE && cubePosition[BLUE][TWO] != BLUE) {
			move(BACK_PRIME);
			move(DOWN_PRIME);
			move(BACK);
		}
		
		if(cubePosition[WHITE][SIX] == WHITE && cubePosition[RED][ZERO] != RED) {
			move(LEFT);
			move(DOWN);
			move(LEFT_PRIME);
		}
	}
	
	private void getEdges() {
		if(cubePosition[RED][FIVE] == BLUE && cubePosition[YELLOW][ONE] == RED) {
			move(DOWN_TWO);
			move(FRONT);
			move(DOWN);
			move(FRONT_PRIME);
			move(DOWN_PRIME);
			move(RIGHT_PRIME);
			move(DOWN_PRIME);
			move(RIGHT);
		}

		if(cubePosition[RED][FIVE] == RED && cubePosition[YELLOW][ONE] == BLUE) {
			move(DOWN_PRIME);
			move(RIGHT_PRIME);
			move(DOWN_PRIME);
			move(RIGHT);
			move(DOWN);
			move(FRONT);
			move(DOWN);
			move(FRONT_PRIME);
		}

		if(cubePosition[RED][FIVE] == RED && cubePosition[YELLOW][ONE] == GREEN) {
			move(DOWN);
			move(LEFT);
			move(DOWN);
			move(LEFT_PRIME);
			move(DOWN_PRIME);
			move(FRONT_PRIME);
			move(DOWN_PRIME);
			move(FRONT);
		}

		if(cubePosition[RED][FIVE] == GREEN && cubePosition[YELLOW][ONE] == RED) {
			move(DOWN_TWO);
			move(FRONT_PRIME);
			move(DOWN_PRIME);
			move(FRONT);
			move(DOWN);
			move(LEFT);
			move(DOWN);
			move(LEFT_PRIME);
		}

		if(cubePosition[RED][FIVE] == GREEN && cubePosition[YELLOW][ONE] == ORANGE) {
			move(BACK);
			move(DOWN);
			move(BACK_PRIME);
			move(DOWN_PRIME);
			move(LEFT_PRIME);
			move(DOWN_PRIME);
			move(LEFT);
		}

		if(cubePosition[RED][FIVE] == ORANGE && cubePosition[YELLOW][ONE] == GREEN) {
			move(DOWN);
			move(LEFT_PRIME);
			move(DOWN_PRIME);
			move(LEFT);
			move(DOWN);
			move(BACK);
			move(DOWN);
			move(BACK_PRIME);
		}

		if(cubePosition[RED][FIVE] == ORANGE && cubePosition[YELLOW][ONE] == BLUE) {
			move(DOWN_PRIME);
			move(RIGHT);
			move(DOWN);
			move(RIGHT_PRIME);
			move(DOWN_PRIME);
			move(BACK_PRIME);
			move(DOWN_PRIME);
			move(BACK);
		}

		if(cubePosition[RED][FIVE] == BLUE && cubePosition[YELLOW][ONE] == ORANGE) {
			move(BACK_PRIME);
			move(DOWN_PRIME);
			move(BACK);
			move(DOWN);
			move(RIGHT);
			move(DOWN);
			move(RIGHT_PRIME);
		}

		//bottom
		if(cubePosition[YELLOW][THREE] != YELLOW && cubePosition[BLUE][FIVE] != YELLOW) {
			move(DOWN_PRIME);
		}

		if(cubePosition[YELLOW][FIVE] != YELLOW && cubePosition[ORANGE][FIVE] != YELLOW) {
			move(DOWN_TWO);
		}

		if(cubePosition[YELLOW][SEVEN] != YELLOW && cubePosition[GREEN][FIVE] != YELLOW) {
			move(DOWN);
		}
	}
	
	//checks for flipped edges
	private void checkEdges() {
		if(cubePosition[RED][THREE] != RED || cubePosition[BLUE][SEVEN] != BLUE) {
			move(FRONT);
			move(DOWN);
			move(FRONT_PRIME);
			move(DOWN_PRIME);
			move(RIGHT_PRIME);
			move(DOWN_PRIME);
			move(RIGHT);
		}

		if(cubePosition[RED][SEVEN] != RED || cubePosition[GREEN][THREE] != GREEN) {
			move(LEFT);
			move(DOWN);
			move(LEFT_PRIME);
			move(DOWN_PRIME);
			move(FRONT_PRIME);
			move(DOWN_PRIME);
			move(FRONT);
		}
		
		if(cubePosition[GREEN][SEVEN] != GREEN || cubePosition[ORANGE][THREE] != ORANGE) {
			move(LEFT_PRIME);
			move(DOWN_PRIME);
			move(LEFT);
			move(DOWN);
			move(BACK);
			move(DOWN);
			move(BACK_PRIME);
		}
		
		if(cubePosition[BLUE][THREE] != BLUE || cubePosition[ORANGE][SEVEN] != ORANGE) {
			move(BACK_PRIME);
			move(DOWN_PRIME);
			move(BACK);
			move(DOWN);
			move(RIGHT);
			move(DOWN);
			move(RIGHT_PRIME);
		}
		
	}
	
	
	
	//solve yellow
	private void getYellow() {
		getYellowCross();
		getYellowCorners();
	}
	// algos for getYellow :

	private void adjCrossAlg () {
		move(FRONT);
		move(DOWN);
		move(LEFT);
		move(DOWN_PRIME);
		move(LEFT_PRIME);
		move(FRONT_PRIME);
	}

	private void oppoCrossAlg() {
		move(FRONT);
		move(LEFT);
		move(DOWN);
		move(LEFT_PRIME);
		move(DOWN_PRIME);
		move(FRONT_PRIME);
	}

	private void getYellowCross () {
		if(cubePosition[YELLOW][ONE] != YELLOW && 
		   cubePosition[YELLOW][THREE] != YELLOW &&
		   cubePosition[YELLOW][FIVE] != YELLOW &&
		   cubePosition[YELLOW][SEVEN] != YELLOW) {
			adjCrossAlg();
			move(DOWN);
			oppoCrossAlg();
		} else if(cubePosition[YELLOW][ONE] != YELLOW && 
		  		  cubePosition[YELLOW][THREE] == YELLOW &&
		   		  cubePosition[YELLOW][FIVE] != YELLOW &&
		   		  cubePosition[YELLOW][SEVEN] == YELLOW) {
			oppoCrossAlg();
		} else if(cubePosition[YELLOW][ONE] == YELLOW && 
		 		  cubePosition[YELLOW][THREE] != YELLOW &&
				  cubePosition[YELLOW][FIVE] == YELLOW &&
		 		  cubePosition[YELLOW][SEVEN] != YELLOW) {
			move(DOWN);
			oppoCrossAlg();
		} else if(cubePosition[YELLOW][ONE] != YELLOW && 
		 		  cubePosition[YELLOW][THREE] == YELLOW &&
		 		  cubePosition[YELLOW][FIVE] == YELLOW &&
		 		  cubePosition[YELLOW][SEVEN] != YELLOW) {
			adjCrossAlg();
		} else if(cubePosition[YELLOW][ONE] != YELLOW && 
		 		  cubePosition[YELLOW][THREE] != YELLOW &&
		 		  cubePosition[YELLOW][FIVE] == YELLOW &&
		 		  cubePosition[YELLOW][SEVEN] == YELLOW) {
			move(DOWN_PRIME);
			adjCrossAlg();
		} else if(cubePosition[YELLOW][ONE] == YELLOW && 
		  		  cubePosition[YELLOW][THREE] != YELLOW &&
		 		  cubePosition[YELLOW][FIVE] != YELLOW &&
		 		  cubePosition[YELLOW][SEVEN] == YELLOW) {
			move(DOWN_TWO);
			adjCrossAlg();
		} else if(cubePosition[YELLOW][ONE] == YELLOW && 
		 		  cubePosition[YELLOW][THREE] == YELLOW &&
		 		  cubePosition[YELLOW][FIVE] != YELLOW &&
		 		  cubePosition[YELLOW][SEVEN] != YELLOW) {
			move(DOWN);
			adjCrossAlg();
		}
	}
	
	private void getYellowCorners() {
		int yellowCase = -1; //undetermined case
		yellowCase = checkYellowCases();
		while(yellowCase == -1) {
			move(DOWN);
			if(numberOfTurns > BREAK_POINT) break;
			yellowCase = checkYellowCases();
		}
		finishSolvingYellow(yellowCase);
	}

	//8 cases
	private int checkYellowCases() {
		//antisune
		if(cubePosition[YELLOW][ZERO] != YELLOW && cubePosition[YELLOW][TWO] == YELLOW &&
		   cubePosition[YELLOW][FOUR] != YELLOW && cubePosition[YELLOW][SIX] != YELLOW &&
		   cubePosition[RED][SIX] == YELLOW) {
			return 0;
		}
		//sune
		if(cubePosition[YELLOW][ZERO] == YELLOW && cubePosition[YELLOW][TWO] != YELLOW &&
		   cubePosition[YELLOW][FOUR] != YELLOW && cubePosition[YELLOW][SIX] != YELLOW &&
		   cubePosition[RED][FOUR] == YELLOW) {
			return 1;
		}
		//double sune
		if(cubePosition[YELLOW][ZERO] != YELLOW && cubePosition[YELLOW][TWO] != YELLOW &&
		   cubePosition[YELLOW][FOUR] != YELLOW && cubePosition[YELLOW][SIX] != YELLOW &&
		   cubePosition[GREEN][FOUR] == YELLOW && cubePosition[BLUE][FOUR] == YELLOW) {
			return 2;
		}
		//other zero corner case
		if(cubePosition[YELLOW][ZERO] != YELLOW && cubePosition[YELLOW][TWO] != YELLOW &&
		   cubePosition[YELLOW][FOUR] != YELLOW && cubePosition[YELLOW][SIX] != YELLOW &&
		   cubePosition[GREEN][FOUR] == YELLOW && cubePosition[RED][FOUR] == YELLOW) {
			return 3;
		}
		//headlights case
		if(cubePosition[YELLOW][ZERO] != YELLOW && cubePosition[YELLOW][TWO] != YELLOW &&
		   cubePosition[YELLOW][FOUR] == YELLOW && cubePosition[YELLOW][SIX] == YELLOW &&
		   cubePosition[RED][FOUR] == YELLOW && cubePosition[RED][SIX] == YELLOW) {
			return 4;
		}
		//other two adjacent corner case
		if(cubePosition[YELLOW][ZERO] != YELLOW && cubePosition[YELLOW][TWO] != YELLOW &&
		   cubePosition[YELLOW][FOUR] == YELLOW && cubePosition[YELLOW][SIX] == YELLOW &&
		   cubePosition[GREEN][FOUR] == YELLOW && cubePosition[BLUE][SIX] == YELLOW) {
			return 5;
		}
		//two corner opposite case
		if(cubePosition[YELLOW][ZERO] == YELLOW && cubePosition[YELLOW][TWO] != YELLOW &&
		   cubePosition[YELLOW][FOUR] == YELLOW && cubePosition[YELLOW][SIX] != YELLOW &&
		   cubePosition[RED][FOUR] == YELLOW && cubePosition[GREEN][SIX] == YELLOW) {
			return 6;
		}
		//OLL skip
		if(cubePosition[YELLOW][ZERO] == YELLOW && cubePosition[YELLOW][TWO] == YELLOW &&
		   cubePosition[YELLOW][FOUR] == YELLOW && cubePosition[YELLOW][SIX] == YELLOW) {
			return 7;
		}
		return -1;
	}

	private void finishSolvingYellow(int yellowCase) {
		switch(yellowCase) {
			case 0: antisune();
					break;
			case 1: sune();
					break;
			case 2: doubleSune();
					break;
			case 3: zeroCorners();
					break;
			case 4: headlightCorners();
					break;
			case 5: otherAdjacentCornerCase();
					break;
			case 6: oppositeCornerCase();
			case 7:
		}
	}

	private void antisune() {
		move(LEFT);
		move(DOWN);
		move(LEFT_PRIME);
		move(DOWN);
		move(LEFT);
		move(DOWN_TWO);
		move(LEFT_PRIME);
	}

	private void sune() {
		move(RIGHT_PRIME);
		move(DOWN_PRIME);
		move(RIGHT);
		move(DOWN_PRIME);
		move(RIGHT_PRIME);
		move(DOWN_TWO);
		move(RIGHT);
	}

	private void doubleSune() {
		move(RIGHT_PRIME);
		move(DOWN_PRIME);
		move(RIGHT);
		move(DOWN_PRIME);
		move(RIGHT_PRIME);
		move(DOWN);
		move(RIGHT);
		move(DOWN_PRIME);
		move(RIGHT_PRIME);
		move(DOWN_TWO);
		move(RIGHT);
	}

	private void zeroCorners() {
		move(RIGHT_PRIME);
		move(DOWN_TWO);
		move(RIGHT_TWO);
		move(DOWN);
		move(RIGHT_TWO);
		move(DOWN);
		move(RIGHT_TWO);
		move(DOWN_TWO);
		move(RIGHT_PRIME);
	}

	private void headlightCorners() {
		move(LEFT_TWO);
		move(UP);
		move(LEFT_PRIME);
		move(DOWN_TWO);
		move(LEFT);
		move(UP_PRIME);
		move(LEFT_PRIME);
		move(DOWN_TWO);
		move(LEFT_PRIME);
	}

	private void otherAdjacentCornerCase() {
		move(BACK);
		move(DOWN);
		move(BACK_PRIME);
		move(DOWN_PRIME);
		move(FRONT_PRIME);
		move(DOWN);
		move(BACK);
		move(DOWN_PRIME);
		move(BACK_PRIME);
		move(FRONT);
	}

	private void oppositeCornerCase() {
		move(FRONT);
		move(LEFT_PRIME);
		move(FRONT_PRIME);
		move(RIGHT);
		move(FRONT);
		move(LEFT);
		move(FRONT_PRIME);
		move(RIGHT_PRIME);
	}
	
	
	//PLL
	private void finishSolve() {
		if(allCornersSolved()) {
			solveAllCornersCases();
		} else if(twoCornersSolved()) {
			solveTwoCornersCases();
		} else {
			solveNoCornersCases();
		}
	}

	private boolean allCornersSolved() {
		if(cubePosition[RED][FOUR] == cubePosition[RED][SIX] &&
		   cubePosition[BLUE][FOUR] == cubePosition[BLUE][SIX] &&
		   cubePosition[ORANGE][FOUR] == cubePosition[ORANGE][SIX] &&
		   cubePosition[GREEN][FOUR] == cubePosition[GREEN][SIX]) {
			return true;
		}
		return false;
	}

	private void solveAllCornersCases() {
		boolean isSolved = false;
		isSolved = allCornerTestColor(RED, ORANGE, GREEN, BLUE);
		if(!isSolved) {
			isSolved = allCornerTestColor(ORANGE, RED, BLUE, GREEN);
		}
		if(!isSolved) {
			isSolved = allCornerTestColor(GREEN, BLUE, ORANGE, RED);
		}
		if(!isSolved) {
			isSolved = allCornerTestColor(BLUE, GREEN, RED, ORANGE);
		}
		if(!isSolved) {
			printError();
		}
	}

	private boolean allCornerTestColor(int colorOne, int oppColor,
			int leftColor, int rightColor) {
		//setup
		while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
			move(DOWN_PRIME);
			if(numberOfTurns > BREAK_POINT) break;
		}

		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
			return true;
		}

		//Ua case
		if(cubePosition[colorOne][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[colorOne][ZERO]) {
			moveColorToFront(colorOne);
			doUaAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Ub
		if(cubePosition[colorOne][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[colorOne][ZERO]) {
			moveColorToFront(colorOne);
			doUbAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//H
		if(cubePosition[colorOne][FIVE] == cubePosition[oppColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[rightColor][ZERO]) {
			doHAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Z
		if(cubePosition[colorOne][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[oppColor][ZERO] &&
				cubePosition[oppColor][FIVE] == cubePosition[rightColor][ZERO]) {
			moveColorToFront(colorOne);
			doZAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}
		return false;
	}

	private boolean twoCornersSolved() {
		if(cubePosition[RED][FOUR] == cubePosition[RED][SIX] ||
		   cubePosition[BLUE][FOUR] == cubePosition[BLUE][SIX] ||
		   cubePosition[ORANGE][FOUR] == cubePosition[ORANGE][SIX] ||
		   cubePosition[GREEN][FOUR] == cubePosition[GREEN][SIX]) {
			return true;
		}
		return false;
	}
	
	private void solveTwoCornersCases() {
		boolean isSolved = false;
		isSolved = twoCornerTestColor(RED, ORANGE, GREEN, BLUE);
		if(!isSolved) {
			isSolved = twoCornerTestColor(ORANGE, RED, BLUE, GREEN);
		}
		if(!isSolved) {
			isSolved = twoCornerTestColor(GREEN, BLUE, ORANGE, RED);
		}
		if(!isSolved) {
			isSolved = twoCornerTestColor(BLUE, GREEN, RED, ORANGE);
		}
		if(!isSolved) {
			printError();
		}
	}
	
	private boolean twoCornerTestColor(int colorOne, int oppColor,
			int leftColor, int rightColor) {
		//setup
		while(cubePosition[colorOne][SIX] != cubePosition[colorOne][FOUR]) {
			move(DOWN_PRIME);
			if(numberOfTurns > BREAK_POINT) break;
		}

		//Aa
		if(cubePosition[colorOne][SIX] == cubePosition[colorOne][ZERO] &&
				cubePosition[colorOne][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[leftColor][SIX] == cubePosition[leftColor][FIVE] &&
				cubePosition[leftColor][SIX] == cubePosition[oppColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[colorOne][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN_TWO);
			doAaAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Ab
		if(cubePosition[colorOne][SIX] == cubePosition[colorOne][ZERO] &&
				cubePosition[colorOne][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[rightColor][FOUR] == cubePosition[rightColor][FIVE] &&
				cubePosition[rightColor][FIVE] == cubePosition[oppColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[colorOne][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN_TWO);
			doAbAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//F
		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][SIX] &&
				cubePosition[colorOne][FIVE] == cubePosition[colorOne][FOUR] &&
				cubePosition[leftColor][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[leftColor][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN);
			doFAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Ga
		if(cubePosition[colorOne][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[oppColor][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[leftColor][SIX] == cubePosition[oppColor][ZERO] &&
				cubePosition[rightColor][FOUR] == cubePosition[rightColor][FIVE]) {
			moveColorToFront(colorOne);
			move(DOWN_PRIME);
			doGaAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Gb
		if(cubePosition[colorOne][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[oppColor][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[leftColor][SIX] == cubePosition[oppColor][ZERO] &&
				cubePosition[leftColor][SIX] == cubePosition[leftColor][FIVE]) {
			moveColorToFront(colorOne);
			move(DOWN);
			doGbAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Gc
		if(cubePosition[colorOne][FIVE] == cubePosition[oppColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[oppColor][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[oppColor][FOUR] == cubePosition[oppColor][FIVE]) {
			moveColorToFront(colorOne);
			move(DOWN_TWO);
			doGcAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Gd
		if(cubePosition[colorOne][FIVE] == cubePosition[oppColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[oppColor][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[oppColor][SIX] == cubePosition[oppColor][FIVE]) {
			moveColorToFront(colorOne);
			move(DOWN_TWO);
			doGdAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Ja
		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][FOUR] &&
				cubePosition[leftColor][FIVE] == cubePosition[leftColor][SIX] &&
				cubePosition[leftColor][FIVE] == cubePosition[oppColor][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN);
			doJaAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Jb
		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][FOUR] &&
				cubePosition[rightColor][FIVE] == cubePosition[rightColor][FOUR] &&
				cubePosition[rightColor][FIVE] == cubePosition[oppColor][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN_PRIME);
			doJbAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Ra
		if(cubePosition[colorOne][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[colorOne][SIX] == cubePosition[colorOne][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
			moveColorToFront(colorOne);
			doRaAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Rb
		if(cubePosition[colorOne][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[colorOne][SIX] == cubePosition[colorOne][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[colorOne][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO]) {
			moveColorToFront(colorOne);
			doRbAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//T
		if(cubePosition[colorOne][FIVE] == cubePosition[oppColor][ZERO] &&
				cubePosition[colorOne][SIX] == cubePosition[colorOne][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN);
			doTAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}
		return false;
	}


	private void solveNoCornersCases() {
		boolean isSolved = false;
		isSolved = noCornerTestColor(RED, ORANGE, GREEN, BLUE);
		if(!isSolved) {
			isSolved = noCornerTestColor(ORANGE, RED, BLUE, GREEN);
		}
		if(!isSolved) {
			isSolved = noCornerTestColor(GREEN, BLUE, ORANGE, RED);
		}
		if(!isSolved) {
			isSolved = noCornerTestColor(BLUE, GREEN, RED, ORANGE);
		}
		if(!isSolved) {
			printError();
		}
	}


	private boolean noCornerTestColor(int colorOne, int oppColor,
			int leftColor, int rightColor) {
		//setup
		while(cubePosition[colorOne][FIVE] != cubePosition[colorOne][ZERO]) {
			move(DOWN_PRIME);
			if(numberOfTurns > BREAK_POINT) break;
		}

		//E
		if(cubePosition[colorOne][FOUR] == cubePosition[rightColor][ZERO] &&
				cubePosition[colorOne][SIX] == cubePosition[leftColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[oppColor][FIVE] == cubePosition[oppColor][ZERO]) {
			moveColorToFront(colorOne);
			doEAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Na
		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][SIX] &&
				cubePosition[colorOne][FOUR] == cubePosition[oppColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[rightColor][ZERO] &&
				cubePosition[leftColor][SIX] == cubePosition[leftColor][FIVE] &&
				cubePosition[leftColor][FOUR] == cubePosition[leftColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[leftColor][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN);
			doNaAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Nb
		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][FOUR] &&
				cubePosition[colorOne][SIX] == cubePosition[oppColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[leftColor][ZERO] &&
				cubePosition[rightColor][FOUR] == cubePosition[leftColor][ZERO] &&
				cubePosition[leftColor][SIX] == cubePosition[leftColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[leftColor][ZERO]) {
			moveColorToFront(colorOne);
			move(DOWN);
			doNbAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//V
		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][FOUR] &&
				cubePosition[colorOne][SIX] == cubePosition[oppColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[oppColor][ZERO] &&
				cubePosition[rightColor][FIVE] == cubePosition[rightColor][SIX] &&
				cubePosition[leftColor][SIX] == cubePosition[leftColor][ZERO]) {
			moveColorToFront(colorOne);
			doVAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}

		//Y
		if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][FOUR] &&
				cubePosition[colorOne][SIX] == cubePosition[oppColor][ZERO] &&
				cubePosition[leftColor][FIVE] == cubePosition[leftColor][SIX] &&
				cubePosition[leftColor][FOUR] == cubePosition[rightColor][ZERO]) {
			moveColorToFront(colorOne);
			doYAlg();
			while(cubePosition[colorOne][FOUR] != cubePosition[colorOne][ZERO]) {
				move(DOWN_PRIME);
			}
			if(cubePosition[colorOne][FIVE] == cubePosition[colorOne][ZERO] &&
					cubePosition[leftColor][FIVE] == cubePosition[leftColor][ZERO] &&
					cubePosition[rightColor][FIVE] == cubePosition[rightColor][ZERO]) {
				return true;
			}
		}
		return false;
	}

	private void moveColorToFront(int colorToFront) {
		switch(colorToFront) {
			case ORANGE: move(DOWN_TWO);
						break;
			case GREEN: move(DOWN);
						break;
			case BLUE: move(DOWN_PRIME);
					   break;
			default:
		}
	}

	private void doAaAlg() {
		turns += "Aa[";
		invertMove(RIGHT_PRIME);
		invertMove(FRONT);
		invertMove(RIGHT_PRIME);
		invertMove(BACK_TWO);
		invertMove(RIGHT);
		invertMove(FRONT_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(BACK_TWO);
		invertMove(RIGHT_TWO);
		turns += "] ";
	}

	private void doAbAlg() {
		turns += "Ab[";
		invertMove(LEFT);
		invertMove(FRONT_PRIME);
		invertMove(LEFT);
		invertMove(BACK_TWO);
		invertMove(LEFT_PRIME);
		invertMove(FRONT);
		invertMove(LEFT);
		invertMove(BACK_TWO);
		invertMove(LEFT_TWO);
		turns += "] ";
	}

	private void doEAlg() {
		turns += "E[";
		move(RIGHT);
		move(FRONT_PRIME);
		move(RIGHT_PRIME);
		move(BACK);
		move(RIGHT);
		move(FRONT);
		move(RIGHT_PRIME);
		move(BACK_PRIME);
		move(RIGHT);
		move(FRONT);
		move(RIGHT_PRIME);
		move(BACK);
		move(RIGHT);
		move(FRONT_PRIME);
		move(RIGHT_PRIME);
		move(BACK_PRIME);
		turns += "] ";
	}

	private void doFAlg() {
		turns += "F[";
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(FRONT_PRIME);
		doTAlg();
		invertMove(FRONT);
		invertMove(UP);
		invertMove(RIGHT);
		
		turns += "] ";
	}

	private void doGaAlg() {
		turns += "Ga[";
		invertMove(LEFT_TWO);
		invertMove(DOWN_PRIME);
		invertMove(BACK);
		invertMove(UP_PRIME);
		invertMove(BACK);
		invertMove(UP);
		invertMove(BACK_PRIME);
		invertMove(DOWN);
		invertMove(LEFT_TWO);
		invertMove(FRONT);
		invertMove(UP_PRIME);
		invertMove(FRONT_PRIME);
		turns += "] ";
	}

	private void doGbAlg() {
		turns += "Gb[";
		invertMove(RIGHT_TWO);
		invertMove(DOWN);
		invertMove(BACK_PRIME);
		invertMove(UP);
		invertMove(BACK_PRIME);
		invertMove(UP_PRIME);
		invertMove(BACK);
		invertMove(DOWN_PRIME);
		invertMove(RIGHT_TWO);
		invertMove(FRONT_PRIME);
		invertMove(UP);
		invertMove(FRONT);
		turns += "] ";
	}

	private void doGcAlg() {
		turns += "Gc[";
		invertMove(FRONT);
		invertMove(UP);
		invertMove(FRONT_PRIME);
		invertMove(LEFT_TWO);
		invertMove(DOWN_PRIME);
		invertMove(BACK);
		invertMove(UP_PRIME);
		invertMove(BACK_PRIME);
		invertMove(UP);
		invertMove(BACK_PRIME);
		invertMove(DOWN);
		invertMove(LEFT_TWO);
		turns += "] ";
	}

	private void doGdAlg() {
		turns += "Gd[";
		invertMove(FRONT_PRIME);
		invertMove(UP_PRIME);
		invertMove(FRONT);
		invertMove(RIGHT_TWO);
		invertMove(DOWN);
		invertMove(BACK_PRIME);
		invertMove(UP);
		invertMove(BACK);
		invertMove(UP_PRIME);
		invertMove(BACK);
		invertMove(DOWN_PRIME);
		invertMove(RIGHT_TWO);
		turns += "] ";
	}

	private void doHAlg() {
		turns += "H[";
		invertMove(RIGHT);
		invertMove(LEFT);
		invertMove(UP_TWO);
		invertMove(RIGHT_PRIME);
		invertMove(LEFT_PRIME);
		invertMove(FRONT_PRIME);
		invertMove(BACK_PRIME);
		invertMove(UP_TWO);
		invertMove(FRONT);
		invertMove(BACK);
		turns += "] ";
	}

	private void doJaAlg() {
		turns += "Ja[";
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT_PRIME);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT);
		invertMove(RIGHT_TWO);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		turns += "] ";
	}

	private void doJbAlg() {
		turns += "Jb[";
		invertMove(LEFT_PRIME);
		invertMove(UP_PRIME);
		invertMove(LEFT);
		invertMove(FRONT);
		invertMove(LEFT_PRIME);
		invertMove(UP_PRIME);
		invertMove(LEFT);
		invertMove(UP);
		invertMove(LEFT);
		invertMove(FRONT_PRIME);
		invertMove(LEFT_TWO);
		invertMove(UP);
		invertMove(LEFT);
		turns += "] ";
	}

	private void doNaAlg() {
		turns += "Na[";
		invertMove(RIGHT);
		invertMove(UP_PRIME);
		invertMove(LEFT);
		invertMove(UP_TWO);
		invertMove(RIGHT_PRIME);
		invertMove(UP);
		invertMove(RIGHT);
		invertMove(LEFT_PRIME);
		invertMove(UP_PRIME);
		invertMove(LEFT);
		invertMove(UP_TWO);
		invertMove(RIGHT_PRIME);
		invertMove(UP);
		invertMove(LEFT_PRIME);
		turns += "] ";
	}

	private void doNbAlg() {
		turns += "Nb[";
		invertMove(LEFT_PRIME);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_TWO);
		invertMove(LEFT);
		invertMove(UP_PRIME);
		invertMove(LEFT_PRIME);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_TWO);
		invertMove(LEFT);
		invertMove(UP_PRIME);
		invertMove(RIGHT);
		turns += "] ";
	}

	private void doRaAlg() {
		turns += "Ra[";
		invertMove(RIGHT_PRIME);
		invertMove(UP_TWO);
		invertMove(RIGHT);
		invertMove(UP_TWO);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT_PRIME);
		invertMove(RIGHT_TWO);
		turns += "] ";
	}

	private void doRbAlg() {
		turns += "Rb[";
		invertMove(LEFT);
		invertMove(UP_TWO);
		invertMove(LEFT_PRIME);
		invertMove(UP_TWO);
		invertMove(LEFT);
		invertMove(FRONT_PRIME);
		invertMove(LEFT_PRIME);
		invertMove(UP_PRIME);
		invertMove(LEFT);
		invertMove(UP);
		invertMove(LEFT);
		invertMove(FRONT);
		invertMove(LEFT_TWO);
		turns += "] ";
	}

	private void doTAlg() {
		turns += "T[";
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT);
		invertMove(RIGHT_TWO);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT_PRIME);
		turns += "] ";
	}

	private void doUaAlg() {
		turns += "Ua[";
		invertMove(RIGHT);
		invertMove(UP_PRIME);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT_TWO);
		turns += "] ";
	}

	private void doUbAlg() {
		turns += "Ub[";
		invertMove(LEFT_PRIME);
		invertMove(UP);
		invertMove(LEFT_PRIME);
		invertMove(UP_PRIME);
		invertMove(LEFT_PRIME);
		invertMove(UP_PRIME);
		invertMove(LEFT_PRIME);
		invertMove(UP);
		invertMove(LEFT);
		invertMove(UP);
		invertMove(LEFT_TWO);
		turns += "] ";
	}

	private void doVAlg() {
		turns += "V[";
		invertMove(RIGHT_PRIME);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(BACK_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(BACK_TWO);
		invertMove(UP_PRIME);
		invertMove(BACK_PRIME);
		invertMove(UP);
		invertMove(BACK_PRIME);
		invertMove(RIGHT);
		invertMove(BACK);
		invertMove(RIGHT);
		turns += "] ";
	}

	private void doYAlg() {
		turns += "Y[";
		invertMove(FRONT);
		invertMove(RIGHT);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT_PRIME);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(FRONT);
		invertMove(RIGHT);
		invertMove(FRONT_PRIME);
		turns += "] ";
	}

	private void doZAlg() {
		turns += "Z[";
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT_TWO);
		invertMove(UP);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT_PRIME);
		invertMove(UP_PRIME);
		invertMove(RIGHT);
		invertMove(UP);
		invertMove(RIGHT);
		invertMove(UP_PRIME);
		invertMove(RIGHT);
		invertMove(UP_PRIME);
		invertMove(RIGHT_PRIME);
		invertMove(UP_TWO);
		turns += "] ";
	}







//Graphics Section:
	private void addGraphics() {
		initializeVertexMatrix();
		addAllButtons();
		addCube();
	}
	//add and delete buttons section
	private void addAllButtons() {
		addButton(getWidth() / 2, 
				  getHeight() - BUTTON_HEIGHT - BUTTON_DISPLACEMENT, "RandomScramble");
		addButton(getWidth() / 2 - BUTTON_WIDTH, 
				  getHeight() - BUTTON_HEIGHT - BUTTON_DISPLACEMENT, "EnterScramble");
		addButton(getWidth() / 2 - 2 * BUTTON_WIDTH, BUTTON_DISPLACEMENT, "HowToUse");
		addButton(getWidth() / 2 - BUTTON_WIDTH, BUTTON_DISPLACEMENT, "Print Matrix");
		addButton(getWidth() / 2, BUTTON_DISPLACEMENT, "Remove Matrix");
		addButton(getWidth() / 2 + BUTTON_WIDTH, BUTTON_DISPLACEMENT, "Solve Cube");
		createNewToggleButtons(MEDIUM_BUTTON_ID);
		addRotationButtons();
	}
	

	private void addButton(double xStart, double yStart, String message) {
		GRect box = new GRect(BUTTON_WIDTH, BUTTON_HEIGHT);
		add(box, xStart, yStart);
		GLabel label = new GLabel(message);
		double labelXStart = xStart + BUTTON_WIDTH / 2 - label.getWidth() / 2;
		add(label, labelXStart, yStart + (BUTTON_HEIGHT + label.getAscent())/ 2);
	}
	
	
	private void addToggleButton(double xPos, double yPos, String message, boolean filled) {
		GRect toggleButton = new GRect(TOGGLE_BUTTON_WIDTH, TOGGLE_BUTTON_WIDTH);
		toggleButton.setFilled(filled);
		add(toggleButton, xPos, yPos);
		GLabel label = new GLabel(message);
		double labelXStart = xPos - label.getWidth() - LABEL_DISPLACEMENT;
		if(getElementAt(labelXStart, yPos + label.getHeight() / 2) == null) {
			add(label, labelXStart, yPos + label.getHeight() / 2);
		}
	}
	
	
	private void addStepButton() {
		GRect nextStepButton = new GRect(3 * TOGGLE_BUTTON_WIDTH, 2 * TOGGLE_BUTTON_WIDTH);
		double nextStepButtonX = getWidth() - LABEL_DISPLACEMENT - 3 * TOGGLE_BUTTON_WIDTH;
		double nextStepButtonY = getHeight() / 3 - 3 * LABEL_DISPLACEMENT;
		add(nextStepButton, nextStepButtonX, nextStepButtonY);
		GLabel nextStepLabel = new GLabel("step");
		add(nextStepLabel, nextStepButtonX + 3 * TOGGLE_BUTTON_WIDTH / 2 - nextStepLabel.getWidth() / 2,
			nextStepButtonY + TOGGLE_BUTTON_WIDTH + nextStepLabel.getAscent() / 2);
	}
	
	private void deleteStepButton() {
		double nextStepButtonX = getWidth() - LABEL_DISPLACEMENT - 3 * TOGGLE_BUTTON_WIDTH;
		double nextStepButtonY = getHeight() / 3 - 3 * LABEL_DISPLACEMENT;
		GObject stepBox = getElementAt(nextStepButtonX, nextStepButtonY);
		GObject stepLabel = getElementAt(nextStepButtonX + 3 * TOGGLE_BUTTON_WIDTH / 2,
				nextStepButtonY + TOGGLE_BUTTON_WIDTH);
		if(stepBox != null) remove(stepBox);
		if(stepLabel != null) remove(stepLabel);
	}
	
	
	private void addLabels() {
		addButton(getWidth() / 2 - 2 * BUTTON_WIDTH, BUTTON_DISPLACEMENT, "HowToUse");
		makeHowToUseLabel(0, "Rubik's Cube turns are represented by the letters U D F B L and R.");
		makeHowToUseLabel(1, "U means Up face, D means Down, F means Front, B means Back, L means Left, and R means Right");
		makeHowToUseLabel(2, "-If the notation says \" U \", that means turn the up face clockwise.");
		makeHowToUseLabel(3, "-If the notation says \" U' \", that means turn the up face counter-clockwise");
		makeHowToUseLabel(4, "-If the notation says \" U2 \", that means turn the up face two rotations");
		makeHowToUseLabel(5, "-For this cube, the Red Face (the face with the red center piece) is always the Front, and the White Face is always the Up face");
		makeHowToUseLabel(7, "That's all you need to know about Rubik's Cube notation!");
	}
	
	private void makeHowToUseLabel(int lineNumber, String message) {
		GLabel howToUseLabel = new GLabel(message);
		add(howToUseLabel, LABEL_DISPLACEMENT, BUTTON_HEIGHT + 3 * LABEL_DISPLACEMENT + lineNumber * LABEL_DISPLACEMENT);
	}
	
	
	private void addRotationButtons() {
		int xStart = getWidth() / 2 + BUTTON_WIDTH + 4 * LABEL_DISPLACEMENT; 
		int yStart = getHeight() - LABEL_DISPLACEMENT - TURN_BUTTON_WIDTH;
		addTurnButton(xStart, yStart, "<");
		addTurnButton(xStart + TURN_BUTTON_WIDTH, yStart, "v");
		addTurnButton(xStart + TURN_BUTTON_WIDTH, yStart - TURN_BUTTON_WIDTH, "^");
		addTurnButton(xStart + 2 * TURN_BUTTON_WIDTH, yStart, ">");
	}
	
	
	private void removeHowToUseStuff() {
		remove(getElementAt(getWidth() / 2, getHeight() / 2));
		remove(getElementAt(getWidth() / 2 - 2 * BUTTON_WIDTH, BUTTON_DISPLACEMENT));
		remove(getElementAt(getWidth() / 2 - 3 * BUTTON_WIDTH / 2, BUTTON_DISPLACEMENT + BUTTON_HEIGHT / 2));
		for(int i = 0; i < 6; i++) {
			remove(getElementAt(LABEL_DISPLACEMENT, BUTTON_HEIGHT + 3 * LABEL_DISPLACEMENT + i * LABEL_DISPLACEMENT));
		}
		remove(getElementAt(LABEL_DISPLACEMENT, BUTTON_HEIGHT + 3 * LABEL_DISPLACEMENT + 7 * LABEL_DISPLACEMENT));
	}
	
	
	private void addAllTurnButtons() {
		addTurnButton(BUTTON_DISPLACEMENT, getHeight() / 4, "U");
		addTurnButton(BUTTON_DISPLACEMENT, getHeight() / 4 + TURN_BUTTON_WIDTH, "D");
		addTurnButton(BUTTON_DISPLACEMENT, getHeight() / 4 + 2 * TURN_BUTTON_WIDTH, "F");
		addTurnButton(BUTTON_DISPLACEMENT, getHeight() / 4 + 3 * TURN_BUTTON_WIDTH, "B");
		addTurnButton(BUTTON_DISPLACEMENT, getHeight() / 4 + 4 * TURN_BUTTON_WIDTH, "L");
		addTurnButton(BUTTON_DISPLACEMENT, getHeight() / 4 + 5 * TURN_BUTTON_WIDTH, "R");
		addTurnButton(BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH, getHeight() / 4, "U'");
		addTurnButton(BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH, getHeight() / 4 + TURN_BUTTON_WIDTH, "D'");
		addTurnButton(BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH, getHeight() / 4 + 2 * TURN_BUTTON_WIDTH, "F'");
		addTurnButton(BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH, getHeight() / 4 + 3 * TURN_BUTTON_WIDTH, "B'");
		addTurnButton(BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH, getHeight() / 4 + 4 * TURN_BUTTON_WIDTH, "L'");
		addTurnButton(BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH, getHeight() / 4 + 5 * TURN_BUTTON_WIDTH, "R'");
	}
	
	private void addTurnButton(int xPos, int yPos, String message) {
		GCompound turnButton = new GCompound();
		turnButton.add(new GRect(TURN_BUTTON_WIDTH, TURN_BUTTON_WIDTH));
		GLabel turnButtonLabel = new GLabel(message);
		turnButton.add(turnButtonLabel, (TURN_BUTTON_WIDTH - turnButtonLabel.getWidth()) / 2, 
					   (TURN_BUTTON_WIDTH + turnButtonLabel.getAscent()) / 2);
		add(turnButton, xPos, yPos);
	}
	
	private void removeTurnButtons() {
		for(int j = 0; j < 2; j++) {
			for(int i = 0; i < 6; i ++) {
				GObject oldTurnLabel = getElementAt(BUTTON_DISPLACEMENT + j * TURN_BUTTON_WIDTH,
													getHeight() / 4 + i * TURN_BUTTON_WIDTH);
				if(oldTurnLabel != null) remove(oldTurnLabel);
			}
		}
		userScramble = false;
	}
	
	
	private void createNewToggleButtons(int id) {
		if(id == 0) {
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 - LABEL_DISPLACEMENT, "Step by Step", true);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3, "Slow     ", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + LABEL_DISPLACEMENT, "Med     ", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + 2 * LABEL_DISPLACEMENT, "Fast     ", false);
		} else if(id == 1) {
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 - LABEL_DISPLACEMENT, "Step by Step", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3, "Slow     ", true);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + LABEL_DISPLACEMENT, "Med     ", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + 2 * LABEL_DISPLACEMENT, "Fast     ", false);
		} else if(id == 2) {
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 - LABEL_DISPLACEMENT, "Step by Step", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3, "Slow     ", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + LABEL_DISPLACEMENT, "Med     ", true);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + 2 * LABEL_DISPLACEMENT, "Fast     ", false);
		} else if(id == 3) {
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 - LABEL_DISPLACEMENT, "Step by Step", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3, "Slow     ", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + LABEL_DISPLACEMENT, "Med     ", false);
			addToggleButton(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + 2 * LABEL_DISPLACEMENT, "Fast     ", true);
		}
	}
	
	private void deleteOldToggleButtons() {
		remove(getElementAt(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 - LABEL_DISPLACEMENT));
		remove(getElementAt(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3));
		remove(getElementAt(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + LABEL_DISPLACEMENT));
		remove(getElementAt(getWidth() - LABEL_DISPLACEMENT, getHeight() / 3 + 2 * LABEL_DISPLACEMENT));
	}
	
	private void addPressSolveLabel() {
		GLabel pressSolve = new GLabel("Press 'Solve Cube' to Start!");
		add(pressSolve, getWidth() - LABEL_DISPLACEMENT - pressSolve.getWidth(), getHeight() / 3 - 4 * LABEL_DISPLACEMENT);
	}
	
	private void deletePressSolveLabel() {
		GObject oldPressSolve = getElementAt(getWidth() - 2 * LABEL_DISPLACEMENT, getHeight() / 3 - 4 * LABEL_DISPLACEMENT);
		if(oldPressSolve != null) remove(oldPressSolve);
	}
	
	
	//checks for every type of click
	public void mouseClicked(MouseEvent e) {
		if(solveCubeButtonPressed(e.getX(), e.getY())) { //checks for if the user presses solve
			if(!solving && !howToUse) {
				solveButtonPressed = true;
				removeTurnButtons();
				deleteOldScrambleLabel();
				deleteOldSolutionLabels();
				deleteOldNumberOfMovesLabel();
				deletePressSolveLabel();
				numberOfTurns = 0;
				turns = "Solution: ";
			}
		} else if(randomScrambleButtonPressed(e.getX(), e.getY())) { //checks for if the user presses scramble
			if(!solving && !howToUse) {
				deleteOldScrambleLabel();
				deleteOldSolutionLabels();
				deleteOldNumberOfMovesLabel();
				deleteError();
				numberOfTurns = 0;
				turns = "Solution: ";
				randomScramble();
				updateArray();
			}
		} else if(printMatrixPressed(e.getX(), e.getY())) { //checks for if the user presses printMatrix
			if(!howToUse) printArray();
		} else if(removeMatrixPressed(e.getX(), e.getY())) { // remove matrix
			if(!howToUse) deleteArray();
		} else if(slowButtonPressed(e.getX(), e.getY())) { // part of the toggle button group
			if(!howToUse) {
				stepByStep = false;
				nextMoveButtonClicked = true;
				deleteStepButton();
				pauseTime = SLOW_SPEED;
				deleteOldToggleButtons();
				deletePressSolveLabel();
				createNewToggleButtons(SLOW_BUTTON_ID);
			}
		} else if(mediumButtonPressed(e.getX(), e.getY())) { // also in the toggle group
			if(!howToUse) {
				stepByStep = false;
				nextMoveButtonClicked = true;
				deleteStepButton();
				pauseTime = REGULAR_SPEED;
				deleteOldToggleButtons();
				deletePressSolveLabel();
				createNewToggleButtons(MEDIUM_BUTTON_ID);
			}
		} else if(fastButtonPressed(e.getX(), e.getY())) { // toggle group
			if(!howToUse) {
				stepByStep = false;
				nextMoveButtonClicked = true;
				deleteStepButton();
				pauseTime = FAST_SPEED;
				deleteOldToggleButtons();
				deletePressSolveLabel();
				createNewToggleButtons(FAST_BUTTON_ID);
			}
		} else if(stepByStepButtonPressed(e.getX(), e.getY())) { //toggle group
			if(!howToUse) {
				stepByStep = true;
				pauseTime = 0;
				deleteOldToggleButtons();
				createNewToggleButtons(STEP_BY_STEP_ID);
				if(!solving) addPressSolveLabel();
				addStepButton();
			}
		} else if(stepButtonPressed(e.getX(), e.getY())) { 
			if(!howToUse) {
				nextMoveButtonClicked = true;
			}
		} else if(howToUseButtonClicked(e.getX(), e.getY())) {
			if(!solving) {	
				if(!howToUse) {
					howToUse = true;
					GRect canvas = new GRect(getWidth(), getHeight());
					canvas.setFilled(true);
					canvas.setColor(Color.WHITE);
					add(canvas);
					addLabels();
				} else {
					removeHowToUseStuff();
					howToUse = false;
				}
			}
		} else if(userScrambleButtonPressed(e.getX(), e.getY())) {
			if(!solving && !howToUse && !userScramble) {
				addAllTurnButtons();
				userScramble = true;
			} else if(userScramble) {
				removeTurnButtons();
			}
		} else if(!howToUse){
			checkTurnButtons(e.getX(), e.getY());
			checkRotationButtons(e.getX(), e.getY());
		}
	}
	
	private void checkRotationButtons(double mouseX, double mouseY) {
		int xStart = getWidth() / 2 + BUTTON_WIDTH + 4 * LABEL_DISPLACEMENT; 
		int yStart = getHeight() - LABEL_DISPLACEMENT - TURN_BUTTON_WIDTH;
		if(mouseX > xStart + TURN_BUTTON_WIDTH && mouseX < xStart + 2 * TURN_BUTTON_WIDTH &&
		   mouseY > yStart - TURN_BUTTON_WIDTH && mouseY < yStart) {
			posXRotation = true;
		} else if(mouseX > xStart + TURN_BUTTON_WIDTH && mouseX < xStart + 2 * TURN_BUTTON_WIDTH &&
				  mouseY > yStart && mouseY < yStart + TURN_BUTTON_WIDTH) {
			negXRotation = true;
		} else if(mouseX > xStart && mouseX < xStart + TURN_BUTTON_WIDTH &&
				  mouseY > yStart && mouseY < yStart + TURN_BUTTON_WIDTH) {
			posYRotation = true;
		} else if(mouseX > xStart + 2 * TURN_BUTTON_WIDTH && mouseX < xStart + 3 * TURN_BUTTON_WIDTH &&
				  mouseY > yStart && mouseY < yStart + TURN_BUTTON_WIDTH) {
			negYRotation = true;
		}
	}

	private void checkTurnButtons(double mouseX, double mouseY) {
		if(mouseX > BUTTON_DISPLACEMENT && mouseX < BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 && mouseY < getHeight() / 4 + TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(UP);
		} else if(mouseX > BUTTON_DISPLACEMENT && mouseX < BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  2 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(DOWN);
		} else if(mouseX > BUTTON_DISPLACEMENT && mouseX < BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 2 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  3 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(FRONT);
		} else if(mouseX > BUTTON_DISPLACEMENT && mouseX < BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 3 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  4 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(BACK);
		} else if(mouseX > BUTTON_DISPLACEMENT && mouseX < BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 4 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  5 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(LEFT);
		} else if(mouseX > BUTTON_DISPLACEMENT && mouseX < BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 5 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  6 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(RIGHT);
		} else if(mouseX > BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH && mouseX < BUTTON_DISPLACEMENT + 2 * TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 && mouseY < getHeight() / 4 + TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(UP_PRIME);
		} else if(mouseX > BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH && mouseX < BUTTON_DISPLACEMENT + 2 * TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  2 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(DOWN_PRIME);
		} else if(mouseX > BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH && mouseX < BUTTON_DISPLACEMENT + 2 * TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 2 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  3 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(FRONT_PRIME);
		} else if(mouseX > BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH && mouseX < BUTTON_DISPLACEMENT + 2 * TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 3 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  4 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(BACK_PRIME);
		} else if(mouseX > BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH && mouseX < BUTTON_DISPLACEMENT + 2 * TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 4 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  5 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(LEFT_PRIME);
		} else if(mouseX > BUTTON_DISPLACEMENT + TURN_BUTTON_WIDTH && mouseX < BUTTON_DISPLACEMENT + 2 * TURN_BUTTON_WIDTH &&
				mouseY > getHeight() / 4 + 5 * TURN_BUTTON_WIDTH && mouseY < getHeight() / 4 +  6 * TURN_BUTTON_WIDTH &&
				userScramble) {
			moveWithNoLabel(RIGHT_PRIME);
		}
	}

	private boolean userScrambleButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() / 2 - BUTTON_WIDTH && mouseX < getWidth() / 2 &&
		   mouseY < getHeight() - BUTTON_DISPLACEMENT && mouseY > getHeight() - BUTTON_DISPLACEMENT - BUTTON_HEIGHT) {
			return true;
		}
		return false;
	}
	
	private boolean howToUseButtonClicked(double mouseX, double mouseY) {
		if(mouseX > getWidth() / 2 - 2 * BUTTON_WIDTH && 
		   mouseX < getWidth() - BUTTON_WIDTH && 
		   mouseY > BUTTON_DISPLACEMENT && mouseY < BUTTON_DISPLACEMENT + BUTTON_HEIGHT) {
			return true;
		}
		return false;
	}
	
	private boolean stepButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() - LABEL_DISPLACEMENT - 3 * TOGGLE_BUTTON_WIDTH && 
		   mouseX < getWidth() - LABEL_DISPLACEMENT && 
		   mouseY > getHeight() / 3 - 3 * LABEL_DISPLACEMENT && 
		   mouseY < getHeight() / 3 - 3 * LABEL_DISPLACEMENT + 2 * TOGGLE_BUTTON_WIDTH) {
			return true;
		}
		return false;
	}
	
	private boolean stepByStepButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() - LABEL_DISPLACEMENT && 
		   mouseX < getWidth() - LABEL_DISPLACEMENT + TOGGLE_BUTTON_WIDTH && 
		   mouseY > getHeight() / 3 - LABEL_DISPLACEMENT && 
		   mouseY < getHeight() / 3 - LABEL_DISPLACEMENT + TOGGLE_BUTTON_WIDTH) {
			return true;
		}
		return false;
	}
	
	private boolean fastButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() - LABEL_DISPLACEMENT && 
		   mouseX < getWidth() - LABEL_DISPLACEMENT + TOGGLE_BUTTON_WIDTH && 
		   mouseY > getHeight() / 3 + 2 * LABEL_DISPLACEMENT && 
		   mouseY < getHeight() / 3 + 2 * LABEL_DISPLACEMENT + TOGGLE_BUTTON_WIDTH) {
					return true;
		}
		return false;
	}
	
	private boolean mediumButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() - LABEL_DISPLACEMENT && 
		   mouseX < getWidth() - LABEL_DISPLACEMENT + TOGGLE_BUTTON_WIDTH && 
		   mouseY > getHeight() / 3 + LABEL_DISPLACEMENT && 
		   mouseY < getHeight() / 3 + LABEL_DISPLACEMENT + TOGGLE_BUTTON_WIDTH) {
			return true;
		}
		return false;
	}
	
	private boolean slowButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() - LABEL_DISPLACEMENT && 
		   mouseX < getWidth() - LABEL_DISPLACEMENT + TOGGLE_BUTTON_WIDTH && mouseY > getHeight() / 3 && 
		   mouseY < getHeight() / 3 + TOGGLE_BUTTON_WIDTH) {
			return true;
		}
		return false;
	}
	
	private boolean removeMatrixPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() / 2 && mouseX < getWidth() / 2 + BUTTON_WIDTH &&
		   mouseY > BUTTON_DISPLACEMENT && mouseY < BUTTON_DISPLACEMENT + BUTTON_HEIGHT) {
			return true;
		}
		return false;
	}
	
	private boolean printMatrixPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() / 2 - BUTTON_WIDTH && mouseX < getWidth() / 2 &&
		   mouseY > BUTTON_DISPLACEMENT && mouseY < BUTTON_DISPLACEMENT + BUTTON_HEIGHT) {
			return true;
		}
		return false;
	}
	
	private boolean randomScrambleButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() / 2 && mouseX < getWidth() / 2 + BUTTON_WIDTH &&
			mouseY < getHeight() - BUTTON_DISPLACEMENT && mouseY > getHeight() - BUTTON_DISPLACEMENT - BUTTON_HEIGHT) {
			return true;
		}
		return false;
	}
	
	private boolean solveCubeButtonPressed(double mouseX, double mouseY) {
		if(mouseX > getWidth() / 2 + BUTTON_WIDTH && mouseX < getWidth() / 2 + 2 * BUTTON_WIDTH &&
		   mouseY < BUTTON_HEIGHT + BUTTON_DISPLACEMENT && mouseY > BUTTON_DISPLACEMENT) {
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	//projections/////

	private void addCube() {
		GObject oldCube = getElementAt(getWidth() / 2, getHeight() / 2);
		if(oldCube != null) remove(oldCube);
		makeInitialCube();
	}

	private void initializeVertexMatrix() {
		vertexMatrix[VERTEX_ZERO][X] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_ZERO][Y] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_ZERO][Z] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_ONE][X] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_ONE][Y] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_ONE][Z] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_TWO][X] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_TWO][Y] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_TWO][Z] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_THREE][X] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_THREE][Y] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_THREE][Z] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_FOUR][X] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_FOUR][Y] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_FOUR][Z] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_FIVE][X] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_FIVE][Y] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_FIVE][Z] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_SIX][X] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_SIX][Y] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_SIX][Z] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_SEVEN][X] = -1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_SEVEN][Y] = 1.5 * CUBIE_WIDTH;
		vertexMatrix[VERTEX_SEVEN][Z] = 1.5 * CUBIE_WIDTH;
	}

	private void makeInitialCube() {
		GCompound cube = new GCompound();
		int smallestZVertex = findSmallestVertex();
		if(whiteSideShowing(smallestZVertex))
			cube = makeSide(WHITE, VERTEX_ZERO, VERTEX_ONE, VERTEX_TWO, VERTEX_THREE, cube);
		if(greenSideShowing(smallestZVertex))
			cube = makeSide(GREEN, VERTEX_SIX, VERTEX_ONE, VERTEX_ZERO, VERTEX_SEVEN, cube);
		if(redSideShowing(smallestZVertex))
			cube = makeSide(RED, VERTEX_SEVEN, VERTEX_ZERO, VERTEX_THREE, VERTEX_FOUR, cube);
		if(yellowSideShowing(smallestZVertex))
			cube = makeSide(YELLOW, VERTEX_SIX, VERTEX_SEVEN, VERTEX_FOUR, VERTEX_FIVE, cube);
		if(blueSideShowing(smallestZVertex))
			cube = makeSide(BLUE, VERTEX_FOUR, VERTEX_THREE, VERTEX_TWO, VERTEX_FIVE, cube);
		if(orangeSideShowing(smallestZVertex))
			cube = makeSide(ORANGE, VERTEX_FIVE, VERTEX_TWO, VERTEX_ONE, VERTEX_SIX, cube);
		add(cube, getWidth() / 2, getHeight() / 2 + LABEL_DISPLACEMENT);
	}
	
	private boolean whiteSideShowing(int smallestZVertex) {
		return smallestZVertex >= 4 && smallestZVertex <= 7;
	}
	private boolean greenSideShowing(int smallestZVertex) {
		return smallestZVertex != 0 && smallestZVertex != 1 &&
				smallestZVertex != 6 && smallestZVertex != 7;
	}
	private boolean redSideShowing(int smallestZVertex) {
		return smallestZVertex != 0 && smallestZVertex != 3 &&
				smallestZVertex != 7 && smallestZVertex != 4;
	}
	private boolean yellowSideShowing(int smallestZVertex) {
		return smallestZVertex >= 0 && smallestZVertex <= 3;
	}
	private boolean blueSideShowing(int smallestZVertex) {
		return smallestZVertex != 2 && smallestZVertex != 3 &&
				smallestZVertex != 5 && smallestZVertex != 4;
	}
	private boolean orangeSideShowing(int smallestZVertex) {
		return smallestZVertex != 1 && smallestZVertex != 2 &&
				smallestZVertex != 6 && smallestZVertex != 5;
	}
	
	private int findSmallestVertex() {
		double smallestValue = 0;
		int smallestID = 0;
		for(int i = 0; i < 8; i++) {
			if(vertexMatrix[i][Z] < smallestValue) {
				smallestID = i;
				smallestValue = vertexMatrix[i][Z];
			}
		}
		return smallestID;
	}

	private GCompound makeSide(int colorID, int firstVertex, int secondVertex, 
							int thirdVertex, int fourthVertex, GCompound cube) {
		double xDifAB = (vertexMatrix[secondVertex][X] - vertexMatrix[firstVertex][X]) / 3;
		double yDifAB = (vertexMatrix[secondVertex][Y] - vertexMatrix[firstVertex][Y]) / 3;
		double xDifBC = (vertexMatrix[thirdVertex][X] - vertexMatrix[secondVertex][X]) / 3;
		double yDifBC = (vertexMatrix[thirdVertex][Y] - vertexMatrix[secondVertex][Y]) / 3;
		double[] pointA = new double[Y + 1];
		pointA[X] = vertexMatrix[firstVertex][X];
		pointA[Y] = vertexMatrix[firstVertex][Y];
		double[] pointB = new double[Y + 1];
		pointB[X] = vertexMatrix[firstVertex][X] + xDifAB;
		pointB[Y] = vertexMatrix[firstVertex][Y] + yDifAB;
		double[] pointC = new double[Y + 1];
		pointC[X] = vertexMatrix[firstVertex][X] + 2 * xDifAB;
		pointC[Y] = vertexMatrix[firstVertex][Y] + 2 * yDifAB;
		double[] pointD = new double[Y + 1];
		pointD[X] = vertexMatrix[secondVertex][X];
		pointD[Y] = vertexMatrix[secondVertex][Y];
		double[] pointE = new double[Y + 1];
		pointE[X] = vertexMatrix[firstVertex][X] + xDifBC;
		pointE[Y] = vertexMatrix[firstVertex][Y] + yDifBC;
		double[] pointF = new double[Y + 1];
		pointF[X] = vertexMatrix[firstVertex][X] + xDifAB + xDifBC;
		pointF[Y] = vertexMatrix[firstVertex][Y] + yDifAB + yDifBC;
		double[] pointG = new double[Y + 1];
		pointG[X] = vertexMatrix[firstVertex][X] + 2 * xDifAB + xDifBC;
		pointG[Y] = vertexMatrix[firstVertex][Y] + 2 * yDifAB + yDifBC;
		double[] pointH = new double[Y + 1];
		pointH[X] = vertexMatrix[secondVertex][X] + xDifBC;
		pointH[Y] = vertexMatrix[secondVertex][Y] + yDifBC;
		double[] pointI = new double[Y + 1];
		pointI[X] = vertexMatrix[firstVertex][X] + 2 * xDifBC;
		pointI[Y] = vertexMatrix[firstVertex][Y] + 2 * yDifBC;
		double[] pointJ = new double[Y + 1];
		pointJ[X] = vertexMatrix[firstVertex][X] + xDifAB + 2 * xDifBC;
		pointJ[Y] = vertexMatrix[firstVertex][Y] + yDifAB + 2 * yDifBC;
		double[] pointK = new double[Y + 1];
		pointK[X] = vertexMatrix[firstVertex][X] + 2 * xDifAB + 2 * xDifBC;
		pointK[Y] = vertexMatrix[firstVertex][Y] + 2 * yDifAB + 2 * yDifBC;
		double[] pointL = new double[Y + 1];
		pointL[X] = vertexMatrix[secondVertex][X] + 2 * xDifBC;
		pointL[Y] = vertexMatrix[secondVertex][Y] + 2 * yDifBC;
		double[] pointM = new double[Y + 1];
		pointM[X] = vertexMatrix[fourthVertex][X];
		pointM[Y] = vertexMatrix[fourthVertex][Y];
		double[] pointN = new double[Y + 1];
		pointN[X] = vertexMatrix[firstVertex][X] + xDifAB + 3 * xDifBC;
		pointN[Y] = vertexMatrix[firstVertex][Y] + yDifAB + 3 * yDifBC;
		double[] pointO = new double[Y + 1];
		pointO[X] = vertexMatrix[firstVertex][X] + 2 * xDifAB + 3 * xDifBC;
		pointO[Y] = vertexMatrix[firstVertex][Y] + 2 * yDifAB + 3 * yDifBC;
		double[] pointP = new double[Y + 1];
		pointP[X] = vertexMatrix[thirdVertex][X];
		pointP[Y] = vertexMatrix[thirdVertex][Y];
		GPolygon sticker0 = makePolygon(pointD, pointH, pointG, pointC, 0, colorID);
		GPolygon sticker1 = makePolygon(pointH, pointL, pointK, pointG, 1, colorID);
		GPolygon sticker2 = makePolygon(pointL, pointP, pointO, pointK, 2, colorID);
		GPolygon sticker3 = makePolygon(pointK, pointO, pointN, pointJ, 3, colorID);
		GPolygon sticker4 = makePolygon(pointJ, pointN, pointM, pointI, 4, colorID);
		GPolygon sticker5 = makePolygon(pointF, pointJ, pointI, pointE, 5, colorID);
		GPolygon sticker6 = makePolygon(pointB, pointF, pointE, pointA, 6, colorID);
		GPolygon sticker7 = makePolygon(pointC, pointG, pointF, pointB, 7, colorID);
		GPolygon sticker8 = makePolygon(pointG, pointK, pointJ, pointF, 8, colorID);
		cube.add(sticker0);
		cube.add(sticker1);
		cube.add(sticker2);
		cube.add(sticker3);
		cube.add(sticker4);
		cube.add(sticker5);
		cube.add(sticker6);
		cube.add(sticker7);
		cube.add(sticker8);
		return cube;
	}

	private GPolygon makePolygon(double[] point1, double[] point2, double[] point3,
								 double[] point4, int stickerID, int colorID) {
		GPolygon sticker = new GPolygon();
		sticker.addVertex(point1[X], point1[Y]);
		sticker.addVertex(point2[X], point2[Y]);
		sticker.addVertex(point3[X], point3[Y]);
		sticker.addVertex(point4[X], point4[Y]);
		sticker.setFilled(true);
		sticker.setFillColor(getFillColor(stickerID, colorID));
		return sticker;
	}

	private Color getFillColor(int stickerID, int colorID) {
		if(stickerID != 8) {
			int colorNumber = cubePosition[colorID][stickerID];
			switch(colorNumber) {
				case 0: return Color.WHITE;
				case 1: return Color.YELLOW;
				case 2: return Color.RED;
				case 3: return Color.ORANGE;
				case 4: return Color.GREEN;
				case 5: return Color.BLUE;
			}
			return Color.WHITE;
		} else {
			switch(colorID) {
				case 0: return Color.WHITE;
				case 1: return Color.YELLOW;
				case 2: return Color.RED;
				case 3: return Color.ORANGE;
				case 4: return Color.GREEN;
				case 5: return Color.BLUE;
			}
		}
		return Color.WHITE;
	}


	private void rotateOnX(double angle) {
		for(int i = 0; i < N_STEPS; i++) {
			setupMultiplicationForXRotation(angle);
			pause(ROTATION_PAUSE_TIME);
			addCube();
		}
	}
	
	private void setupMultiplicationForXRotation(double angle) {
		//matrix setup:
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		
		//actual multiplication:
		for(int i = 0; i < VERTEX_SEVEN + 1; i++) {
			double originalVertexMatrixY = vertexMatrix[i][Y];
			vertexMatrix[i][Y] = vertexMatrix[i][Y] * cos + vertexMatrix[i][Z] * sin;
			vertexMatrix[i][Z] = originalVertexMatrixY * -sin + vertexMatrix[i][Z] * cos;
		}
	}
	
	private void rotateOnY(double angle) {
		for(int i = -10; i < 10; i++) {
			setupMultiplicationForYRotation(angle);
			pause(ROTATION_PAUSE_TIME);
			addCube();
		}
	}
	
	private void setupMultiplicationForYRotation(double angle) {
		//matrix setup:
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		//actual multiplication:
		
		for(int i = 0; i < VERTEX_SEVEN + 1; i++) {
			double originalVertexMatrixX = vertexMatrix[i][X];
			vertexMatrix[i][X] = vertexMatrix[i][X] * cos + vertexMatrix[i][Z] * sin;
			vertexMatrix[i][Z] = originalVertexMatrixX * -sin + vertexMatrix[i][Z] * cos;
		}
	}

}


