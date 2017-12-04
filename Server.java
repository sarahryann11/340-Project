/*
 * By: Sarah Nicholson and Peyton Hartzell
 */

import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.*;
import java.util.Scanner;

public class Server extends JFrame implements Runnable
{
	private JTextArea chat;	// chat window
	private JTextField inputText; // text field to input text
	private ServerSocket server; // the server
	private Socket socket; // the connection
	private ObjectInputStream input; // the input
	private ObjectOutputStream output; // the output
	
	public static String serverName;
	
	// server constructor
	public Server()
	{
		super("Instant Messenger");
		inputText = new JTextField();
		inputText.setEditable(false);
		
		// get server's name from user input
		Scanner scanner = new Scanner(System.in); 
		System.out.println("Enter server's name: ");
		serverName = scanner.next();
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
		chat.setForeground(Color.red);
		chat.setBackground(Color.black);
		chat.setEditable(false);
		add(new JScrollPane(chat));
		setSize(450, 350);
		setVisible(true);
	}
	
	// connect to client and perform all actions
	public void run()
	{
		try
		{
			server = new ServerSocket(6789, 100); // 6789 is a dummy port; 100 is how many people can wait to connect.
			while(true)
			{
				try
				{
					connectAndSetup(); // connect to the server and set up streams
					messaging(); // using the instant messenger and sending messages
				}
				catch(EOFException eofe)
				{
					showMessage("\nServer ended connection");
				} 
				finally
				{
					closeConnection();
				}
			}
		} 
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	// connect to server and set up streams
	private void connectAndSetup() throws IOException
	{
		showMessage("Waiting connection...\n");
		socket = server.accept();
		showMessage("Connected to localhost");
		
		output = new ObjectOutputStream(socket.getOutputStream());		
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
		showMessage("\nStreams are set up\n");
	}
	
	// while messaging client
	private void messaging() throws IOException
	{
		String message = " You are connected!";
		send(message);
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
	
	// close the connection
	public void closeConnection()
	{
		showMessage("\nConnection Closed\n");
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
	
	// send message to client
	private void send(String message)
	{
		try
		{
			output.writeObject(serverName + " - " + message);
			output.flush();
			showMessage("\n" + serverName + " - " + message);
		}
		catch(IOException ioe)
		{
			chat.append("\nCan't send message");
		}
	}
	
	// update chat by showing new message
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
	
	// allows server to type
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