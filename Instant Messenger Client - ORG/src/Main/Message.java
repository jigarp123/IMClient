package Main;

public class Message
{
	private String message;
	private String name;
	private boolean mine;

	public Message(String name, String message, boolean mine)
	{
		this.message = message;
		this.name = name;
		this.mine = mine;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getMessage()
	{
		return message;
	}

	public String getName()
	{
		return name;
	}

	public boolean getMine()
	{
		return mine;
	}

	public void setMine(boolean mine)
	{
		this.mine = mine;
	}
}
