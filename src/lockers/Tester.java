/* Marc Eder
 * EC504 Project
 * 3/4/14
 * Tester: Tests deduplicator
 */

package lockers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;

public class Tester {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		String fileName = "sample.txt";
		Locker storage = new FasterStupidLocker();
		long start = System.currentTimeMillis();
		storage.insert(fileName);
		storage.retrieve(fileName);
		long end = System.currentTimeMillis();
		System.out.println("Total time: " + (double)(end - start)/1000 + "s\n");
		
		//Compares input and output files
		RandomAccessFile f = new RandomAccessFile ("sample.txt", "r");
		FileChannel fc = f.getChannel();
		MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		bb.load();
		byte[] originalFile = new byte[(int) fc.size()];
		bb.get(originalFile);
		f.close();
		fc.close();
		System.out.println("Input file Hash: " + storage.hash(originalFile));
		
		f = new RandomAccessFile ("Copysample.txt", "r");
		fc = f.getChannel();
		bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		bb.load();
		byte[] copyFile = new byte[(int) fc.size()];
		bb.get(copyFile);
		f.close();
		fc.close();
		System.out.println("Output file Hash: " + storage.hash(copyFile));
		System.out.println("The output file matches the input file: " + (storage.hash(originalFile).equals(storage.hash(copyFile))));
	}
}