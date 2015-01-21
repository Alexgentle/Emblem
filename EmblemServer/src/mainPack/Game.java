/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;

import java.util.ArrayList;

/**
 *
 * @author Dasty
 */
public class Game implements Runnable
{
    public ArrayList<Player> players = new ArrayList();
    public ArrayList<Player> playersToAdd = new ArrayList();
    private Main sender;
    private Thread gameLoopThread;
    
    public Vector3 FORWARD = new Vector3(1.0,0.0,0.0);
    public Vector3 LEFT = new Vector3(0.0,0.0,1.0);
    public Vector3 RIGHT = new Vector3(0.0,0.0,-1.0);
    public Vector3 BACK = new Vector3(-1.0,0.0,0.0);
    
    public Game(Main s)
    {
        sender = s;
        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }
    
    private void Update(double timeChange)
    {
        for(Player player : players)
        {
            
            if(player.isMoving())
            {
                player.velocity = new Vector3(0,0,0);
                if(player.KEY_DOWN_W)
                {
                    player.velocity.add(FORWARD);
                }
                if(player.KEY_DOWN_A)
                {
                    player.velocity.add(LEFT);
                }
                if(player.KEY_DOWN_S)
                {
                    player.velocity.add(BACK);
                }
                if(player.KEY_DOWN_D)
                {
                    player.velocity.add(RIGHT);
                }
                Vector3 tempVel = player.velocity;
                tempVel.mult(timeChange);
                tempVel.mult(2.5);
                player.position.add(tempVel);
            }
            else
            {
                player.velocity = new Vector3(0.0,0.0,0.0);
            }
            if(!player.lastSentVelocity.equals(player.velocity))
            {
                sender.sendDataToAllClients(sender.msg.SET_VELOCITY, player.ID+","+player.position+","+player.velocity);
                player.lastSentVelocity = player.velocity;
                
            }
        }
    }
    
    @Override
    public void run() 
    {
        double previousTime = System.nanoTime();
        double secondsPerTick = 1 / 60.0;
        while(true)
        {
            double currentTime = System.nanoTime();
            double passedTime = currentTime - previousTime;
            
            
            if(passedTime/1000000000.0 > secondsPerTick)
            {
                previousTime = currentTime;
                Update(passedTime/1000000000.0);
                for(int i = 0; i<playersToAdd.size();i++)
                {
                    players.add(playersToAdd.get(i));
                }
                playersToAdd.clear();
            }
        }
    }
}
