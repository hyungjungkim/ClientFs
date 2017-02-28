package db.domain;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class HandleInfo {
	
	private Socket sock;
	private FileInfo fileInfo;
	//private ObjectInputStream in;	
	//private ObjectOutputStream out;	
	private DataInputStream dis;
	private DataOutputStream dos;
	//private JSONObject jobj;
	private OutputStreamWriter writer = null;
	//private BufferedReader reader = null;
	public HandleInfo(Socket sock, FileInfo fileInfo,OutputStreamWriter writer, DataInputStream dis, DataOutputStream dos) {
		this.writer = writer;
		this.sock = sock;
		this.fileInfo = fileInfo;
		//this.reader = reader;
		this.dis = dis;
		this.dos = dos;
		//this.jobj = jsonObject;
	}

	public OutputStreamWriter getWriter() {
		return writer;
	}

	public void setWriter(OutputStreamWriter writer) {
		this.writer = writer;
	}

//	public BufferedReader getReader() {
//		return reader;
//	}
//
//	public void setReader(BufferedReader reader) {
//		this.reader = reader;
//	}

	public Socket getSock() {
		return sock;
	}

	public FileInfo getFileInfo() {
		return fileInfo;
	}

	public void setSock(Socket sock) {
		this.sock = sock;
	}

	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

//	public ObjectOutputStream getOut() {
//		return out;
//	}
//
//	public void setOut(ObjectOutputStream out) {
//		this.out = out;
//	}
//
//	public ObjectInputStream getIn() {
//		return in;
//	}
//
//	public void setIn(ObjectInputStream in) {
//		this.in = in;
//	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}
	
	
	
}
