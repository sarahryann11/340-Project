/*
 * By: Sarah Nicholson and Peyton Hartzell
 */

import javax.swing.JFrame;
import java.util.Scanner;

public class ClientThread extends Thread
{
	public static void main(String[] args) 
	{
		// create the client thread
		Thread clientThread = new Thread(new Client("127.0.0.1"));
		clientThread.start();
	}
}