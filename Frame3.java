import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class Frame3 extends JFrame implements ActionListener {
  JLabel answer = new JLabel("");
  JPanel pane = new JPanel(); // create pane object
  JButton pressme = new JButton("Press Me");

  /*
  Constructor
   */
  public Frame3() {
    super("Event Handler Demo");
    setBounds(100,100,300,200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container con = this.getContentPane(); // inherit main frame
    con.add(pane); pressme.setMnemonic('P'); // associate hotkey
    pressme.addActionListener(this);   // register button listener
    pane.add(answer); pane.add(pressme);
    pressme.requestFocus();
    setVisible(true); // make frame visible
  }

  /*
  Event Handler
   */
  public void actionPerformed(ActionEvent event){
    Object source = event.getSource();
    if (source == pressme){
      answer.setText("Button pressed!");
      JOptionPane.showMessageDialog(null,"I hear you!","Message Dialog", JOptionPane.PLAIN_MESSAGE); setVisible(true);  // show something
    }
  }
  public static void main(String args[]) {
    new Frame3();
  }
}
