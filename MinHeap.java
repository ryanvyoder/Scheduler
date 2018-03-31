/**
* Creates a pq that uses a binary tree in which every child is greater than the parent (in other words, the data is sorted in ascending order by date)
* When an item is added, the tree is filled breadth-first (Min Heap)
* Originally intended to be an indexable priority queue, I was more fascinated with the minHeap and changed it as such
*
* Created for Scheduler.java -- Ryan Yoder (2016-2017)
*/
public class MinHeap{
	Node top = null, last = null;
	int nodes = 0;
	//This needs to sort everything. Searchable by month, by year, and by day.
	
	/**
	* MinHeap():	/CONSTRUCTOR/
	* This method is a constructor in which the pq is created using a Node object as the
	* parameter, and this Node becomes the current first and last Node in the tree.
	*/
	public MinHeap(Node root){
		top = root;
		last = root;
	}
	
	/**
	* MinHeap():	/CONSTRUCTOR/
	* This method is another constructor in which the pq is created without passing in any Node objects.
	*/
	public MinHeap(){
		
	}
	
	/**
	* getNumNodes():
	* This method returns the number of Node objects held within this queue.
	*
	* Return : the number of nodes in the queue
	*/
	public int getNumNodes(){
		return this.nodes;
	}
	
	/**
	* addItem():
	* This method takes a Node object and adds it to the priority queue. It places the new Node as 
	* the lowest leaf in the tree and then calls the bubbleUp() method to move it to the correct spot in the tree.
	*
	* Node item : the Node item which is being added into the tree
	*/
	public void addItem(Node item){
		if (last == null)
			top = item;
		else{
			if (last.getParent() == null){ // if the last node added is the root node
				last.setLeft(item);
				item.setParent(last);
			}
			else{
				if((last.getParent().getNext() == null) && (last.getParent().isFull())){  //If the last node filled was at the bottom right of the graph...
					Node n = this.top;
					while(n.getLeft() != null){
						n = n.getLeft();
					}
					n.setLeft(item);
					item.setParent(n);
				}
				else if(last.getParent().isFull()){ //If the last node filled was a left child to a parent...
					last.getParent().getNext().setLeft(item);
					last.setNext(item);
					item.setParent(last.getParent().getNext());
				}
				else{	//Otherwise the last node was the left child of the non-leftmost node in the graph, simply set the right child of the same parent
					last.setNext(item);
					last.getParent().setRight(item);
					item.setParent(last.getParent());
				}
			}
		}
		last = item;
		nodes++;
		//Now we need to bubble it up
		BubbleUp(item);
	}
	
	/**
	* peek():
	* This method returns the Node that is stored as the next assignment that is due (top of pq tree)
	*
	* Return : the node which is stored as the first assignment due date 
	*/
	public Node peek(){
		return top;
	}
	
	/**
	* BubbleUp():
	* This method takes a Node object that is stored within the tree and moves it up the tree until it is as far up as it should be.
	*
	* Node item : The node which is to be bubbled up the tree
	*/
	public void BubbleUp(Node item){
		while(true){
			if(item.getParent() == null){
				//System.out.println("Parent null");
				break;
			}
			if(item.getDate().before(item.getParent().getDate())){
				//if the parent is after the child
				item.swap(item.getParent());
				item = item.getParent();
				//need to take care of the case that there is no parent
			}
			else{break;}
		}
	}
	
	/**
	* Sink():
	* This method takes a Node object and pushes it down the tree until it's at the lowest level that it should be
	*
	* Node item : The node which is to be pushed down the tree to where it belongs
	*/
	public void Sink(Node item){
		while(true){
				if(item.isEmpty()){
					break;
				}
				
				if(item.getDate().after(item.getLeft().getDate())){
					//if the parent is after the child
					if(item.getRight() != null){
						if(item.getLeft().getDate().before(item.getRight().getDate())){
							item.swap(item.getLeft());
							item = item.getLeft();
						}
						else{
							item.swap(item.getRight());
							item = item.getRight();
						}
					}
					else{
						item.swap(item.getLeft());
						item = item.getLeft();
					}
				}
				else if(item.getRight() == null){
					break;
				}
				else if(item.getDate().after(item.getRight().getDate())){
					item.swap(item.getRight());
					item = item.getRight();
				}
				else{break;}
			}
	}
	
	
	/**
	* getAssignments():
	* This method returns an array of Node objects for every Node within the tree (pq) in the order of when they are due (lowest index for soonest)
	*
	* Return : The Node array that will contain all of the Nodes in ascending order
	*/
	public Node[] getAssignments(){
		int bop = nodes;
		Node[] assignments = new Node[nodes];
		for(int i = 0; i < bop; i++){
			assignments[i] = top.duplicate();
			Node n = pop();
		}
		for(int i = 0; i < bop; i++){
			addItem(assignments[i]);
		}
		
		return assignments;
	}
	
	/**
	* pop():
	* This method will swap the first and last nodes of the tree in order to remove the first one efficiently. It will then remove the last node from
	* the tree and sink the item that was pushed to the top back down to the bottom
	*
	* Return : the Node which was removed
	*/
	public Node pop(){
		if(top.isEmpty()){
			nodes--;
			top = null;
			last = null;
			return top;
		}
		else{
			top.swap(last);
			//now need to set last to be the node before last and delete our current last
			Node item = top;

			//remove the last node and make it so that nothing is pointing at it
			if(last.getParent().getLeft() == last){
				last.getParent().setLeft(null);
			}
			else{
				last.getParent().setRight(null);
			}
			last.disable();
			last.refresh();
			
			last = getLast();
			
			//sink the node
			Sink(item);
			nodes--;
			
			
			//Have it either set the right child of the parent to the last and the right to null or start at the root and traverse all the way left and then through all next nodes
			
			return top;
		}
	}
	
	/**
	* getLast():
	* This method traverses down the tree (pq) on the leftmost side and then once it reaches the bottom it moves
	* all the way to the rightmost node and returns it. This Node is effectively the last node in the tree
	*
	* Return : the Node which is the last node in the tree
	*/
	private Node getLast(){
		Node look = top;
		while(look.getLeft() != null && look.getLeft().getUse()){
			look = look.getLeft();
		}
		
		while(look.getNext() != null && look.getNext().getUse()){
			look = look.getNext();
		}
		
		if(look.getNext() != null){
			look.setNext(null);
		}
		
		if(look.getLeft() != null){
			look.setLeft(null);
		}
		
		return look;
	}
	
	/**
	* isEmpty():
	* This method simply checks if the top node exists. If it does, then the tree cannot be empty
	* and so it will return false, but if it doesn't then the tree must be empty and it will return true
	*
	* Return : true if the tree is empty, false if it is not
	*/
	public boolean isEmpty(){
		if(top == null){
			return true;
		}
		else{
			return false;
		}
	}

}