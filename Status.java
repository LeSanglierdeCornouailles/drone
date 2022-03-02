import java.lang.Math;
import java.io.Serializable;

public class Status implements Serializable
{
    enum Orientation { NORTH, SOUTH, EAST, WEAST }
    
    static int LIM_MIN = 0;
    static int LIM_MAX = 498;
    
    private int         asked;
    private int         x, y, z;
    private int         size;
    private int         speed;
    private int         height;
    private String      name;
    private String      time;
    private Orientation orientation;
    
    public Status(String robot, int size)
    {
        this.name = robot;
        this.size = size;
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.orientation = Orientation.NORTH;
        this.speed = 0;
        this.time = "";
        this.asked = 0;
    }
    
    /*
     * ● The server should calculate the distance between each robot and
     * evaluate if two robots are in danger of colliding using the current
     * position and the size of the robots in question. For example, this
     * could be performed by asking if the 
     * distance between the two robots > sum of the radii + a safety margin?
     */
    public boolean isClose(Status other, int margin)
    {
        double distance = 0.0;
        double radio    = 0.0;
        
        if (other == null || other == this)
            return false;
        
        distance = Math.pow(x - other.x, 2) + 
                   Math.pow(y - other.y, 2) + 
                   Math.pow(z - other.z, 2);
        distance = Math.sqrt(distance);
        
        radio = size + other.size;
        
        if (distance > (radio + margin))
            return false;
        return true;
    }
    
    public void ask()
    {
        DateTimeService tmp = new DateTimeService();
        
        time = tmp.getDateTime();
        asked++;
    }
    
    public boolean move()
    {
        int     tmp = 0;
        boolean res = true;
        
        if (speed != 0) {
            switch (orientation) {
            case NORTH:
                tmp = y - speed;
                break;
            case SOUTH:
                tmp = y + speed;
                break;
            case WEAST:
                tmp = x - speed;
                break;
            case EAST:
                tmp = x + speed;
                break;
            }
            
            if (tmp < LIM_MIN || tmp > LIM_MAX) {
                res = false;
            } else {
                if (orientation == Orientation.NORTH || 
                    orientation == Orientation.SOUTH)
                    y = tmp;
                else
                    x = tmp;
            }
        }
        
        if (height != 0) {
            tmp = z + height;
            if (tmp < LIM_MIN || tmp > LIM_MAX)
                res = false;
            else
                z = tmp;
        }
        return res;
    }
    
    public String getCoords()
    {
        String dir = "";
        switch (orientation) {
        case NORTH:
            dir = "NORTH";
            break;
        case SOUTH:
            dir = "SOUTH";
            break;
        case WEAST:
            dir = "WEAST";
            break;
        case EAST:
            dir = "EAST";
            break;
        }
        return " x=" + x + "\n y=" + y + "\n z=" + z + 
            "\n Direction=" + dir;
    }
    
    public String toString()
    {
        String res;
        
        res =  "Name:    " + name + "\n";
        res += "Coords:  (" + x + "," + y + "," + z + ")\n";
        res += "Speed:   " + speed + "\n";
        res += "Time:    " + time + "\n";
        return res;
    }
    
    public boolean isClose(int x, int y)
    {
        if (this.x == x && this.y == y)
            return true;
        
        if (Math.abs(this.x - x) < this.size &&
            Math.abs(this.y - y) < this.size)
            return true;
        return false;
    }
    
    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getSize() { return size; }
    public int getAsked() { return asked; }
    public String getTime() { return time; }
    
    public void setOrientation(Orientation orient)
    {
        orientation = orient;
    }
    
    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }
    
    public void setCoords(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
