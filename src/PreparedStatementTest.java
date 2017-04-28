import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PreparedStatementTest extends JFrame {

	final static String DRIVER = "com.mysql.jdbc.Driver";             
	final static String url = "jdbc:mysql://localhost/library"
			+ "user=root&password=password";
	//GUI objects
	private JButton insert, show;
	private JTextArea display;
	private JPanel pEntry;
	private JLabel lblBookAuthor, lblTitle, lblYear, lblPatronFName, lblPatronLName;
	private JTextField txtBookAuthor,txtTitle, txtYear, txtPatronFName, txtPatronLName;
	//JDBC objects
	Connection c ;
	Statement st;
	PreparedStatement pst;


	//
	public PreparedStatementTest()
	{
		//create and add the text area to south area of the frame
		display=new JTextArea(10,5);
		JScrollPane scrollPane = new JScrollPane(display);
		add(scrollPane,BorderLayout.SOUTH);
		//
		pEntry =new JPanel();
		GridBagLayout grid = new GridBagLayout();
		pEntry.setLayout (grid);
		//

		lblBookAuthor=new JLabel("Author:");
		txtBookAuthor=new JTextField(40);
		//
		lblTitle=new JLabel("Title:");
		txtTitle=new JTextField(40);
		//
		insert=new JButton("Insert");
		show = new JButton("Show");
		//
		lblYear=new JLabel("Published Year");
		txtYear=new JTextField(5);
		//
		lblPatronFName=new JLabel("Patron First Name");
		txtPatronFName=new JTextField(20);
		//
		lblPatronLName=new JLabel("Patron Last Name");
		txtPatronLName=new JTextField(20);
		
		

		//add components to the grid
		addComponent(pEntry, grid, lblBookAuthor, 0,0,1,1);
		addComponent(pEntry, grid, txtBookAuthor, 1,0,1,1);
		addComponent(pEntry, grid, insert, 2,0,1,1);
		addComponent(pEntry, grid, lblTitle, 0,1,1,1);
		addComponent(pEntry, grid, txtTitle, 1,1,1,1);
		addComponent(pEntry, grid, show, 2,1,1,1);
		addComponent(pEntry, grid, show, 2,1,1,1);
		addComponent(pEntry, grid, lblYear, 0,2,1,1);
		addComponent(pEntry, grid, txtYear, 1,2,1,1);
		addComponent(pEntry, grid, lblPatronFName, 0,3,1,1);
		addComponent(pEntry, grid, txtPatronFName, 1,3,1,1);
		addComponent(pEntry, grid, lblPatronLName, 0,4,1,1);
		addComponent(pEntry, grid, txtPatronLName, 1,4,1,1);


		add(pEntry,BorderLayout.WEST);
		//
		ButtonHandler bHandler= new ButtonHandler();
		insert.addActionListener(bHandler);
		show.addActionListener(bHandler);
		//
		connect();

	}
	public void addComponent(JPanel p, GridBagLayout grid, Component c, int gridx, int gridy,
			int gridwidth, int gridheight)
	{
		GridBagConstraints constr = new GridBagConstraints();
		constr.gridx = gridx; //column
		constr.gridy = gridy; //row
		constr.gridwidth = gridwidth; //number of cells in the row that will be covered
		constr.gridheight = gridheight; //number of cells in the column that will be covered
		constr.fill = GridBagConstraints.HORIZONTAL; //resize the component horizontally
		// add the component 
		grid.setConstraints(c, constr); //apply the constraints to the grid
		p.add(c);
	}

	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{		
				String author = txtBookAuthor.getText();
				String title = txtTitle.getText();
				String textYear = txtYear.getText();
				int year = Integer.parseInt(textYear);
				String patronFirst = txtPatronFName.getText();
				String patronLast = txtPatronLName.getText();
			if(e.getSource()==insert)
			{
				
				if(author.isEmpty() || title.isEmpty() || textYear.isEmpty())
				{
					insertRowPatron(patronFirst, patronLast);
				}
				else if (patronFirst.isEmpty() || patronLast.isEmpty())
				{
					insertRowBook(author, title, year);
					
				}
				
			}
			else if(e.getSource()==show)
				displayResults();
		}

	}
	//
	public void connect()
	{
		try
		{			
			Class.forName( DRIVER ).newInstance();
			// establish connection to database                              
			c = DriverManager.getConnection( url);

		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			//e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			//e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
	//
	public void displayResults()
	{
		try
		{
			st = c.createStatement();

			ResultSet rs = st.executeQuery("SELECT first_name, last_name, title, checkout_date FROM BookReader INNER JOIN Patron ON Patron.id = patron_id INNER JOIN Book ON Book.id = book_id ORDER BY last_name, first_name;");
			ResultSetMetaData md = rs.getMetaData();
			int row=0;
			String info="";
			while(rs.next())
			{
				for( int i=1;i <= md.getColumnCount();i++)
				{
					info+=md.getColumnName(i)+"\t: "+rs.getObject(i)+"\t"; 
				}
				row+=1;
				info+="\n";
			}
			display.setText(info);
			rs.close();
		}
		catch(SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			//e.printStackTrace();
		}
	}
	//
	public void insertRowPatron(String first_name, String last_name)
	{
		try {

			pst = c.prepareStatement("Insert into Patron (id, first_name, last_name) VALUES(null,?,?)");

			pst.setString(1, first_name); 
			pst.setString(2, last_name);

			//Execute the prepared statement using executeUpdate method:  
			int val = pst.executeUpdate(); //returns the row count


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			System.out.println("Done!");
		}
	}
	public void insertRowBook(String title, String author, int year)
	{
		try {

			pst = c.prepareStatement("Insert into Book Values(null,?,?,?)");

			pst.setString(1, author); 
			pst.setString(2, title);
			pst.setInt(3, year);

			//Execute the prepared statement using executeUpdate method:  
			int val = pst.executeUpdate(); //returns the row count


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			System.out.println("Done!");
		}
	}
	public static void main(String[] args) {
		JFrame frame = new PreparedStatementTest();
		frame.setSize(600,300);
		frame.setVisible(true);

	}

}

