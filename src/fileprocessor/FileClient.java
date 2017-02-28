package fileprocessor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import db.domain.DirFile;

public interface FileClient {
	//
	/*
	 *  userID => Default Variable
	 *  localPath => File, Directory ���ε�, �ٿ�, ������ Local ���?
	 *  currentPath => ���� �����ִ� ���?
	 *  File Method
	 */

	public List<DirFile> FileUpload(String userId, String localPath , String serverPath) throws IOException, ClassNotFoundException;
	public boolean FileDownload(String userId, String localPath) throws IOException;
	public List<DirFile> FileRemove(String userId, String currentPath) throws IOException, ClassNotFoundException;
	public List<DirFile> ChangeName(String userId, String currentPath, String newPath) throws IOException, ClassNotFoundException;
	public List<DirFile> FileSearch(String userId, String searchName) throws IOException, ClassNotFoundException;
	public List<DirFile> CheckCopyCut(int flag , String userId, String currentPath, String newPath) throws UnsupportedEncodingException, IOException;

	/*
	 *  Directory Method
	 */
	public List<DirFile> DirectoryCreate(String userId, String currentPath) throws IOException, ClassNotFoundException;
	public List<DirFile> DirectoryRemove(String userId, String currentPath) throws IOException, ClassNotFoundException;
	public List<DirFile> ShowList(String userId, String currentPath) throws IOException, ClassNotFoundException;
	
	
	
	/**
	 * User Method
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public boolean IsExistId(String userId) throws UnsupportedEncodingException, IOException;
	public boolean RegistUser(String userId, String password, String Name) throws UnsupportedEncodingException, IOException;
	public boolean LoginUser(String userId, String password) throws UnsupportedEncodingException, IOException;
	
	
}
