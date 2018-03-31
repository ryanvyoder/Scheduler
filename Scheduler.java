/*
 * Ryan Yoder (2016-2017): Scheduler program (More accurately described as an Assignment Keeper)
 * This code creates a Scheduler using MinHeap.java and Node.java as created by Ryan Yoder for this program
 */
import java.util.*;
import java.time.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/*
	TODO:
	- Add e-mail capabilities
	- Add ability to specify a time for something to be due
		+ Alter the toString() for Nodes
		+ look at time when sorting?

*/

/**
* CLASS Scheduler:
* This class is the main driver program for the Assignment Keeper. It keeps track of dates and maintains a data structure which holds and sorts all of the
* assignments that the user has entered. It will output messages on startup related to how soon assignments are due.
*/
public class Scheduler {

	//Defining some different data which will be used for weekday and month mapping as well as declaring our File for loading and saving and the priority queue structure.
	static File inFile = null;
	static MinHeap pq = null;
	static String [] month = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
	static String [] weekdays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

	public static void main(String[] args){
		pq = new MinHeap();

		try{
			Load();
			pL(0);
		}
		catch(Exception e){
			System.out.println("ERROR LOADING FILE");
			pL(0);
		}

		//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("dd, yyyy");
		DateFormat monthFormat = new SimpleDateFormat("MM");
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		System.out.println("Hello! Welcome back. Today is " + weekdays[dayOfWeek - 1] + ", " + (month[Integer.parseInt(monthFormat.format(date)) - 1]) + " " + dateFormat.format(date) + ".");  //2016/11/16 12:08:43

		pL(0);
		Node soon = pq.peek();
		double daysLeft = daysBetween(date, soon.getDate());

		//System.out.println(daysLeft);

		if(daysLeft <= 5 && daysLeft > 1){
			System.out.println("/////WARNING: Your next assignment, \"" + soon.getAssignment() + "\" is due in just " + ((int)(daysLeft)) + " days!\\\\\\\\\\");
		}
		else if(daysLeft == 1){
			System.out.println("/////WARNING: Your assignment, \"" + soon.getAssignment() +  "\" is due tomorrow!\\\\\\\\\\");
		}
		else if(daysLeft  == 0){
			System.out.println("/////WARNING: Your assignment, \"" + soon.getAssignment() + "\" is due today!\\\\\\\\\\");
		}
		else if(daysLeft < 0){
			Node[] temp = new Node[pq.getNumNodes()];
			int i = 0;
			while(daysLeft <= -1 && soon != null){
				System.out.println("Your assignment, \"" + soon.getAssignment() + "\" is overdue. Would you like me to remove it? (y/n)");
				pL(0);
				Scanner s = new Scanner(System.in);
				String input = s.nextLine();
				if(input.equalsIgnoreCase("y")){
					removeTop(0);
					soon = pq.peek();
					daysLeft = daysBetween(date, soon.getDate());
				}
				else{
					// gets stuck in a loop
					temp[i] = new Node(soon);
					removeTop(1);
					soon = pq.peek();
					if(soon != null){
						daysLeft = daysBetween(date, soon.getDate());
					}
					i++;
				}
				pL(0);
			}
			if(daysLeft <= 5 && daysLeft > 1){
				System.out.println("/////WARNING: Your next assignment, \"" + soon.getAssignment() + "\" is due in just " + ((int)(daysLeft+1)) + " days!\\\\\\\\\\");
			}
			else if(daysLeft <= 1 && daysLeft > 0){
				System.out.println("/////WARNING: Your assignment, \"" + soon.getAssignment() +  "\" is due tomorrow!\\\\\\\\\\");
			}
			else if(daysLeft  > -1 && daysLeft <= 0){
				System.out.println("/////WARNING: Your assignment, \"" + soon.getAssignment() + "\" is due today!\\\\\\\\\\");
			}
			pL(0);
			for(int j = 0; j < i; j++){
				pq.addItem(temp[j]);
			}
		}

		while(true){
			Scanner scan = new Scanner(System.in);
			System.out.println("What would you like to do? (Enter '0' for commands)");
			int input = scan.nextInt();
			scan.nextLine();
			pL(0);

			//Print commands
			if(input == 0){
				printCommands();
			}

			//Add an assignment
			else if(input == 1){
				System.out.println("Which assignment will be due?");
				String assi = scan.nextLine();pL(0);
				String dudat[];
				while(true){
					System.out.println("What's the due date? (Format: mm/dd/yyyy)");
					String due = scan.nextLine();
					dudat = due.split("/");
					if(dudat[2].length() == 2){
						dudat[2] = Integer.toString(Integer.parseInt(dudat[2]) + 2000);
					}
					if(dudat.length != 3){
						System.out.println("ERROR: Incorrect Format. Please try again (Format: mm/dd/yyyy)");
					}
					else{
						break;
					}
				}

				pq.addItem(addAssignment(assi, dudat));

				System.out.println("Assignment successfully added. Next assignment due:");
				System.out.println(pq.peek());
				pL(0);
			}

			//Remove an assignment
			else if(input == 2){
				System.out.println("Which Assignment would you like to remove?\nPlease identify using the assignment's index:\n");
				int times = pq.getNumNodes();
				Node[] list = DisplayAssignments();
				System.out.println();
				int re = (scan.nextInt()-1);

				remove(re, list);
			}

			//Change due date
			else if (input == 3){
				System.out.println("Which Assignment would you like to change?\nPlease identify using the assignment's index:\n");
				int times = pq.getNumNodes();
				Node[] list = DisplayAssignments();
				System.out.println();
				int re = (scan.nextInt()-1);
				changeDate(re, list);
			}

			//Print out Assignments
			else if (input == 4){
				DisplayAssignments();
				pL(0);
			}

			//print tree
			else if (input == 5){
				printTree();
				pL(0);
			}

			//Save
			else if (input == 6){
				try{
					Save();
				}catch(IOException IO){

				}
			}

			//exit
			else if (input == 7){
				System.exit(0);
			}
		}
	}

