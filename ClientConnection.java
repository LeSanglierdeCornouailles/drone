import java.net.*;
import java.io.*;

public class ClientConnection
{
    // Data
    private int     port;
    private String  ip;
    private boolean connected;
    // Connectors
	private Socket             socket = null;
    private ObjectInputStream  is = null;
    private ObjectOutputStream os = null;
    
	public ClientConnection(String ip, int port)
    {
        this.ip        = ip;
        this.port      = port;
        this.connected = false;
    }
    
    public ClientConnection(Socket client)
    {
        this.ip     = client.getInetAddress().toString();
        this.port   = client.getPort();
        this.socket = client;
        
        try {
            this.os        = new ObjectOutputStream(this.socket.getOutputStream());
    		this.is        = new ObjectInputStream(this.socket.getInputStream());
            this.connected = true;
            /*System.out.println("00. -> Connected to Server:" + 
                this.socket.getInetAddress() + " on port: " + 
                this.socket.getPort());
    		System.out.println("    -> from local address: " + 
                this.socket.getLocalAddress() + " and port: " +
                this.socket.getLocalPort());*/
        } catch (IOException e) {
            System.out.println("XX. Failed to Acept the Client at port: " +
                this.port);
        	System.out.println("    Exception: " + e.toString());
            this.connected = false;
        }
        
        this.ip        = ip;
        this.port      = port;
        this.connected = true;
    }
    
    public boolean connect()
    {
        if (this.connected)
            disconnect();
        
        try {
            this.socket    = new Socket(this.ip, this.port);
    		this.os        = new ObjectOutputStream(this.socket.getOutputStream());
    		this.is        = new ObjectInputStream(this.socket.getInputStream());
            this.connected = true;
        } catch (Exception e) {
            System.out.println("XX. Failed to Connect to the Server at port: " +
                               this.port);
        	System.out.println("    Exception: " + e.toString());
            this.connected = false;
        }
        return this.connected;
    }
    
    public void disconnect()
    {
        try {
            if (this.is != null)
                this.is.close();
            if (this.os != null)
                this.os.close();
            if (this.socket != null)
                this.socket.close();
        } catch (Exception e) {
            System.out.println("Exception at disconnect: " + e.toString());
        }
        this.is        = null;
        this.os        = null;
        this.socket    = null;
        this.connected = false;
    }
    
    // method to send a generic object.
    public boolean send(Object o)
    {
        boolean res = false;
        
        if (!this.connected)
            return false;
        
		try {
            this.os.reset();
		    this.os.writeObject(o);
		    this.os.flush();
            res = true;
        } catch (EOFException e) {
            System.out.println("XX. Client Disconnected");
            disconnect();
            res = false;
		} catch (IOException e) {
		    System.out.println("XX. Exception Occurred on Sending:" +
                               e.toString());
		}
        return res;
    }
    
    // method to receive a generic object.
    public Object receive() 
    {
        if (!this.connected)
            return null;
        
		Object obj = null;
		try {
			obj = this.is.readObject();
		} catch (EOFException e) {
            System.out.println("XX. Client Disconnected");
            disconnect();
            obj = null;
        } catch (Exception e) {
		    System.out.println("XX. Exception Occurred on Receiving:" +
                               e.toString());
		}
		return obj;
    }
    
    public boolean isConnected() { return this.connected; }
}
