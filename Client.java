/*
 * By: Sarah Nicholson and Peyton Hartzell
 */

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.Scanner;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;

public class Client extends JFrame implements Runnable{
	
	private JTextArea chat;	// chat window
	private JTextField inputText; // text field to input text
	private ServerSocket server; // the server
	private Socket socket; // the connection
	private ObjectInputStream input; // the input
	private ObjectOutputStream output; // the output
	
	private String ip; // IP address
	private String message = ""; // message
	
	public static String clientName;
	
	public Client(String host)
	{
		super("Instant Messenger");
		ip = host;
		
		inputText = new JTextField();
		inputText.setEditable(false);
		
		// get client's name from user input
		Scanner scanner = new Scanner(System.in); 
		System.out.println("Enter client's name: ");
		clientName = scanner.next();
		scanner.close();
		
		inputText.addActionListener(
				new ActionListener()
				{
				public void actionPerformed(ActionEvent event)
				{
					send(event.getActionCommand());
					inputText.setText("");
				}
			}
		);
		add(inputText, BorderLayout.SOUTH); // where user inputs text
		
		// creating the textarea and setting up the background and font color
		chat = new JTextArea();
		chat.setForeground(Color.white);
		chat.setBackground(Color.black);
		chat.setEditable(false);
		add(new JScrollPane(chat));
		setSize(450, 350);
		setVisible(true);
	}
	
	// connect to the server and perform all actions
	public void run()
	{
		try
		{
			connectAndSetup(); // connect to the server and set up streams
			messaging(); // using the instant messenger and sending messages
		}
		catch(EOFException eofe)
		{
			showMessage("\nClient ended connection");
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		finally
		{
			closeConnection();
		}
	}
	
	// connect to server and setup streams
	private void connectAndSetup() throws IOException
	{
		showMessage("Connecting... \n");
		socket = new Socket(InetAddress.getByName(ip), 6789);
		showMessage("Connection to localhost established!");
		
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
		showMessage("\nStreams are set up\n");
	}
	
	// while messaging server
	private void messaging() throws IOException
	{
		canType(true);	
		
		String bye = "";
		
		// to close, type "Bye"
		while(!bye.equals("Bye"))
		{
			try
			{
				message = (String) input.readObject();
				bye = message.substring(message.length() - 3);
				showMessage("\n" + message);
			}
			catch(ClassNotFoundException cnfe)
			{
				showMessage("Invalid object sent!");
			}
		}
	}
	
	// close connection
	private void closeConnection()
	{
		showMessage("\nConnection closed!");
		canType(false);
		try
		{
			output.close(); // closes output path to client
			input.close(); // closes input path from client to server
			socket.close(); // closes connection between server and client
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	// sends a message to server
	private void send(String message)
	{
		try
		{
			output.writeObject(clientName + " - " + message);
			output.flush();
			showMessage("\n" + clientName + " - " + message);
		}
		catch(IOException ioe)
		{
			chat.append("\nSomething went wrong!");
		}
	}
	
	// shows the messages
	private void showMessage(String message)
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					chat.append(message);
				}
			}
		);
	}
	
	// allows client to type
	private void canType(boolean b)
	{
		SwingUtilities.invokeLater(
			new Runnable()
			{
				public void run()
				{
					inputText.setEditable(b);
				}
			}
		);
	}
}