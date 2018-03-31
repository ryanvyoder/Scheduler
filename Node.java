import java.util.*;
import java.time.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class Node{
	private String assignment;
	private Date dueDate;
	private Node right =  null;
	private Node left = null;
	private Node parent = null;;
	private Node next = null;
	private boolean use = false;
	
	public Node(String assignment, Date date){
		dueDate = date;
		this.assignment = assignment;
		this.use = true;
	}
	
	public Node(Node n){
		this.assignment = n.getAssignment();
		this.use = true;
		this.dueDate = n.getDate();
	}
	
	public Node duplicate(){
		Node n = new Node(this.assignment, this.dueDate);
		return n;
	}
	
	public int compareTo(Node n){
		if(this.assignment.equalsIgnoreCase(n.getAssignment())){
			return 1;
		}
		else{return 0;}
	}
	
	public void disable(){
		this.use = false;
	}
	
	public boolean getUse(){
		return use;
	}
	
	public void refresh(){
		this.right = null;
		this.left = null;
		this.parent = null;
		this. next = null;
	}
	
	public boolean isEmpty(){
		if(right == null && left == null){
			return true;
		}
		else{return false;}
	}
	
	public void setRight(Node item){
		right = item;
	}
	
	public void setLeft(Node item){
		left = item;
	}
	
	public Node getParent(){
		return this.parent;
	}
	
	public void setParent(Node n){
		this.parent = n;
	}
	
	public Node getRight(){
		return this.right;
	}
	
	public Node getLeft(){
		return this.left;
	}

	public void setChild(Node item){
		if(this.left == null){
			this.left = item;
		}
		else{
			this.right = item;
		}
	}
	
	public void setNext(Node item){
		this.next = item;
	}
	
	public Node getNext(){
		return this.next;
	}
	
	public boolean isFull(){
		if(this.left != null && this.right != null){
			return true;
		}
		else{return false;}
	}
	
	public Date getDate(){
		return this.dueDate;
	}
	
	public void changeDate(Date d){
		this.dueDate = d;
	}
	
	public void changeAssignment(String a){
		this.assignment = a;
	}
	
	public String getAssignment(){
		return this.assignment;
	}
	
	public void swap(Node n){
		Date d = n.getDate();
		n.changeDate(this.dueDate);
		changeDate(d);
		
		String a = n.getAssignment();
		n.changeAssignment(this.assignment);
		changeAssignment(a);
	}
	
	public String toString(){
		String [] month = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		String [] weekdays = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		DateFormat dateFormat = new SimpleDateFormat("dd, yyyy");
		DateFormat monthFormat = new SimpleDateFormat("MM");
		Calendar c = Calendar.getInstance();
		c.setTime(dueDate);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		StringBuilder sb = new StringBuilder();
		sb.append(assignment + "\t");
		sb.append(weekdays[dayOfWeek - 1] + ", " + (month[Integer.parseInt(monthFormat.format(dueDate)) - 1]) + " " + dateFormat.format(dueDate));
		return sb.toString();
	}
}