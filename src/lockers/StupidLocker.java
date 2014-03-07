package lockers;
/* Marc Eder
 * EC504 Project
 * 3/4/14
 * StupidLocker: Deprecated implementation of storage locker. Extends old Locker. Very slow, no longer
 * matches correct data types for some methods.
 */
//
//package lockers;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//
//public class StupidLocker extends Locker {
//
//	private static final long serialVersionUID = 1L;
//
//	StupidLocker() {
//		super();
//		chunkSize = 100;
//	}
//
//	void insert(String fileName) {
//		// Probably should handle duplicate fileName conflicts
//		String data = "";
//		try {
//			File f = new File (fileName);
//			long start = System.currentTimeMillis();
//			BufferedReader in = new BufferedReader(new FileReader(f));
//			long end = System.currentTimeMillis();
//			System.out.println((end-start));
//			String str;
//			while ((str = in.readLine()) != null){
//				data += str;
//			}
//			in.close();
//			
//			// Would normally proceed to chunk and check file
//			// But this locker just takes a whole file as a chunk
//			String chunk = data;
//			String h = this.hash(chunk);
//			ArrayList<String> fr = new ArrayList<String>();
//			if (!this.hashExists(h)){
//				this.addToDictionary(h, chunk);
//				fr.add(h);
//			} else {
//				System.out.println("File already exists.");
//				fr.add(h);
//			}
//			
//			// Adds file to our list
//			this.files.add(new FileInfo(f.getName(), this.hash(data), fr));
//			int newIndex = this.files.size() - 1;
//			this.files.get(newIndex).setStorageIndex(newIndex);
//			System.out.println("File added successfully.");
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//	}
//
//	void addToDictionary(String hash, String chunk) {
//		dictionary.put(hash, chunk);
//	}
//
//	boolean hashExists (String hash) {
//		return dictionary.containsKey(hash);
//	}
//
//}