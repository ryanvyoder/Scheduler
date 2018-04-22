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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
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
public class SchedulerGUI extends JFrame implements ActionListener {

  JLabel dataDisplay = new JLabel("");
  JPanel pane = new JPanel(); // create pane object
  JButton displayData = new JButton("Display Data");
  JButton addAssignment = new JButton("Add Assignment");
  JButton submitAssignment = new JButton("Submit");
  JButton removeAssignment = new JButton("Remove Assignment");
  JTextField assignName = new JTextField(20);
  JTextField dueDate = new JTextField(20);

  //Defining some different data which will be used for weekday and month mapping as well as declaring our File for loading and saving and the priority queue structure.
  static Scheduler sc = new Scheduler();
  /*
  * Constructor
  */
  public SchedulerGUI() {
    super("SchedulerGUI");
    setBounds(100,100,700,200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container con = this.getContentPane();
    con.add(pane);
    displayData.addActionListener(this);
    addAssignment.addActionListener(this);
    submitAssignment.addActionListener(this);
    pane.add(dataDisplay);
    pane.add(displayData);
    pane.add(addAssignment);
    pane.add(removeAssignment);
    displayData.requestFocus();
    addAssignment.requestFocus();
    removeAssignment.requestFocus();
    submitAssignment.requestFocus();
    setVisible(true); // make frame visible
	}


  /*
  Event Handler
   */
  public void actionPerformed(ActionEvent event){
    Object source = event.getSource();
    switch (source){
      case displayData:
        displayData();
        break;
      case addAssignment:
        addAssignment();
        break;
      case submitAssignment:
        submitAssignment();
        break;
      case removeAssignment:
        removeAssignment();
        break;
      default:
        System.out.println("Error");
        break;
    }
  }

  private void displayData(){
    dataDisplay.setText(sc.GetAssignments());
    //JOptionPane.showMessageDialog(null,"I hear you!","Message Dialog", JOptionPane.PLAIN_MESSAGE); setVisible(true);  // show something
  }

  private void removeAssignment(){
    addAssignment.setVisible(false);
    removeAssignment.setVisible(false);
    pane.remove(addAssignment);
    pane.remove(removeAssignment);
    dataDisplay.setText("What is the number of the assignment that you would like to remove?");
    pane.add(assignName);
    pane.add(submitRemoval);
    submitRemoval.setVisible(true);
    assignName.setVisible(true);
  }

  private void submitRemoval(){

  }

  private void addAssignment(){
    addAssignment.setVisible(false);
    displayData.setVisible(false);
    removeAssignment.setVisible(false);
    pane.remove(displayData);
    pane.remove(addAssignment);
    pane.remove(removeAssignment);
    dataDisplay.setText("What is the name and due date of the assignment you would like to add?");
    pane.add(assignName);
    pane.add(dueDate);
    pane.add(submitAssignment);
    dueDate.setVisible(true);
    submitAssignment.setVisible(true);
    assignName.setVisible(true);
    //JOptionPane.showMessageDialog(null,"I hear you!","Message Dialog", JOptionPane.PLAIN_MESSAGE); setVisible(true);  // show something
  }

  private void submitAssignment(){
    String[] dudat = dueDate.getText().split("/");
    if(dudat[2].length() == 2){
      dudat[2] = Integer.toString(Integer.parseInt(dudat[2]) + 2000);
      sc.addAssignment(assignName.getText(), dudat);
    }

    if(dudat.length != 3){
      dataDisplay.setText("What is the name and due date of the assignment you would like to add? ERROR: INCORRECT FORMAT");
    }
    else{
      sc.pq.addItem(sc.addAssignment(assignName.getText(), dudat));
      dueDate.setVisible(false);
      submitAssignment.setVisible(false);
      assignName.setVisible(false);
      pane.remove(dueDate);
      pane.remove(submitAssignment);
      pane.remove(assignName);
      pane.add(displayData);
      pane.add(addAssignment);
      pane.add(removeAssignment);
      displayData.setVisible(true);
      addAssignment.setVisible(true);
      removeAssignment.setVisible(false);
      dataDisplay.setText(sc.GetAssignments());
    }
  }

	public static void main(String[] args){
    new SchedulerGUI();
    sc.startup();
	}
}
