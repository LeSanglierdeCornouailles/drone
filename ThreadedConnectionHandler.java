/* The Connection Handler Class - Written by Derek Molloy for the EE402 Module
 * See: ee402.eeng.dcu.ie
 */
import java.io.*;
import java.net.*;

public class ThreadedConnectionHandler extends Thread
{
    private Message          message;
    private Status[]         stats = {null, null, null, null};
    private ClientConnection client;
    
	public ThreadedConnectionHandler(Socket clientSocket)
    {
        this.message = new Message();
        this.client  = new ClientConnection(clientSocket);
    }

    // Will eventually be the thread execution method 
    // - can't pass the exception back
    public void run()
    {
        Object obj  = null;
        Status stat = null;
        
        do {
            obj = this.client.receive();
            if (obj == null)
                break;
            
            stat = (Status) obj;
            //System.out.println("Rec: " + stat.getX() + "/" + stat.getY() );
            setStatus(stat);
            this.client.send(message);
            // Clean
            obj  = null;
            stat = null;
        } while(true);
        this.client.disconnect();
        System.out.println("05. -> Ending Client.");
    }
    
    private void setStatus(Status stat)
    {
        this.stats[3] = this.stats[2];
        this.stats[2] = this.stats[1];
        this.stats[1] = this.stats[0];
        this.stats[0] = stat;
    }
    
    public Status getHistorical(int i)
    {
        if (i >= 0 && i < this.stats.length)
            return this.stats[i];
        System.out.println("Out of array.");
        return null;
    }
    
    public void setError(String msg)
    {
        message.setText(msg);
        message.setType(Message.Type.SHUTDOWN);
    }
    
    public void setWarning(String msg)
    {
        message.setText(msg);
        message.setType(Message.Type.WARNING);
    }
    
    public void setAnswer(String msg)
    {
        message.setText(msg);
        message.setType(Message.Type.ANSWER);
    }
    
    public Status getStatus() { return this.stats[0]; }
}