	/**
	* DisplayAssignments():
	* This method will print out all of the assignments formatted as: "#. <Assignment Name>		<day of week due>, <date due>"
	* and then will return the list that it just printed out.
	*
	* Return : the array of Node objects (Assignments) that were just printed out
	*/
	private static Node[] DisplayAssignments(){
		Node[] list = null;
		if(pq.isEmpty()){
			System.out.println("There are no assignments.");
		}
		else{
			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
			int times = pq.getNumNodes();
			list = pq.getAssignments();
			Calendar c = Calendar.getInstance();
			for(int i = 0; i < times; i++){
				c.setTime(list[i].getDate());
				int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
				System.out.println(String.format("%s. %-40s%s, %s", i+1, list[i].getAssignment(), weekdays[dayOfWeek-1], df.format(list[i].getDate())));
			}
		}
		return list;
	}

	/**
	* remove():
	*
	*/
	private static void remove(int re, Node[] list){
		Node search = pq.peek();
		Node[] assignments = new Node[pq.getNumNodes()];
		int j = 0;
		int num = pq.getNumNodes();
		Node compare = list[re].duplicate();
		for(int i = 0; i < num;i++){
			if(compare.compareTo(search) == 1){
				break;
			}
			else{
				assignments[i] = search.duplicate();
				pq.pop();
				search = pq.peek();
				j++;
			}
		}
		if(pq.peek() != null){
			pq.pop();
			System.out.println("Removed " + compare.getAssignment() + ".");
		}
		else{
			System.out.println("Not found.");
		}
		j-=1;
		for(;j>=0;j--){
			pq.addItem(assignments[j]);
		}
	}

