package project1_receiver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Receiver3 {
	private static final int RANDOM_PORT = 555;
	private static final File file = new File("src/outputFile.jpg");
	public static void main(String[] args) {
	
		boolean keepListening = true;
		int totalNumPackets = 0;
		int packetNumber;
		int dataSize = 248;
		int maxPackets = 1000;
		int packetsReceived = 0;
		byte[][] packetData = new byte[maxPackets][dataSize];		
		byte[] output;		
				
		try {
			System.out.println("Receiver Started");	
			while(keepListening) {
				//network things
				DatagramSocket dsock = new DatagramSocket( RANDOM_PORT );
				DatagramPacket data = new DatagramPacket( new byte[ 256 ], 256 );
				dsock.receive( data );	          		          
				 
				//extract header, footer and data from packet
				byte[] headerBytes = Arrays.copyOfRange(data.getData(),0,4);
				byte[] dataBytes = Arrays.copyOfRange(data.getData(),4,data.getLength() - 4);
				byte[] footerBytes = Arrays.copyOfRange(data.getData(),data.getLength() - 4,data.getLength());
				  
				//store header, footer and data in receiver
				packetNumber = ByteBuffer.wrap(headerBytes).getInt();
				totalNumPackets = ByteBuffer.wrap(footerBytes).getInt();		          
				packetData[packetNumber] = dataBytes;
				  
				//update dataSize, packetsReceived
				dataSize = dataBytes.length;		          
				packetsReceived++;
				System.out.println("Received: " + packetsReceived + " of " + totalNumPackets + " packets.");
				  
				//end loop if finished and disconnect
				if (packetsReceived == totalNumPackets) {
					keepListening = false;
					System.out.println("Transfer Complete");
				}
				//Thread.sleep((long)(Math.random() * 10));
				//dsock.disconnect();
				dsock.close();
			}	
			//Combine individual data packets into single byte[]
			output = new byte[totalNumPackets * dataSize];			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			for (int i =0 ; i <= totalNumPackets; i++) {
				baos.write(packetData[i]);
			}	
			output = baos.toByteArray();			        
			
			//write to file
			FileOutputStream fileOutputStream = new FileOutputStream(file);  
			fileOutputStream.write(output);
			fileOutputStream.close();			
		
		} catch( SocketException ex ) {
	          //Logger.getLogger(Receiver.class.getName() ).
	          //        log( Level.SEVERE, null, ex );
	    	  ex.printStackTrace();
	    	   
	    } catch( IOException ex ) {
	          //Logger.getLogger(Receiver.class.getName() ).
	          //        log( Level.SEVERE, null, ex );
	    	   ex.printStackTrace();
	    }		
	}
}
