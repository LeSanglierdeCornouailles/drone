import java.util.List;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ServerFrame extends Frame implements ActionListener, MouseMotionListener
{
    // Process
    private int                 maxCon;
    private boolean             listen;
    private ServerSocket        socket;
    private java.util.List<ThreadedConnectionHandler> list;
    // GUI
    private Timer     timer;
    private Canvas    can1;
    private Choice    cho1;
    private Button    btn1;
    private TextArea  txt3;
    private TextField txt1;
    private TextField txt2;
    private TextField txt4;
    
    public ServerFrame(int port, int max_con)
    {
        list   = new ArrayList<ThreadedConnectionHandler>();
        maxCon = max_con;
        
        try {
            socket = new ServerSocket(port);
            listen = true;
        } catch (IOException e) {
            System.out.println("Cannot listen on port: " + port + 
                ", Exception: " + e);
            System.exit(1);
        }
    }
    
    public void run()
    {
        txt1 = createText("Clients: ", 500, 50);
        txt2 = createText("R Name:",   500, 90);
        txt4 = createText("R Size:",   500, 135);
        cho1 = createChoice("Margin",  500, 170);
        btn1 = createButton("Kill Connections", 510, 210);
        txt3 = createReporter(505, 270);
        
        Canvas can = new Canvas();
        can1 = new Canvas();
        can1.setBackground(Color.DARK_GRAY);    
        can1.setSize(500, 500);
        can1.addMouseMotionListener(this);
        add(can1);
        
        setSize(650, 500);
        setTitle("Server Monitor");
        setLayout(null); //setLayout(new FlowLayout());
        setVisible(true);
        setResizable(false);
        addWindowListener( closeWin() );
        
        timer = new Timer(5000, this);
        timer.start();
    }
    
    public void listening()
    {
        Thread lThread = new Thread() {
            public void run() {
                Socket client = null;
                do {
                    try {
                        System.out.println("00. Listening for a connection...");
                        client = socket.accept();
                    } catch (IOException e) {
                        System.out.println("XX. Accept failed: " + e);
                        listen = false;
                    }
                    
                    if (listen) {
                        System.out.println("01. Adding New Client");
                        addClient(client);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("XX. Sleep failed: " + e);
                    }
                } while (listen);
            }
        };
        lThread.start();
    }
    
    private void addClient(Socket client)
    {
        if (client == null)
            return;
        
        if (list.size() > maxCon) {
            try {
                client.close();
            } catch (Exception e) {
                System.out.println("Exception at disconnect: " + e.toString());
            }
            return;
        }
        
        ThreadedConnectionHandler con = new ThreadedConnectionHandler(client);
        con.start();
        list.add(con);
    }
    
    private void verify()
    {
        java.util.List<ThreadedConnectionHandler> rlist = 
            new ArrayList<ThreadedConnectionHandler>();
        
        int      margin = Integer.parseInt( cho1.getSelectedItem() );
        String   msg    = "";
        Graphics graph  = can1.getGraphics();
        
        graph.setColor(Color.DARK_GRAY);
        graph.clearRect(0, 0, 500, 500);
        for (ThreadedConnectionHandler con : list) {
            if (!con.isAlive()) {
                rlist.add(con);
                continue;
            }
            Status stat1 = con.getStatus();
            
            // print coordinates
            printPoint(graph, con.getHistorical(3), new Color(255,255,255));
            printPoint(graph, con.getHistorical(2), new Color(255,150,150));
            printPoint(graph, con.getHistorical(1), new Color(255,102,102));
            printPoint(graph, stat1,                new Color(255,  0,  0));
            // check colisions
            boolean colision = false;
            if (stat1 != null) {
                for (ThreadedConnectionHandler check : list) {
                    Status stat2 = check.getStatus();
                    if (con != check && check.isAlive()  && 
                        stat1.isClose(stat2, margin)) {
                        con.setWarning("Warning of Colision");
                        msg += "Warning of Colision, robot: " + 
                            con.getStatus().getName() + "\n";
                        colision = true;
                        break;
                    }
                }
            }
            if (!colision)
                con.setAnswer("Status OK");
        }
        
        if (msg == "")
            txt3.setText("xxx");
        else
            txt3.setText(msg);
        
        // Delete/Remove unconnected clients
        for (ThreadedConnectionHandler con : rlist)
            list.remove(con);
        txt1.setText( Integer.toString(list.size()) );
    }
    
    private TextField createText(String label, int x, int y)
    {
        Label  lbl = new Label(label);
        TextField txt = new TextField("xxx");
        
        lbl.setBounds(x,    y, 50, 30);
        txt.setBounds(x+60, y, 70, 30);
        txt.setEditable(false);
        add(lbl);
        add(txt);
        return txt;
    }
    
    private Button createButton(String name, int x, int y)
    {
        Button btn = new Button(name);
        btn.setBounds(x, y, 120, 50);
        btn.addActionListener(this);
        add(btn);
        return btn;
    }
    
    private Choice createChoice(String label, int x, int y)
    {
        Label  lbl = new Label(label);
        Choice cho = new Choice();
        
        for (int i = 5; i <= 100; i+=5)
            cho.add(Integer.toString(i));
        cho.select(0);
        cho.setBounds(x+60, y, 50, 30);
        lbl.setBounds(x,    y, 50, 30);
        add(lbl);
        add(cho);
        return cho;
    }
    
    private TextArea createReporter(int x, int y)
    {
        TextArea txt = new TextArea("xxx", 20, 10, TextArea.SCROLLBARS_NONE);
        txt.setBounds(x, y, 135, 220);
        txt.setEditable(false);
        add(txt);
        return txt;
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == timer) {
            verify();
        } else if (e.getSource() == btn1) {
            for (ThreadedConnectionHandler con : list) {
                if (con.isAlive())
                    con.setError("SHUTDOWN");
            }
        }
    }
    
    public void printPoint(Graphics graph, Status stat, Color color)
    {
        if (stat == null)
            return;
        
        graph.setColor(color);
        graph.fillOval(stat.getX(),
                       stat.getY(),
                       stat.getSize(),
                       stat.getSize());
    }
    
    public void mouseMoved(MouseEvent e)
    {
        boolean set = false;
        
        for (ThreadedConnectionHandler con : list) {
            if (con.isAlive() && con.getStatus().isClose(e.getX(), e.getY())) {
                txt2.setText(con.getStatus().getName());
                txt4.setText(Integer.toString(con.getStatus().getZ()));
                set = true;
                break;
            }
        }
        
        if (!set) {
            txt2.setText("xxx");
            txt4.setText("xxx");
        }
    }
    
    public void mouseDragged(MouseEvent e) { }
    
    private WindowAdapter closeWin()
    {
        return new WindowAdapter() {
            public void windowClosing (WindowEvent e) {
                listen = false;
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.err.println("XX. Could not close server socket. " + 
                        ex.getMessage());
                }
                dispose();
                System.out.println("Windows was closed.");
                System.exit(0);
            }
        };
    }
}
