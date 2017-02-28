package fileprocessor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;

import com.google.gson.Gson;

import db.domain.DirFile;
import db.domain.FileInfo;
import db.domain.ListInfor;
import db.domain.RequestInfo;
import db.domain.ServiceNum;

public class FileClientLogic implements FileClient {
	//
	// Need Default Path

	//private static String defaultPath = "C:\\Users\\Administrator\\Desktop\\fileServer";
	private static String defaultPath = "C:/Users/Administrator/Desktop/fileServer";
	
	private Socket sock;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private FileInputStream fis = null;
	private FileOutputStream fos = null;
	private OutputStreamWriter writer = null;
	private BufferedReader reader = null;
	private BufferedInputStream bis = null;
	//
	RequestInfo rqInfo = null;

	public FileClientLogic(Socket sock) {
		this.sock = sock;
	}

	@Override
	public List<DirFile> FileUpload(String userId, String localPath, String serverPath)
			throws IOException, ClassNotFoundException {

		FileInfo fileInfo = new FileInfo(userId, localPath, serverPath, ServiceNum.UPLOAD);
		String fileInfoStr = new Gson().toJson(fileInfo);

		System.out.println("fileInfo serverPath : " + fileInfo.getNewPath());
		System.out.println("localPath in ClientLogic " + localPath);
		System.out.println("fileInfo localPath : " + fileInfo.getCurrentPath());
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		writer.write(fileInfoStr + "\n");
		writer.flush();

		dos = new DataOutputStream(sock.getOutputStream());
		fis = new FileInputStream(localPath);
		ListInfor retList = null;
		byte[] contentBytes = new byte[1024];
		int bytes = 0;
		try {
			while (true) {
				int count = fis.read(contentBytes);
				System.out.println("count = " + count);

				if (count == -1) {
					break;
				}
				bytes += count;

			}
			fis.close();
			fis = new FileInputStream(localPath);
			dos.writeInt(bytes);

			while (true) {

				int count = fis.read(contentBytes);
				System.out.println("count = " + count);

				if (count == -1) {
					break;
				}
				dos.write(contentBytes, 0, count);

			}

			// fis.close();
		} catch (IOException e) {
			e.getStackTrace();
		}
		System.out.println("ClientLogic before Reader");
		String line = reader.readLine();
		System.out.println("ClientLogic after Reader");
		retList = new Gson().fromJson(line, ListInfor.class);
		System.out.println("ClientLogic retList : " + retList);
		return retList.getListInfor();
	}

