package com.game.one.model;

public class UserData
{

    // private variables
    int _id;
    String _name;
    String _data;

    // Empty constructor
    public UserData()
    {

    }

    // constructor
    public UserData(int id, String name, String data)
    {
        this._id = id;
        this._name = name;
        this._data = data;
    }

    // constructor
    public UserData(String name, String data)
    {
        this._name = name;
        this._data = data;
    }

    // getting ID
    public int getID()
    {
        return this._id;
    }

    // setting id
    public void setID(int id)
    {
        this._id = id;
    }

    // getting name
    public String getName()
    {
        return this._name;
    }

    // setting name
    public void setName(String name)
    {
        this._name = name;
    }

    // getting email
    public String getData()
    {
        return this._data;
    }

    // setting email
    public void setData(String data)
    {
        this._data = data;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "UserInfo [name=" + _name + ", data=" + _data + "]";
    }

}