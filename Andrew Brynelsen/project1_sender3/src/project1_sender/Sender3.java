package project1_sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Sender3 {
	private static final int RANDOM_PORT = 555;
	
	public static void main(String[] args) throws IOException {
		//System.out.println("Sender Started");				
	    InetAddress addr = InetAddress.getByName( "127.0.0.1" );
	    
	    //change to cmd switch
	    byte[] send = Files.readAllBytes(Paths.get("src/Capture.jpg"));	     
	    int eof = send.length;
	    int dataStartIndex=0;
	    int sequenceNumber = 0;
	    
	    while (dataStartIndex < eof) {
	    	int chunkSize = 256;
	    	int headerSize = 4;
	    	int footerSize = 4;
	    	int dataSize = chunkSize - headerSize - footerSize;
	    	int totalNumPackets = send.length / dataSize;
	    	//int totalNumPackets = (int) Math.ceil(send.length / (double)dataSize);
	    	
	    	//deal with last chunk
	    	
	    	if (eof - dataStartIndex < dataSize) {
	    		dataSize = eof - dataStartIndex;
	    	}
	    	
	    	//Create header at start of chunk and remaining space fill with data
	    	ByteBuffer header = ByteBuffer.allocate(headerSize);
	    	header.putInt(sequenceNumber);	    	
	    	ByteBuffer footer = ByteBuffer.allocate(footerSize);
	    	footer.putInt(totalNumPackets);	    	
	    	
	    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    	outputStream.write(header.array());
	    	outputStream.write(Arrays.copyOfRange(send,dataStartIndex,dataStartIndex + dataSize));
	    	outputStream.write(footer.array());
	    	byte chunk[] = outputStream.toByteArray( );
	    	
	    	//print data about each packet
	    	//System.out.println("Packet Number = " + sequenceNumber);
	    	//System.out.println("Input File Size = " + send.length);
	    	//System.out.println("Total Packets = " + totalNumPackets);
	    	//System.out.println("Data Start = " + dataStartIndex);
	    	//System.out.println("Chunk End = " + (chunkStartIndex + chunkSize));
	    	//System.out.println("Chunk Length = " + chunk.length + "\n");
	    	
	    	//Send Packet
	    	DatagramSocket dsock = new DatagramSocket();	
		    DatagramPacket data = new DatagramPacket( chunk, chunk.length, addr, RANDOM_PORT );
		    dataStartIndex += dataSize ;
		    sequenceNumber++;
		    
		    //dsock.send( data );
		    try {
				Thread.sleep((long)(Math.random() * 130));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    dsock.send( data );
		    
		    dsock.close();
	    }
	    //System.out.println("Done Sending");
	}
}
