import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;


public class EV3Server {

	public static void main(String[] args) {
		try {

			//Keep a socket open to listen to all the UDP trafic that is destined for this port
			DatagramSocket socket = new DatagramSocket(3015, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);

			while (true) {
				System.out.println(">>Listening for EV3...");

				//Receive a packet
				byte[] recvBuf = new byte[67];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);
				//Packet received
			
				//See if the packet holds the right command (message)
				String message = new String(packet.getData()).trim();
				
				if (message.contains("EV3")) {
					
					System.out.println(">>Have EV3 Broadcast from "+packet.getAddress()+":"+packet.getPort());
					System.out.println(message);
					
					String[] words = message.split(" ");
					String serialNumber = words[1];
					int end = serialNumber.indexOf("\r");
					serialNumber = serialNumber.substring(0, end);
					System.out.println(">>Serial:"+serialNumber);
					
					byte[] sendData = "HELLO ALBERT".getBytes();

					//Send a response
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);

					System.out.println(">>Sent UDP response to: " + sendPacket.getAddress().getHostAddress());
					
					startTCP(packet.getAddress(), 5555, serialNumber);
					break;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static void startTCP(InetAddress address, int port, String serialNumber) {
		
		try {
			Socket socket = new Socket(address, port);
			
			OutputStream out = socket.getOutputStream();
			String message = "GET /target?sn="+serialNumber+" VMTP1.0\nProtocol: EV3";
			byte[] msg = message.getBytes();
			out.write(msg);
			
			System.out.println(">>Sent TCP connect to "+address.getHostAddress()+":"+port);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String data = readFully(in,12);
			System.out.println(data);
			
			if(data.contains("Accept")) {
				startConsole(out, in);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void startConsole(OutputStream out, BufferedReader in) {
		System.out.println(">>Command?");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {

			String cmd = br.readLine();
			byte[] msg = cmd.getBytes();
			
			out.write(msg);
			
			String data = readFully(in,1);
			System.out.println(">>have response");
			System.out.println(data);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public static String readFully(Reader reader, int length) throws IOException
	{
	    char[] buffer = new char[length];
	    int totalRead = 0;
	    while (totalRead < length)
	    {
	        int read = reader.read(buffer, totalRead, length-totalRead);
	        if (read == -1)
	        {
	            throw new IOException("Insufficient data");
	        }
	        totalRead += read;
	    }
	    return new String(buffer);
	}
}