	// FileDownload Method using InputStream from Server
	@Override
	public boolean FileDownload(String userId, String localPath) throws IOException {
		// TODO Auto-generated method stub
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");

		try {
			System.out.println(localPath);
			FileInfo fileInfo = new FileInfo(userId, localPath, "", ServiceNum.DOWNLOAD);
			String fileInfoStr = new Gson().toJson(fileInfo);
			System.out.println("client up: " + fileInfoStr);
			writer.write(fileInfoStr + "\n");
			writer.flush();
			System.out.println("okokokokokokokokokokowrite download");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("okokokokokokokokokokowrite downloadsdsdsdsdsdsdsdsds");
		byte[] contentBytes = new byte[1024];
		try {
			dis = new DataInputStream(sock.getInputStream());
			System.out.println("okokokokokokokokokokowrite downloasdasasgagqwgts,k7wwad");
			System.out.println(localPath.substring(localPath.lastIndexOf("/"), localPath.length()));
			System.out.println("clientLogic + path :"
					+ (defaultPath + localPath.substring(localPath.lastIndexOf("/"), localPath.length())));
			// defaultPath + fileName
			fos = new FileOutputStream(
					defaultPath + localPath.substring(localPath.lastIndexOf("/"), localPath.length()));
			System.out.println("clientLogic + path :"
					+ (defaultPath + localPath.substring(localPath.lastIndexOf("/"), localPath.length())));
			bis = new BufferedInputStream(sock.getInputStream());
			int bytes = dis.readInt();
			int count = 0;
			while (true) {
				
				int bytess = bis.read(contentBytes, 0, contentBytes.length); 
				System.out.println("FileUpload in Server Count : " + bytess);
				count += bytess;
				fos.write(contentBytes, 0, bytess);
				
				if(count >= bytes){
					System.out.println("���������پƾƾƾ�~~~");
					fos.close();
					break;
				}
				
			}
		} catch (IOException e) {
			e.getStackTrace();
		}
		return true;
	}

	// FileRemove Method
	@Override
	public List<DirFile> FileRemove(String userId, String currentPath) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		ListInfor retList = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));

		try {

			FileInfo fileInfo = new FileInfo(userId, currentPath, "", ServiceNum.RMFILE);
			String fileInfoStr = new Gson().toJson(fileInfo);
			System.out.println("client up: " + fileInfoStr);
			writer.write(fileInfoStr + "\n");
			writer.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = reader.readLine();
		retList = new Gson().fromJson(line, ListInfor.class);

		return retList.getListInfor();
	}

	// ChangeName
	@Override
	public List<DirFile> ChangeName(String userId, String currentPath, String newPath)
			throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		// need parsing
		ListInfor retList = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		// parsing path , need to separate File/Directory
		String fileName = newPath.substring(newPath.lastIndexOf("/"), newPath.length());

		try {

			FileInfo fileInfo = new FileInfo(userId, currentPath, newPath, ServiceNum.CNGFILENAME);
			String fileInfoStr = new Gson().toJson(fileInfo);
			System.out.println("client up: " + fileInfoStr);
			writer.write(fileInfoStr + "\n");
			writer.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = reader.readLine();
		retList = new Gson().fromJson(line, ListInfor.class);

		return retList.getListInfor();
	}

	// FileSearch
	@Override
	public List<DirFile> FileSearch(String userId, String searchName) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		// path + fileName;
		ListInfor retList = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		try {

			FileInfo fileInfo = new FileInfo(userId, searchName, "", ServiceNum.SEARCH);
			String fileInfoStr = new Gson().toJson(fileInfo);
			System.out.println("client up: " + fileInfoStr);
			writer.write(fileInfoStr + "\n");
			writer.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = reader.readLine();
		retList = new Gson().fromJson(line, ListInfor.class);
		return retList.getListInfor();
	}

	// DirectoryCreate
	@Override
	public List<DirFile> DirectoryCreate(String userId, String currentPath) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		ListInfor retList = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		try {

			FileInfo fileInfo = new FileInfo(userId, currentPath, "", ServiceNum.MKDIR);
			String fileInfoStr = new Gson().toJson(fileInfo);
			System.out.println("client up: " + fileInfoStr);
			writer.write(fileInfoStr + "\n");
			writer.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = reader.readLine();
		retList = new Gson().fromJson(line, ListInfor.class);
		return retList.getListInfor();
	}

	// DirectoryRemove
	@Override
	public List<DirFile> DirectoryRemove(String userId, String currentPath) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		ListInfor retList = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		try {

			FileInfo fileInfo = new FileInfo(userId, currentPath, "", ServiceNum.RMVDIR);
			String fileInfoStr = new Gson().toJson(fileInfo);
			System.out.println("client up: " + fileInfoStr);
			writer.write(fileInfoStr + "\n");
			writer.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = reader.readLine();
		retList = new Gson().fromJson(line, ListInfor.class);

		return retList.getListInfor();
	}

	// ShowList
	@Override
	public List<DirFile> ShowList(String userId, String currentPath) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		// first page & back page function
		ListInfor retList = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		System.out.println("ClientLogic : " + userId);
		// rqInfo = new RequestInfo(new FileInfo(userId, currentPath, "",
		// ServiceNum.SHOWLIST), userId,
		// ServiceNum.SHOWLIST);
		try {
			System.out.println(userId);
			FileInfo fileInfo = new FileInfo(userId, currentPath, "", ServiceNum.SHOWLIST);
			String fileInfoStr = new Gson().toJson(fileInfo);
			System.out.println(fileInfoStr);
			writer.write(fileInfoStr + "\n");
			writer.flush();

			System.out.println("fileinfoagain : " + fileInfoStr);
			String line = reader.readLine();
			System.out.println("line : " + line);
			retList = new Gson().fromJson(line, ListInfor.class);
			// retList = (ListInfor) jsonObject.get("list");

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return retList.getListInfor();
	}

	// CheckCopyCut
	@Override
	public List<DirFile> CheckCopyCut(int flag, String userId, String currentPath, String newPath) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		boolean success = true;
		List<DirFile> retList = null;
		FileInfo fileInfo = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		if (flag == 0)
			fileInfo = new FileInfo(userId, currentPath, newPath, ServiceNum.COPYPASTE);
		else
			fileInfo = new FileInfo(userId, currentPath, newPath, ServiceNum.CUTPASTE);

		// to Server
		try {
			String fileInfoStr = new Gson().toJson(fileInfo);
			writer.write(fileInfoStr + "\n");
			writer.flush();
			
			//from server
			String line = reader.readLine();
			success = new Gson().fromJson(line, Boolean.class);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// show List
		if (success) {
			try {
				retList = ShowList(userId, newPath);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			retList = null;
		}
		return retList;

	}

	@Override
	public boolean IsExistId(String userId) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		String ret = null;
		boolean success = false;
		FileInfo fileInfo = null;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		try {
			//to server
			fileInfo = new FileInfo(userId, "", "", ServiceNum.ISEXISTID);
			String fileInfoStr = new Gson().toJson(fileInfo);
			writer.write(fileInfoStr + "\n");
			writer.flush();
			//from server
			String line = reader.readLine();
			ret = new Gson().fromJson(line, String.class);
			if(ret.equals("true")) success = true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return success;
	}

	@Override
	public boolean RegistUser(String userId, String password, String Name) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		
		FileInfo fileInfo = null;
		String ret = null;
		boolean success = true;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		try {
			fileInfo = new FileInfo(userId, password, Name, ServiceNum.REGISTUSER);
			String fileInfoStr = new Gson().toJson(fileInfo);
			writer.write(fileInfoStr + "\n");
			writer.flush();
			//from server
			String line = reader.readLine();
			ret = new Gson().fromJson(line, String.class);
			if(ret.equals("true")) success = true;
			else success = false;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public boolean LoginUser(String userId, String password) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated method stub
		FileInfo fileInfo = null;
		
		boolean success = false;
		writer = new OutputStreamWriter(sock.getOutputStream(), "UTF-8");
		reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
		try {
			fileInfo = new FileInfo(userId, password, password, ServiceNum.LOGINUSER);
			String fileInfoStr = new Gson().toJson(fileInfo);
			writer.write(fileInfoStr + "\n");
			writer.flush();
			
			//from server
			String line = reader.readLine();
			System.out.println("line in loginUser : " + line);
			success = new Gson().fromJson(line, Boolean.class);
			System.out.println("success? "+success);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}
}


