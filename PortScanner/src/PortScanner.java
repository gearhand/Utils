import java.net.*;
import java.io.IOException;


public class PortScanner {

	public static void main(String[] args) {
		try {
			String address = args[0];
			int startPort = Integer.parseInt(args[1]);
			int endPort = Integer.parseInt(args[2]);
					
			for (int port = startPort; port <= endPort; ++port) {
				try {
					Socket knock = new Socket (InetAddress.getByName(address), port);
					System.out.printf("Port %d\topen\n", port);
					knock.close();
				}
				catch (IOException i) {
					System.out.printf("Port %d\tclosed\n", port);
				}				
			}
	
		}
		catch (ArrayIndexOutOfBoundsException input) {
			System.out.println("Scans ports from <start port> to <end port>");
			System.out.println("Usage: java [options] PortScanner <ip-address or hostname> <start port> <end port>");
			return;
		}
	}
}
