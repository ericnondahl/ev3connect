import java.io.IOException;
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
				System.out.println("Listening for EV3...");

				//Receive a packet
				byte[] recvBuf = new byte[67];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				socket.receive(packet);
				//Packet received
			
				//See if the packet holds the right command (message)
				String message = new String(packet.getData()).trim();
				
				if (message.contains("EV3")) {
					
					System.out.println("Have EV3 Broadcast from "+packet.getAddress()+":"+packet.getPort());
					
					byte[] sendData = "HELLO ALBERT".getBytes();

					//Send a response
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);

					System.out.println("Sent response to: " + sendPacket.getAddress().getHostAddress());
					
					startTCP(packet.getAddress(), 5555);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private static void startTCP(InetAddress address, int port) {
		
		try {
			Socket socket = new Socket(address, port);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