	/**
	* changeDate()
	*
	*/
	private static void changeDate(int ch, Node[] list){
		Scanner scan = new Scanner(System.in);
		Node search = pq.peek();
		Node[] assignments = new Node[pq.getNumNodes()];
		int j = 0;
		int num = pq.getNumNodes();
		Node compare = list[ch].duplicate();
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
		for(int i = 0; i < num;i++){
			if(compare.compareTo(search) == 1){
				break;
			}
			else{
				assignments[i] = search.duplicate();
				pq.pop();
				search = pq.peek();
				j++;
			}
		}
		if(pq.peek() != null){
			String dudat[];
			while(true){
				System.out.println("Current due date for " + compare.getAssignment() + " is " + String.format("%s", df.format(compare.getDate())) + ". What would you like to change it to? (mm/dd/yyyy)");
				String due = scan.nextLine();
				dudat = due.split("/");
				if(dudat[2].length() == 2){
					dudat[2] = Integer.toString(Integer.parseInt(dudat[2]) + 2000);
				}
				if(dudat.length != 3){
					System.out.println("ERROR: Incorrect Format. Please try again (Format: mm/dd/yyyy)");
				}
				else{
					break;
				}
			}
			String assi = compare.getAssignment();

			pq.pop();
			pq.addItem(addAssignment(assi, dudat));

			System.out.println("Changed " + compare.getAssignment() + ".");
		}
		else{
			System.out.println("Not found.");
		}
		j-=1;
		for(;j>=0;j--){
			pq.addItem(assignments[j]);
		}
	}

	private static void removeTop(int print){
		if(pq.peek() == null){
			System.out.println("There are no more assignments left.");
		}
		else{
			if(print == 0){
				System.out.println("Removing " + pq.peek().toString());
			}
			Node n = pq.pop();
			if(n == null){
				pq = new MinHeap();
			}
		}
	}

