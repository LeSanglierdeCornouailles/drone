import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientFrame extends Frame implements ActionListener
{
    // Process
    private int              counter;
    private Status           stat;
    private boolean          listen;
    private Message          message;
    private ClientConnection conn;
    // GUI
    private Timer    timer;
    private Choice   ch1;
    private Choice   ch2;
    private Button   b1;
    private Button   b2;
    private Button   b3;
    private Button   b4;
    private TextArea tf1;
    private TextArea tf2;
    
    public ClientFrame(String ip, int port, String name, int size)
    {
        stat    = new Status(name, size);
        conn    = new ClientConnection(ip, port);
        listen  = true;
        counter = 0;
        message = new Message();
    }
    
    public void run()
    {
        b1 = createButton("North", 60, 50);
        b2 = createButton("South", 60, 100);
        b3 = createButton("East", 110, 100);
        b4 = createButton("Weast", 10, 100);
        
        tf1 = createReporter(150, 160);
        tf2 = createReporter( 10, 160);
        
        ch1 = createChoice("Speed:",  180, 60);
        ch2 = createChoice("Height:", 180, 110);
        
        setSize(300, 250);
        setTitle("Robot [" + stat.getName() + "]");
        setLayout(null); //setLayout(new FlowLayout());
        setVisible(true);
        setResizable(false);
        addWindowListener( closeWin() );
        stat.setCoords(250, 250, 0);
        
        timer = new Timer(1000, this);
        timer.start();
        
        if (!conn.connect())
            System.exit(-1);
    }
    
    private Button createButton(String name, int x, int y)
    {
        Button btn = new Button(name);
        btn.setBounds(x, y, 50, 50);
        btn.addActionListener(this);
        add(btn);
        return btn;
    }
    
    private TextArea createReporter(int x, int y)
    {
        TextArea txt = new TextArea("xxx", 20, 2, TextArea.SCROLLBARS_NONE);
        txt.setBounds(x, y, 135, 85);
        txt.setEditable(false);
        add(txt);
        return txt;
    }
    
    private Choice createChoice(String label, int x, int y)
    {
        Label  lbl = new Label(label);
        Choice cho = new Choice();
        
        for (int i = -2; i <= 6; i+=2)
            cho.add(Integer.toString(i));
        cho.select(1);
        cho.addItemListener( changeChoice() );
        cho.setBounds(x+50, y, 50, 30);
        lbl.setBounds(x,    y, 50, 30);
        add(lbl);
        add(cho);
        return cho;
    }
    
    private WindowAdapter closeWin()
    {
        return new WindowAdapter() {
            public void windowClosing (WindowEvent e) {
                closeAll("Windows was closed.");
            }
        };
    }
    
    public void closeAll(String msg)
    {
        timer.stop();
        listen = false;
        conn.disconnect();
        dispose();
        System.out.println(msg);
        System.exit(0);
    }
    
    private ItemListener changeChoice()
    {
        return new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                if (e.getSource() == ch1) {
                    String tmp = ch1.getSelectedItem();
                    stat.setSpeed(Integer.parseInt(tmp));
                } else if (e.getSource() == ch2) {
                    String tmp = ch2.getSelectedItem();
                    stat.setHeight(Integer.parseInt(tmp));
                }
            }
        };
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == b1)
            stat.setOrientation(Status.Orientation.NORTH);
        else if (e.getSource() == b2)
            stat.setOrientation(Status.Orientation.SOUTH);
        else if (e.getSource() == b3)
            stat.setOrientation(Status.Orientation.EAST);
        else if (e.getSource() == b4)
            stat.setOrientation(Status.Orientation.WEAST);
        else if (e.getSource() == timer)
            verify();
    }

    public void verify()
    {
        counter++;
        stat.move();
        tf2.setText( stat.getCoords() );
        
        if (counter >= 10) {
            // Sending
            stat.ask();
            //System.out.println("Snd: " + stat.getX() + "/" + stat.getY() );
            if (!conn.send(stat)) {
                System.out.println("Error at sending.");
                System.exit(-1);
            }
            // Receiving
            Object obj = conn.receive();
            if (obj != null)
                message = (Message) obj;
                
            tf1.setText(
                "Package: " + stat.getAsked() + "\n" +
                "Date: " + stat.getTime() + "\n" +
                "Answer: " + message.getText() );
            if (message.getType() == Message.Type.SHUTDOWN)
                closeAll("Shutdown received.");
            // Restart counting
            counter = 0;
        }
    }
}
