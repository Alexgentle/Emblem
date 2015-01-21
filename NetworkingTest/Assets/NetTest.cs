using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Collections;
using System.Collections.Generic;
using System.Text.RegularExpressions;

public class NetTest : MonoBehaviour {
	
	Socket sendSocket;
	IPEndPoint sender;
	UdpClient inSocket;
	Mssages msg = new Mssages();
	List<EPlayer> players = new List<EPlayer>();
	private GameObject PlayerObj; 
	private LocalPlayerS s; 
	private GameObject localPlayer;
	
	bool key_down_w = false;
	bool key_down_a = false;
	bool key_down_s = false;
	bool key_down_d = false;
	
	bool connected = false;
	
	bool received = false;
	
	byte[] receiveBytes;
	
	// Use this for initialization
	void Start () 
	{
		PlayerObj = Resources.Load("Player") as GameObject; 
		
		sendSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram,ProtocolType.Udp);
		IPAddress send_to_address = IPAddress.Parse("127.0.0.1");
		sender = new IPEndPoint(send_to_address, 8888);
		inSocket = new UdpClient(9999);
		IPEndPoint remoteIpEndPoint = new IPEndPoint(send_to_address, 9999);
		
		
		
		Thread listener = new Thread(()=>
		{
    		while(true)
    		{
        		try
        		{
					Debug.Log("Started");
            		receiveBytes = inSocket.Receive(ref remoteIpEndPoint);
					Debug.Log("Got stuff");
					received = true;
        		}
        		catch(SocketException)
        		{ 
					Debug.Log("Broke");
            		break; // exit the while loop
        		}
    		}
		});
		listener.IsBackground = true;
		listener.Start();
		Send(msg.FIRST_TIME_CONNECTION,"");
	}
	
	void Update()
	{
		if(received == true)
		{
			Receive(receiveBytes);
			received = false;
		}
		if(connected)
		{
			
			if(Input.GetKey(KeyCode.W))
			{
				if(key_down_w == false)
				{
					Debug.Log("W down");
					Send(msg.CLIENT_KEY_DOWN_W,"");
					key_down_w = true;
				}
			}
			else
			{
				if(key_down_w == true)
				{
					Debug.Log("W up");
					Send(msg.CLIENT_KEY_UP_W,"");
					key_down_w = false;
				}
			}
			if(Input.GetKey(KeyCode.A))
			{
				if(key_down_a == false)
				{
					Debug.Log("A down");
					Send(msg.CLIENT_KEY_DOWN_A,"");
					key_down_a = true;
				}
			}
			else
			{
				if(key_down_a == true)
				{
					Debug.Log("A up");
					Send(msg.CLIENT_KEY_UP_A,"");
					key_down_a = false;
				}
			}
			if(Input.GetKey(KeyCode.S))
			{
				if(key_down_s == false)
				{
					Debug.Log("S down");
					Send(msg.CLIENT_KEY_DOWN_S,"");
					key_down_s = true;
				}
			}
			else
			{
				if(key_down_s == true)
				{
					Debug.Log("S up");
					Send(msg.CLIENT_KEY_UP_S,"");
					key_down_s = false;
				}
			}
			if(Input.GetKey(KeyCode.D))
			{
				if(key_down_d == false)
				{
					Debug.Log("D down");
					Send(msg.CLIENT_KEY_DOWN_D,"");
					key_down_d = true;
				}
			}
			else
			{
				if(key_down_d == true)
				{
					Debug.Log("D up");
					Send(msg.CLIENT_KEY_UP_D,"");
					key_down_d = false;
				}
			}
		}
		else
		{
			//Send(msg.SET_ME_UP,"");
		}
	}
	
	void AddNewPlayer(string i)
	{
		Vector3 newPos = new Vector3(5,1,5);
		Quaternion rot = Quaternion.Euler(0, 0, 0);
		GameObject newP = Instantiate(PlayerObj,newPos,rot) as GameObject;
		EPlayer ep = newP.GetComponent(typeof(EPlayer)) as EPlayer;
		ep.id = i;
		ep.position = new Vector3(5,1,5);
		ep.velocity = new Vector3(0,0,0);
		players.Add(ep);	
	}
	
	void CreateLocalPlayer(string i)
	{
		Vector3 newPos = new Vector3(5,1,5);
		Quaternion rot = Quaternion.Euler(0, 0, 0);
		localPlayer = GameObject.Find("LocalPlayer");
		do{
			s = localPlayer.GetComponent(typeof(LocalPlayerS)) as LocalPlayerS;
		}while(s == null);
		s.id = i;
		s.position = new Vector3(5,1,5);
		s.velocity = new Vector3(0,0,0);
		connected = true;
	}
	
	void Send(int m, string xData) 
	{

		String sendData = "";
		if(connected)
		{
			sendData = s.id+":";
		}
		sendData += m+"";
		if(xData != "")
		{
			sendData += ","+xData;	
		}
		Debug.Log(sendData);
        byte[] data = System.Text.Encoding.ASCII.GetBytes(sendData);
      	sendSocket.SendTo(data, sender);
		

	}
	
	
	void Receive(byte[] buffer)
	{
		string data = Encoding.ASCII.GetString(buffer);
		Debug.Log(data);
		if(data.Contains(","))
        {
			if(data.Contains(msg.CONNECTION_SUCCESS+""))
			{
				CreateLocalPlayer(data.Substring(data.IndexOf(",")+1));
				Debug.Log("Creating");
			}
			if(connected)
			{
	            if(data.Contains(msg.NEW_PLAYER_CONNECTED+""))
				{
					AddNewPlayer(data.Substring(data.IndexOf(",")+1));
				}
				else if(data.Contains(msg.SET_VELOCITY+""))
				{
					String id = data.Substring(data.IndexOf(",")+1,NthIndexOf(",",data,2)-data.IndexOf(",")-1);
					String posxs = data.Substring(NthIndexOf(",",data,2)+1,NthIndexOf(",",data,3)-NthIndexOf(",",data,2)-1);
					String posys = data.Substring(NthIndexOf(",",data,3)+1,NthIndexOf(",",data,4)-NthIndexOf(",",data,3)-1);
					String poszs = data.Substring(NthIndexOf(",",data,4)+1,NthIndexOf(",",data,5)-NthIndexOf(",",data,4)-1);
					String velxs = data.Substring(NthIndexOf(",",data,5)+1,NthIndexOf(",",data,6)-NthIndexOf(",",data,5)-1);
					String velys = data.Substring(NthIndexOf(",",data,6)+1,NthIndexOf(",",data,7)-NthIndexOf(",",data,6)-1);
					String velzs = data.Substring(NthIndexOf(",",data,7)+1);
					float posx = convertStringToFloat(posxs);
					float posy = convertStringToFloat(posys);
					float posz = convertStringToFloat(poszs);
					float velx = convertStringToFloat(velxs);
					float vely = convertStringToFloat(velys);
					float velz = convertStringToFloat(velzs);
					Debug.Log(s);
					if(id == s.id)
					{
						s.velocity = new Vector3(velx,vely,velz);
						s.position = new Vector3(posx,posy,posz);
					}
					else
					{
						for(int i = 0;i < players.Count;i++)
						{
							EPlayer ps = (EPlayer)players[i];
							if(ps.id == id)
							{
								ps.velocity = new Vector3(velx,vely,velz);
								ps.position = new Vector3(posx,posy,posz);
							}
						}
					}
				}
			}
        }
        else
        {
            
        }
	}
	
	public int NthIndexOf(string value, string target, int n)
    {
        Match m = Regex.Match(target, "((" + value + ").*?){" + n + "}");

        if (m.Success)
            return m.Groups[2].Captures[n - 1].Index;
        else
            return -1;
    }
	
	public float convertStringToFloat(string s)
	{
		decimal decval = System.Convert.ToDecimal(s);
        double doubleval = System.Convert.ToDouble(decval);
        float fval = System.Convert.ToSingle(doubleval);
		
		return fval;
	}
	
	void OnApplicationQuit()
	{
		Send(msg.CLIENT_DISCONNECTED,"");	
		sendSocket.Close();
		inSocket.Close();
	}
}



