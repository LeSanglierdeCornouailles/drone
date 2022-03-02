/*
 * ● You should pass the server address and the robot name (e.g., localhost)
 * to the client as a command line argument when you execute it.
 * ● The client application should have a GUI that allows you to push buttons
 * to go forward, backward, rotate left, rotate right, and to choose a speed.
 * ● The client should send an update every 10 seconds, regardless of whether
 * the robot is moved or not.
 * ● The client should display the current connection status and the last time
 * that a message was successfully sent to the server.
 * ● Add a novel feature of your own design.
 */
import java.net.*;
import java.io.*;


public class Client
{
    public static void main(String args[]) throws InterruptedException
    {
        ClientFrame scre = null;
        
        System.out.println("Client Application - EE402 Assignment\n");
        if (args.length != 4)
        {
            System.out.println("Error usage:");
            System.out.println("java Client <server> <port> <robot name> <size>");
            return;
        }
        
        scre = new ClientFrame(args[0], 
                               Integer.parseInt(args[1]),
                               args[2],
                               Integer.parseInt(args[3]));
        scre.run();
    }
}
