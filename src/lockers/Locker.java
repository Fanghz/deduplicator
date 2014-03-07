/* Marc Eder
 * EC504 Project
 * 3/4/14
 * Locker: Abstract class for storage locker. Implements hash and retrieve methods.
 */

package lockers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Locker implements Serializable {
	private static final long serialVersionUID = 1L;
	protected int chunkSize;
	protected ConcurrentHashMap <String, byte[]> dictionary;
	protected ArrayList<FileInfo> files;
	
	final public class FileInfo implements Serializable {
		private static final long serialVersionUID = 2L;
		private String fileName;
		private int storageIndex;
		private String hash;
		private ArrayList<String> fileReconstructor;
		private int size;
		
		public FileInfo (String fileName, String hash, ArrayList<String> fileReconstructor, int size){
			this.fileName = fileName;
			this.fileReconstructor = fileReconstructor;
			this.hash = hash;
			this.size = size;
		}
		
		public void setStorageIndex(int index){
			this.storageIndex = index;
		}
		
		public int getStorageIndex(){
			return this.storageIndex;
		}
		
		public int getSize(){
			return this.size;
		}
		
		public String getFileName (){
			return this.fileName;
		}
		
		public ArrayList<String> getFileReconstructor (){
			return this.fileReconstructor;
		}
		
		public String getHash(){
			return this.hash;
		}
		
		public String toString(){
			return "File Name: " + this.fileName + ", Storage Index: " + this.storageIndex + ", Size: " + this.size/1000 + "kB";
		}
	}
	
	Locker() {
		this.files = new ArrayList<FileInfo>();
		this.dictionary = new ConcurrentHashMap <String, byte[]>(); 
	}
	
	// Hashes the chunks of file
	public String hash (byte[] chunk) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(chunk);
		byte[] h = md.digest(chunk);
		
		// Makes it ASCII string
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < h.length; i++) {
        	sb.append(Integer.toString((h[i] & 0xff) + 0x100, 16).substring(1));
        }
        String hashOutput = sb.toString();
		return hashOutput;
	}
		
	// Retrieves requested file
	public void retrieve (String fileName) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException{
		// Grabs correct fileReconstructor for this file
		int indexOfFile = -1;
		for (FileInfo file : this.files){
			if (fileName.equals(file.getFileName())){
				indexOfFile = file.getStorageIndex();
			}
		}
		if (indexOfFile == -1){
			System.err.println("File not found.");
			return;
		}
		FileInfo currentFile = this.files.get(indexOfFile);
		ArrayList<String> fr = currentFile.getFileReconstructor();
		
		// Serializes contents of file to a copy of the original file
		String fileNameOut = "Copy" + fileName;
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(fileNameOut), false));
		for (int j = 0; j < fr.size(); j++){
			bos.write(this.dictionary.get(fr.get(j)));
		}
		bos.close();
		System.out.println("Retrieved File Information:\n" + currentFile.toString());
	}
	
	// ABSTRACT METHODS
	// Inserts a file
	abstract void insert (String fileName);
	
	abstract boolean hashExists (String hash);
}