	private static double daysBetween(Date d1, Date d2){
		d1 = resetTime(d1, 0, 0, 0);
		d2 = resetTime(d2, 0, 0, 0);
		return (double)((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

	public static Date resetTime(Date date, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

	private static void Load() throws IOException,ParseException{
		inFile = new File ("data.txt");

		Scanner input = new Scanner(inFile);

		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		while(input.hasNext()){
			String name = input.nextLine();
			String date = input.nextLine();
			Date d = df.parse(date);
			Node n = new Node(name, d);
			pq.addItem(n);
		}

		System.out.println("File found and loaded from memory.");
	}

	private static Node addAssignment(String assignment, String[] dueDate){
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(dueDate[2]), Integer.parseInt(dueDate[0]) - 1, Integer.parseInt(dueDate[1]));
		Date due = c.getTime();
		Node temp = new Node(assignment, due);
		return temp;
	}

	public static void Save() throws IOException{

		File outFile = new File("data.txt");
		FileWriter fw = new FileWriter(outFile);
		PrintWriter pw = new PrintWriter(fw);

		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		if(pq.isEmpty()){
			System.out.println("There are no assignments.");
		}
		else{
			int times = pq.getNumNodes();
			Node[] list = pq.getAssignments();
			for(int i = 0; i < times; i++){
				pw.println(list[i].getAssignment());
				pw.println(df.format(list[i].getDate()));
			}
		}
		System.out.println("Save Completed.");

		pw.close();
	}

	public static void printTree(){
		//depth of tree is log(2)N
		//width of branch = 2^branch#
		//make an array for each level
		int numNodes = pq.getNumNodes();
		Node root = pq.peek();
		Node temp = root;
		int levels = (int) Math.ceil(Math.log(numNodes)) + 1;
		int finalWidth = (int) Math.pow(2, levels -1);
		System.out.println("Number of Assignments: " + numNodes + "\nLevels in tree: " + levels + "\nWidth of final level: " + finalWidth + "\n");
		// TreeCont is the total number of nodes in the tree if all levels were to be filled (since it prints out empty nodes)
		int treeCont = 0;
		for(int i = 0; i < levels; i++){
			treeCont += Math.pow(2, i);
		}

		// This 2-D array holds all assignments with their location within the tree, indexed by [level][row]
		Node[][] tree = new Node[levels][finalWidth];
		int level = 0;
		tree[level][0] = temp; 	// Set the very first index in the array (0,0) as our root node
		level++;
		//while we haven't filled every level's children, fill this level's children in the array
		while(level < levels){
			int nodePos = 0;
			int childPos = 0;
			int parentLevel = level - 1;
			//while we haven't filled every node on this level
			while((nodePos) < (int) Math.pow(2, level)){
				// in a try-catch because if this fails, there is no left child for the node. This means we've filled in all nodes that are in the tree
				try{
					temp = tree[parentLevel][nodePos].getLeft();
					tree[level][childPos] = temp;
					childPos++;
				}catch(NullPointerException npe){
					break;
				}
				// same reason for try loop here, now getting right child node
				try{
					temp = tree[parentLevel][nodePos].getRight();
					tree[level][childPos] = temp;
					childPos++;
					nodePos++;
				}catch(NullPointerException npe){
					break;
				}
			}
			//iterate through the previous entry in the array and map all of the children into the next entry..
			level++;
		}

		// Now to print out each level


		level = levels - 1;
		int row = 0;
		int count = 0;
		int[] location = new int[levels];
		for(int i = 0; i < levels; i++){
			location[i] = 0;
		}
		//  The location array directs the program at each node through the tree
		//	in the array, 0 for left, 1 for right, -1 for untouched (the -1s are always at the end and represent how far up the tree we are looking)

		// iterate until you find one that is not -1
		// if it is 0, go back, observe, go right, set 1
		///////////////////////////////////////////////////////////////////////////////////////////////
		// using 0, 1, -1:
		// step 1: Last 0 change to -1 (all -1 after that)
		// step 2: First -1 to a 1 (all 0 after that)
		// use a boolean to describe whether in step 1 or 2, use int to keep track of last 0 index
		///////////////////////////////////////////////////////////////////////////////////////////////
		// level = levels - number of -1s
		// row = number of 1s

		//level stores the level we're on
		//row counts the 1s in the array

		boolean step = false;
		int countTree = 0;
		while(countTree < treeCont){
			int levels2 = levels -1;
			row = 0;
			int sub = 0;

			//Counting the -1s as they tell where our current level is
			for(int j = 0; j < levels; j++){
				if(location[j] == -1){
					sub++;
				}
			}

			//Subtract sub -- the number of levels we are from the bottom of the tree -- to get our current level
			level = levels2 - sub;

			int last = 1;
			int lastInt = 0;
			for(int j = 1; j < level + 1; j++){
				// Every time we have a 1 we go down the right child, effectively putting us in a new location. The row must change as levels go by
				// depending on how many lefts and rights we've taken. If we take a right at the beginning and the rest lefts, we will still be growing futher into the tree as we go down
				/*Row works this way:
					Every left:
						+2^(current level) - (((nodes on next level)/2) - current row)
					Every right:
						+2^(current level) - (((nodes on next level)/2) - (current row+1))

				*/
				if(location[j] == 1){
					row = row + ((int) Math.pow(2, j) - ((((int) Math.pow(2,j) - row))));
				}
				else if (location[j] == 0){
					row = row + ((int) Math.pow(2, j) - ((((int) Math.pow(2,j) - (row+1)))));
				}
			}

			// Indents the entire tree
			System.out.print("        ");
			// Indenting the current level to the proper location
			for(int i = 0; i < level - 1; i++){
				System.out.print("        ");
			}
			for(int i = 0; i < level; i++){
				System.out.print("        ");
			}
			if(level != 0){System.out.print("|--------");}

			// Accessing the array; if a spot is left empty, that's because there is no node there. Display this with an [EMPTY] node
			try{
				System.out.println(tree[level][row].getAssignment());
				count++;
			}catch(NullPointerException npe){
				System.out.println("[EMPTY]");
			}
			countTree++;

			// Steps as described above.
			// step 1
			if(!step){
				int index = 0;
				for(int i = 0; i < levels; i ++){
					if(location[i] == 0){
						index = i;
					}
				}
				location[index] = -1;
				for(int i = index; i < levels; i++){
					location[i] = -1;
				}
				step = true;
			}
			// step 2
			else{
				int index = 0;
				for(int i = 0; i < levels; i++){
					if(location[i] == -1){
						index = i;
						break;
					}
				}
				location[index] = 1;
				for(int i = index+1; i < levels; i++){
					location[i] = 0;
				}
				step = false;
			}
		}
		System.out.println();
	}

	public static void printCommands(){
		pL(0);
		System.out.println("Commands:");
		System.out.println("1  -  Add a new assignment\n2  -  Delete an assignment\n3  -  Change an assignment's due date\n4  -  List assignments\n5  -  Print data tree as stored in memory\n6  -  Save\n7  -  Quit");
		pL(0);
	}

	public static void pL(int mode){
		if(mode == 0)
			System.out.println("--------------------------------------------------------------------------------------");
	}
}
