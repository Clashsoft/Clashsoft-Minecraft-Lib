package clashsoft.cslib.src.parser;

import java.util.Objects;

import clashsoft.cslib.src.SyntaxException;

public class Token implements IToken
{
	private IToken			prev;
	private IToken			next;
	
	private int				index;
	private final String	value;
	private final byte		type;
	private final int		start;
	private final int		end;
	
	public Token(int index, String value, byte type, String code)
	{
		this.index = index;
		this.value = value;
		this.type = type;
		this.start = code.indexOf(value);
		this.end = this.start + value.length();
	}
	
	public Token(int index, String value, byte type, int start, int end)
	{
		this.index = index;
		this.value = value;
		this.type = type;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public String value()
	{
		return this.value;
	}
	
	@Override
	public byte type() throws SyntaxException
	{
		return this.type;
	}
	
	@Override
	public boolean equals(String value)
	{
		return Objects.equals(this.value, value);
	}
	
	@Override
	public boolean isType(byte type) throws SyntaxException
	{
		return this.type == type;
	}
	
	@Override
	public int index()
	{
		return this.index;
	}
	
	@Override
	public int start()
	{
		return this.start;
	}
	
	@Override
	public int end()
	{
		return this.end;
	}
	
	@Override
	public IToken prev()
	{
		if (this.prev == null)
		{
			this.prev = new FakeToken();
			this.prev.setNext(this);
		}
		return this.prev;
	}
	
	@Override
	public IToken next()
	{
		if (this.next == null)
		{
			this.next = new FakeToken();
			this.next.setPrev(this);
		}
		return this.next;
	}
	
	@Override
	public boolean hasNext()
	{
		return this.next != null;
	}
	
	@Override
	public boolean hasPrev()
	{
		return this.prev != null;
	}
	
	@Override
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	@Override
	public void setPrev(IToken prev)
	{
		this.prev = prev;
	}
	
	@Override
	public void setNext(IToken next)
	{
		this.next = next;
	}
	
	@Override
	public String toString()
	{
		return "Token #" + this.index + ": \"" + this.value + "\" (" + this.start + "-" + this.end + ")";
	}
}
