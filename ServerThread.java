/*
 * By: Sarah Nicholson and Peyton Hartzell
 */

import javax.swing.JFrame;

public class ServerThread extends Thread
{
	public static void main(String[] args) 
	{
		// create the server thread
		Thread serverThread = new Thread(new Server());
		serverThread.start();
	}
}