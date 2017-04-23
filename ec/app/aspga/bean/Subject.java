/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga.bean;

/**
 * The Subject class that contains it features. <br/>
 * The subject is formed by: <br/><br/>
 *
 * <code>int</code> ID <br/>
 * <code>{@link String}</code> NAME <br/>
 * <code>int</code> DIFFICULT <br/>
 *
 * @author Victor Jatoba
 * @version Mon Nov 26 02:29 2013
 */
public class Subject {

    private static final byte MIN_DIFFICULTY_VALUE = 0;
    private static final byte MAX_DIFFICULTY_VALUE = 100;

	private int     id;
	private String  name;
	private byte     difficulty; //from 0 to 100

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public byte getDifficulty()
	{
		return this.difficulty;
	}

	public void setDifficulty(byte difficulty)
	{
        if( difficulty < MIN_DIFFICULTY_VALUE ){
            difficulty = MIN_DIFFICULTY_VALUE;
        }
        else if( difficulty > MAX_DIFFICULTY_VALUE ){
            difficulty = MAX_DIFFICULTY_VALUE;
        }
		this.difficulty = difficulty;
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;

        Subject subject = (Subject) o;

        if (id != subject.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
