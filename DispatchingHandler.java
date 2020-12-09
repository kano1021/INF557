//Author: Xinyi DAI, Zhengqing LIU


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
//import com.sun.tools.javac.code.Attribute.Array;

public class DispatchingHandler extends Handler {

	//1. the detection of a new connection request, which gives rise to an insertion 
	//into the queue of pending connections,
	
	//2. the dispatching of every incoming packet 
	//to the ConnectedLayer corresponding to its connectionId, and to it only.
  
	/** one will need it */
	private static final String HELLO = "--HELLO--";
	private static final String ACK = "--ACK--";
	/** An arbitrary base value for the numbering of handlers. **/
	private static int counter = 35000;

	/** the queue for pending connections */
	private final ArrayBlockingQueue<ConnectionParameters> queue;
	private final ArrayBlockingQueue<ConnectionParameters> Helloqueue;
	// to be completed

	protected final ConcurrentHashMap<Integer,Integer> connected;
	
	private static final boolean DEBUG = false;

	
	private int queueCapacity;
    public static String prevRemoteId = "";
	
	//public boolean added = false;
	
	/**
   * Initializes a new dispatching handler with the specified parameters
   * 
   * @param _under
   *                         the {@link Handler} on which the new handler will
   *                         be stacked
   * @param _queueCapacity
   *                         the capacity of the queue of pending connections
   */
	public DispatchingHandler(final Handler _under, int _queueCapacity) {
		super(_under, ++counter, false);
		this.queueCapacity = _queueCapacity;
		this.queue = new ArrayBlockingQueue<ConnectionParameters>(_queueCapacity);
		this.Helloqueue = new ArrayBlockingQueue<ConnectionParameters>(1000);
		// add other initializations if needed
		this.connected=new ConcurrentHashMap<Integer,Integer>();
	}

  /**
   * Retrieves and removes the head of the queue of pending connections, waiting
   * if no elements are present on this queue.
   *
   * @return the connection parameters record at the head of the queue
   * @throws InterruptedException
   *                                if the calling thread is interrupted while
   *                                waiting
   */
	public ConnectionParameters accept() throws InterruptedException {
		return this.queue.take();
	}

	@Override
  	public void send(String payload) {
		no_send();
	}

	@Override
  	protected void send(String payload, String destinationAddress) {
	  	this.downside.send(payload, destinationAddress);
  	}

  	@SuppressWarnings("unlikely-arg-type")
	@Override
  	public synchronized void handle(Message message) {
	    //super.handle(message);
		String[] rcvdmsg=message.payload.split(";",4);
  		int remoteId = Integer.parseInt(rcvdmsg[0]);
  		int senderId = Integer.parseInt(rcvdmsg[1]);
  		int packetNumber = Integer.parseInt(rcvdmsg[2]);
  		String payload = rcvdmsg[3];
  		
  		boolean Adding = true;
  		
  		//detect new connection request

  		
  		if (payload.equals(HELLO)) {
  			
  			ConnectionParameters addingParameter = new ConnectionParameters(remoteId, message.sourceAddress);
  			
  			for(ConnectionParameters con: this.Helloqueue) {
  				if(con.getRemoteId() == remoteId){
  					
  						Adding = false;
  					}
  				}  				
  			
  			
  			  			  				
			if(Adding && (this.queue.size() < this.queueCapacity) && (remoteId!=-1) 
					&& senderId==-1 && (packetNumber == 0)){
				this.queue.add(addingParameter);
		 	    this.Helloqueue.add(addingParameter);
			}
			if (!Adding){
				//if (!this.upsideHandlers.containsKey(senderId) && (this.queue.size() < this.queueCapacity) && (remoteId!=-1) 
				//&& senderId==-1 && (packetNumber == 0)){
				//	this.queue.add(addingParameter);
				//}
				if (connected.containsKey(remoteId)){
					if (this.upsideHandlers.containsKey(connected.get(remoteId)))
						this.upsideHandlers.get(connected.get(remoteId)).receive(message);
					else
						this.queue.add(addingParameter);
				}
			} 
  			
  		} else {	
	  		try {
	  			
	  			if(this.upsideHandlers.containsKey(senderId)) {
					this.upsideHandlers.get(senderId).receive(message); 
					if (!this.connected.containsKey(remoteId)){
						this.connected.put(remoteId,senderId);
					}  
	  			}
	  			
	  		} catch (IllegalStateException e) {
	  			e.printStackTrace();
	  		}
  		}
  		
  
  	}
}