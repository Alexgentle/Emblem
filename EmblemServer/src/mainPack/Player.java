/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;

import java.net.InetAddress;

/**
 *
 * @author Dasty
 */
public class Player 
{
    public InetAddress address;
    public boolean KEY_DOWN_W;
    public boolean KEY_DOWN_A;
    public boolean KEY_DOWN_S;
    public boolean KEY_DOWN_D;
    
    public String ID;
    
    public Vector3 position;
    public Vector3 velocity;
    
    public Vector3 lastSentVelocity;
    
    
    
    public Player(InetAddress a, String i)
    {
        address = a;
        ID = i;
        
        position = new Vector3(5,1,5);
        velocity = new Vector3(0,0,0);
        lastSentVelocity = new Vector3(999,999,999);
    }
    
    public boolean isMoving()
    {
        return KEY_DOWN_W || KEY_DOWN_A || KEY_DOWN_S || KEY_DOWN_D; 
    }
    
    @Override
    public boolean equals(Object o)
    {
        Player temp = (Player)o;
        if(temp.ID.equals(ID))
        {
            return true;
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "<"+address+"> - ("+ID+")";
    }
    
    
}
