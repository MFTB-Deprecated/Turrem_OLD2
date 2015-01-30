package net.turrem.app.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkConnection
{
	public Socket network;
	
	private volatile DataInputStream input;
	private volatile DataOutputStream output;
	
	public NetworkConnection(Socket network) throws IOException
	{
		this.network = network;
		
		this.input = new DataInputStream(this.network.getInputStream());
		this.output = new DataOutputStream(this.network.getOutputStream());
	}
	
	public DataInputStream getInput()
	{
		return this.input;
	}
	
	public DataOutputStream getOutput()
	{
		return this.output;
	}
}
