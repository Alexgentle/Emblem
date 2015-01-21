/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mainPack;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;

/**
 *
 * @author Dasty
 */
public class Main extends JFrame
{
    private JScrollPane consolePane;
    private JTextArea console;
    private JTextField inputField;
    private JPanel mainPanel;
    private static DatagramSocket srvr;
    private Game mainGame;
    public Messages msg;
    private SecureRandom random = new SecureRandom();
    private Timer timer = new Timer();
    
    public Main()
    {
        super("Network Server");
        setSize(WIDTH, HEIGHT);
        initComponents();
        console.setEditable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                cleanUp();
                ((JFrame)(e.getComponent())).dispose();
            }
        });
        mainGame = new Game(this);
        msg = new Messages();
        try 
        {
            srvr = new DatagramSocket(8888);
        }
        catch(Exception e) 
        {

        }
    }
    
    public void waitForData()
    {
        displayMessage("[SERVER] Started server on port "+srvr.getLocalPort());
        while(true)
        {
            try
            {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                srvr.receive(receivePacket);
                
                if(receivePacket.getAddress() != null)
                {
                    String data = new String(receivePacket.getData());
                    System.out.println(data);
                    if(data.contains(""+msg.FIRST_TIME_CONNECTION))
                    {
                        final Player newPlayer = new Player(receivePacket.getAddress(),generateID());
                        if(!mainGame.players.contains(newPlayer))
                        {
                            mainGame.players.add(newPlayer);
                        
                            displayMessage("[SERVER] New Player Connecting From: <"+newPlayer.address+"> on "+newPlayer.ID);
                            

                            timer.schedule( new TimerTask(){
                                 public void run() { 
                                     sendDataToClient(msg.CONNECTION_SUCCESS, newPlayer, newPlayer.ID);
                                  }
                               }, 3000);

                            
                            sendDataToAllClientsBut(msg.NEW_PLAYER_CONNECTED, newPlayer.ID,newPlayer);
                        }
                    }
                    else
                    {
                        String tempID = data.substring(0, data.indexOf(":"));
                        Player findPlayer = new Player(receivePacket.getAddress(),tempID);
                        processData(data.substring(data.indexOf(":")+1),mainGame.players.get(mainGame.players.indexOf(findPlayer)));
                    }
                }
            }
            catch(Exception io)
            {
                displayMessage(io.toString() );
                io.printStackTrace();
            }
        }
    }
    
    public void processData(String data,Player p)
    {
        displayMessage("[SERVER] Recieved: "+data.trim()+" From: <"+p.address+"> on "+p.ID);
        if(data.contains(","))
        {
            
        }
        else
        {
            if(data.contains(msg.CLIENT_DISCONNECTED+""))
            {
                closePlayer(p, true);
            }
            else if(data.contains(msg.CLIENT_KEY_DOWN_W+""))
            {
                p.KEY_DOWN_W = true;
            }
            else if(data.contains(msg.CLIENT_KEY_DOWN_A+""))
            {
                p.KEY_DOWN_A = true;
            }
            else if(data.contains(msg.CLIENT_KEY_DOWN_S+""))
            {
                p.KEY_DOWN_S = true;
            }
            else if(data.contains(msg.CLIENT_KEY_DOWN_D+""))
            {
                p.KEY_DOWN_D = true;
            }
            else if(data.contains(msg.CLIENT_KEY_UP_W+""))
            {
                p.KEY_DOWN_W = false;
            }
            else if(data.contains(msg.CLIENT_KEY_UP_A+""))
            {
                p.KEY_DOWN_A = false;
            }
            else if(data.contains(msg.CLIENT_KEY_UP_S+""))
            {
                p.KEY_DOWN_S = false;
            }
            else if(data.contains(msg.CLIENT_KEY_UP_D+""))
            {
                p.KEY_DOWN_D = false;
            }
            else if(data.contains(msg.SET_ME_UP+""))
            {
                sendDataToClient(msg.CONNECTION_SUCCESS, p, p.ID);
            }
        }
    }
    
    //Sends message ID m to Player p.
    public void sendDataToClient(int m, Player p, String xData)
    {
        try
        {
            String message = ""+m;
            if(!xData.equals(""))
                message+=","+xData;
            System.out.println(message);
            byte data[] = message.getBytes();
            DatagramPacket sendPacket;
            sendPacket = new DatagramPacket(data,data.length,p.address,9999);
            srvr.send(sendPacket);
        }
        catch(Exception e)
        {
            displayMessage("[SERVER] Failed to send data to Player at <"+p.address+"> on "+9999);
            closePlayer(p, true);
        }
    }
    
    public void sendDataToAllClients(int m,String xData)
    {

        String message = ""+m;
        if(!xData.equals(""))
            message+=","+xData;
        byte data[] = message.getBytes();

        for(int i = 0;i<mainGame.players.size();i++)
        {
            try
            {
                DatagramPacket sendPacket;
                sendPacket = new DatagramPacket(data,data.length,mainGame.players.get(i).address,9999);
                srvr.send(sendPacket);
            }
            catch(Exception e)
            {
                displayMessage("[SERVER] Failed to send data to Player at <"+mainGame.players.get(i).address+"> on "+9999);
                closePlayer(mainGame.players.get(i), true);
            }
        }
    }
    
    public void sendDataToAllClientsBut(int m,String xData, Player p)
    {

        String message = ""+m;
        if(!xData.equals(""))
            message+=","+xData;
        byte data[] = message.getBytes();

        for(int i = 0;i<mainGame.players.size();i++)
        {
            if(!mainGame.players.get(i).equals(p))
            {
                try
                {
                    DatagramPacket sendPacket;
                    sendPacket = new DatagramPacket(data,data.length,mainGame.players.get(i).address,9999);
                    srvr.send(sendPacket);
                }
                catch(Exception e)
                {
                    displayMessage("[SERVER] Failed to send data to Player at <"+mainGame.players.get(i).address+"> on "+9999);
                    closePlayer(mainGame.players.get(i), true);
                }
            }
        }
    }
    
    private void closePlayer(Player p, boolean f)
    {
        if(!f)
            sendDataToClient(msg.DISCONNECTED_FROM_SERVER,p,"");
        displayMessage("[SERVER] Closing connection for player <"+p.address+"> on "+p.ID);
        sendDataToAllClients(msg.CLIENT_DISCONNECTED,p.ID);
        mainGame.players.remove(p);
    }
    
    private void displayMessage(final String messageToDisplay)
    {
        SwingUtilities.invokeLater(
                new Runnable()
                {
                    public void run()
                    {
                        console.append(messageToDisplay+"\n");
                    }
                }
        );
    }
    
    private void initComponents() 
    {

        mainPanel = new JPanel();
        consolePane = new JScrollPane();
        console = new JTextArea();
        inputField = new JTextField();

        mainPanel.setName("mainPanel"); 

        consolePane.setName("consolePane"); 

        console.setColumns(20);
        console.setRows(5);
        console.setName("console"); 
        consolePane.setViewportView(console);

        inputField.setName("inputField");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inputField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                    .addComponent(consolePane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(consolePane, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inputField, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(mainPanel);
        pack();
    }
    
    public void cleanUp()
    {
        try
        {
            for(int i = 0; i < mainGame.players.size(); i++)
            {
                closePlayer(mainGame.players.get(i),false);
            }
            srvr.close();
        }
        catch(Exception e)
        {
            
        }
    }
    
    public String generateID()
    {
        return new BigInteger(130, random).toString(32);
    }
    
    public static void main(String[] args)
    {
        Main m = new Main();
        m.setVisible(true);
        m.waitForData();
        

    }
}
