/* Marc Eder
 * EC504 Project
 * 3/4/14
 * FasterStupidLocker: Extends locker. Implements insert capabilities.
 */


package lockers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FasterStupidLocker extends Locker {

	private static final long serialVersionUID = 1L;

	FasterStupidLocker() {
		super();
		chunkSize = 1024;
	}

	void insert(String fileName) {
		// Probably should handle duplicate fileName conflicts
		try {
			long start = System.currentTimeMillis();
			
			// Loads file to a MappedByteBuffer to read contents
			RandomAccessFile f = new RandomAccessFile (fileName, "r");
			FileChannel fc = f.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			bb.load();
			
			// Reads chunks of data from file
			byte[] chunk = new byte[chunkSize];
			int i = 0;
			int limit = bb.limit();
			ArrayList<String> fr = new ArrayList<String>();
			while (i < limit - chunkSize){
				bb.get(chunk, 0, chunkSize);
				String h = this.hash(chunk);
				if (!this.hashExists(h)){
					this.addToDictionary(h, chunk);
					fr.add(h);
				} else {
					System.out.println("Hash already exists.");
					fr.add(h);
				}
				chunk = new byte[chunkSize];
				i += chunkSize;
			}
			// Handles last chunk (possibly less than normal chunkSize)
			if (limit - i > 0){
				byte[] lastChunk = new byte[limit-i];
				bb.get(lastChunk, 0, limit-i);
				String h = this.hash(lastChunk);
				if (!this.hashExists(h)){
					this.addToDictionary(h, lastChunk);
					fr.add(h);
				} else {
					System.out.println("Hash already exists.");
					fr.add(h);
				}
			}
			fc.close();
			f.close();
			
			// Adds file to our list
			this.files.add(new FileInfo(fileName, null, fr, limit));
			int newIndex = this.files.size() - 1;
			this.files.get(newIndex).setStorageIndex(newIndex);
		
			long end = System.currentTimeMillis();
			System.out.println(("Adding time: " + (double)(end-start)/1000) + "s");
			System.out.println("File added successfully.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	void addToDictionary(String hash, byte[] chunk) {
		dictionary.put(hash, chunk);
	}

	boolean hashExists (String hash) {
		return dictionary.containsKey(hash);
	}

}
