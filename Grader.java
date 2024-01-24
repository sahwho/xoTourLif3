import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Grader {
	public static void main(String[] args) {
		
		double score = 0.0;
		
		for (int startRow = 0; startRow<8; startRow++) {
			for (int startCol = 0; startCol<8; startCol++) {
				ArrayList<ArrayList<Integer>> list = Tour.tour(startRow, startCol);
				double pointsForTour = pointsToMoves(visit(list, startRow, startCol));
				
				if (pointsForTour==0)
					System.out.println("0 point tour: " + list);
				
				System.out.println("score for tour at (" + startRow + "," + startCol + "): " + pointsForTour);
				score += pointsForTour;
			}
		}
		
		System.out.println("raw score: " + score);
		double finalScore = score > 64 ? 32.0 : score / 2.0;
		System.out.println("final score: " + finalScore);
	}
	
	//core logic of kt grading:
	
	//1. verify that list.get(0) is correct starting position
	//   since the starting position is passed in, don't need to
	//   explicitly check for correctness on board (0-7).
	
	//2. verify that every <r,c> pair in the arraylist is 
	//   inside the board
	
	//3. verify that every move is ({2, -2, -1, 1}, {2, -2, -1, 1})
	
	//4. after "seeing" a move, create a copy, put it in the "visited"
	//   arraylist. make sure to check !visited.contains(currMove), as
	//   it is invalid to visit a spot on the board twice.
	
	//5. after correctness checking is done: size of arraylist is number
	//   of moves, add points for that tour
	
	//6. check for closed tour by comparing list.get(list.size()-1) and list.get(0)
	
	// check that a move made from position m1 to position m2 is valid, according
	// to the definition of a valid move for a knight on a chessboard
	public static boolean isValidMove(ArrayList<Integer> m1, ArrayList<Integer> m2) {
		List<List<Integer>> validMoves = new ArrayList<List<Integer>>();
		
		validMoves.add(Arrays.asList(1, 2));
		validMoves.add(Arrays.asList(1, -2));
		validMoves.add(Arrays.asList(-1, 2));
		validMoves.add(Arrays.asList(-1, -2));
		validMoves.add(Arrays.asList(2, 1));
		validMoves.add(Arrays.asList(2, -1));
		validMoves.add(Arrays.asList(-2, 1));
		validMoves.add(Arrays.asList(-2, -1));
		
		//System.out.println(validMoves.contains(m1));
		
		if (m1.size()!=2 || m2.size()!=2)
			return false;
		
		int diff1 = m1.get(0)-m2.get(0);
		int diff2 = m1.get(1)-m2.get(1);
		
		ArrayList<Integer> diffList = new ArrayList<Integer>();
		diffList.add(diff1);
		diffList.add(diff2);
		
		return validMoves.contains(diffList);
	}
	
	// check that a given move is a valid position in an 8x8 chessboard,
	// meaning that each part of the tuple is between 0 and 7, inclusive.
	public static boolean isValidPosition(ArrayList<Integer> position) {
		if (position.size()!=2)
			return false;
		
		if (position.get(0) < 0 || position.get(0) > 7)
			return false;
		
		if (position.get(1) < 0 || position.get(1) > 7)
			return false;
		
		return true;
	}
	
	// check that the first move in a tour is what it should be -- the (row, col) tuple
	public static boolean isStartingPositionCorrect(ArrayList<ArrayList<Integer>> tour, int row, int col) {
		return tour.get(0).get(0)==row && tour.get(0).get(1)==col;
	}
	
	// given properties of an individual tour (number of moves, whether it's closed, whether it's valid),
	// return the corresponding point number for that tour. an invalid tour receives 0 points.
	// moves==-1 means the tour wasn't valid
	// moves==65 means a closed tour
	// otherwise, a value of moves between [0 and 64] will earn the associated number of points
	public static double pointsToMoves(int moves) {
		if(moves==-1)
			return 0;
		
		if (moves==65)
			return 1;
		
		else if (moves==64)
			return 1;
		
		else if (moves >= 58 && moves < 64)
			return 0.9;
		
		else if (moves >= 51 && moves < 58)
			return 0.8;
		
		else if (moves >= 45 && moves < 51)
			return 0.7;
		
		return 0.5;
	}

	// returns an int, indicating information related to the tour.
	// a return value of -1 means the tour wasn't valid
	// a return value of 65 means a full, closed tour
	// otherwise, a value between 0 and 64 indicates that number of moves
	public static int visit(ArrayList<ArrayList<Integer>> tour, int expectedStartRow, int expectedStartCol) {
		if (!isStartingPositionCorrect(tour, expectedStartRow, expectedStartCol))
			return -1;
		
		HashSet<ArrayList<Integer>> visited = new HashSet<ArrayList<Integer>>();
		
		//stop at the second to last position in the board, since we'll always compare the current move(i) to the next one (i+1)
		for (int i=0; i<tour.size()-1; i++) {
			if (!isValidPosition(tour.get(i)) || !isValidMove(tour.get(i), tour.get(i+1)))
				return -1;
			else { //the move from index i to index i+1 is valid; mark position i as visited and continue
				ArrayList<Integer> currentPosition = new ArrayList<Integer>();
				currentPosition.add(tour.get(i).get(0));
				currentPosition.add(tour.get(i).get(1));
				
				if (visited.contains(currentPosition)) {
					//we've already seen this move, so the tour is invalid
					return -1;
				} else {
					visited.add(currentPosition);
				}
			}
		}
		
		//because we stop one move early, we need to make sure the last move in the tour isn't already visited or invalid
		if (!isValidPosition(tour.get(tour.size()-1)) || visited.contains(tour.get(tour.size()-1)))
			return -1;
		
		//a full, closed tour
		if (tour.size()==64 && isValidMove(tour.get(tour.size()-1), tour.get(0))){
			return 65;
		}
		
		return tour.size();
	}
}
