//author xinyi dai, zhengqing liu
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GroundLayer {

  /**
   * This {@code Charset} is used to convert between our Java native String
   * encoding and a chosen encoding for the effective payloads that fly over the
   * network.
   */
    private static final Charset CONVERTER = StandardCharsets.UTF_8;

  /**
   * This value is used as the probability that {@code send} really sends a
   * datagram. This allows to simulate the loss of packets in the network.
   */
    public static double RELIABILITY = 1.0;

    private static DatagramSocket localSocket = null;
    private static Thread receiver = null;
    private static Handler handler = null;
    public static boolean closed=false;

    public static void start(int _localPort, Handler _handler)
        throws SocketException {
        if (handler != null)
            throw new IllegalStateException("GroundLayer is already started");
        handler = _handler;
    // TO BE COMPLETED
        localSocket = new DatagramSocket(_localPort);
	    
        Thread receiver = new Thread(new Runnable() {
            //@SuppressWarnings("synthetic-access")
            //@Override

            public void run() {
                closed = false;
                while (!closed) {
                    Message message = null;
    
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
  
                    try {	
                        localSocket.receive(packet);
                        String payload = new String(buffer, 0, packet.getLength(), CONVERTER);
                        String socketAddress = packet.getSocketAddress().toString();
                        message = new Message(payload,socketAddress);
                  
                        if (!closed && message != null) handler.receive(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                localSocket.close();

            }
        
        },handler.getClass().getName() + "'s processor");
  
        receiver.setDaemon(true);
        receiver.start();
        if (closed) receiver.interrupt();
    }

    public static void send(String payload, SocketAddress destinationAddress) {
        if ((!closed) && Math.random() <= RELIABILITY) {
      // MUST SEND
            byte[] buffer = payload.getBytes(CONVERTER);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, destinationAddress);
            try {
                localSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close() {
        closed=true;
        handler=null;
	    if (receiver != null) {
            receiver.interrupt();
        }
        if (localSocket!=null){
		    localSocket.close();  
        }
        System.out.println("GroundLayer closed");
    }

}