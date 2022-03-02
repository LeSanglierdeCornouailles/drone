/* 
 * ● On execution it should open a server socket and wait for connections
 * (e.g., on port 5000) from multiple drone client devices.
 * ● The server should have a GUI that is built using AWT. It should 
 * display graphical top-down location information on an (x,y) graphical 
 * grid, where North is at the top. Height should be represented by a
 * number at the drone location.
 * ● The server should be able to accept connections from many robots
 * (limited to a maximum of 3 for the purpose of testing).
 * ● The display should show the (x,y,z) location and orientation of
 * every robot client that is connected to the server. This is the
 * current position.
 * ● Each robot has a name and dimension. This information needs to be
 * displayed on the GUI when that robot is selected with the mouse.
 * ● The server should calculate the distance between each robot and
 * evaluate if two robots are in danger of colliding using the current
 * position and the size of the robots in question. For example, this
 * could be performed by asking if the distance between the two robots
 * > sum of the radii + a safety margin?
 * ● The GUI should provide a display of the last three positions
 * (e.g., using a different colour)
 * ● The collision safety margin should be configurable.
 * ● Add two other features of your choice that you deem necessary
 * and/or useful. List them explicitly in your report.
 */

import java.net.*;
import java.io.*;

public class ThreadedServer 
{
    private static int maxConn    = 5;
	private static int portNumber = 5050;
	
	public static void main(String args[])
    {
        ServerFrame server = new ServerFrame(portNumber, maxConn);
        server.run();
        server.listening();
        
        return;
        
        /*
        boolean      listening    = true;
        ServerSocket serverSocket = null;
        
        // Set up the Server Socket
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("New Server has started listening on port: " +
                               portNumber);
        } catch (IOException e) {
            System.out.println("Cannot listen on port: " +
                portNumber + ", Exception: " + e);
            System.exit(1);
        }
        
        // Server is now listening for connections or would not get to this point
        while (listening) // almost infinite loop - loop once for each client request
        {
            Socket clientSocket = null;
            try {
            	System.out.println("**. Listening for a connection...");
                clientSocket = serverSocket.accept();
                System.out.println("00. <- Accepted socket connection from a client: ");
                System.out.println("    <- with address: " + clientSocket.getInetAddress().toString());
                System.out.println("    <- and port number: " + clientSocket.getPort());
            } catch (IOException e) {
                System.out.println("XX. Accept failed: " + portNumber + e);
                listening = false;   // end the loop - stop listening for further client requests
            }
            
            ThreadedConnectionHandler con = new ThreadedConnectionHandler(clientSocket);
            con.start();
            //con.isAlive()
            System.out.println("02. -- Finished communicating with client:" + clientSocket.getInetAddress().toString());
        }
        
        // Server is no longer listening for client connections - time to shut down.
        try {
            System.out.println("04. -- Closing down the server socket gracefully.");
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("XX. Could not close server socket. " + e.getMessage());
        }
        */
    }
}
