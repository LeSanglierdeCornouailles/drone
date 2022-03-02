import java.io.Serializable;

public class Message implements Serializable
{
    enum Type { ANSWER, WARNING, SHUTDOWN }
    
    private Type   type;
    private String text;
    
    public Message()
    {
        this.type = Type.ANSWER;
        this.text = "";
    }
    
    public Message(String text, Type type)
    {
        this.type = type;
        this.text = text;
    }
    
    public Type getType() { return this.type; }
    public String getText() { return this.text; }
    
    public void setType(Type type)
    {
        this.type = type;
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
}
