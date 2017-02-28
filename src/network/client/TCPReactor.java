package network.client;

import java.io.IOException;
import java.net.Socket;

public class TCPReactor {
	
	private Socket sock;
	private String ip;
	private int port;
	
	public TCPReactor(String ip, int port){
		
		this.ip = ip;
		this.port = port;
		 
		
	}
	
	public Socket getSocket(){
		
		try {
			
			this.sock = new Socket(ip, port);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return this.sock;
		
	}
	
	
}
