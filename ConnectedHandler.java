import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Runnable;

public class ConnectedHandler extends Handler {

    /**
     * @return an integer identifier, supposed to be unique.
     */
    public static int getUniqueID() {
        return (int) (Math.random() * Integer.MAX_VALUE);
    }

    // don't change the two following definitions

    private static final String HELLO = "--HELLO--";
    private static final String ACK = "--ACK--";

    /**
     * the two following parameters are suitable for manual experimentation and
     * automatic validation
     */

    /** delay before retransmitting a non acked message */
    private static final int DELAY = 300;

    /** number of times a non acked message is sent before timeout */
    private static final int MAX_REPEAT = 10;

    /** A single Timer for all usages. Don't cancel it. **/
    private static final Timer TIMER = new Timer("ConnectedHandler's Timer",
        true);

    private final int localId;
    private final String destination;
    private Handler aboveHandler;
    public String state;
    private int rcvpacketId;
    private int sendpacketId;
    public String destinationId;
    public boolean waitForACK;
    public String dest;
    // to be completed

    /**
     * Initializes a new connected handler with the specified parameters
     * 
     * @param _under
     *          the {@link Handler} on which the new handler will be stacked
     * @param _localId
     *          the connection Id used to identify this connected handler
     * @param _destination
     *          a {@code String} identifying the destination
     */
    
    public ConnectedHandler(final Handler _under, int _localId,
        String _destination) {
        super(_under, _localId, true);
        this.localId = _localId;
        this.destination = _destination;
        this.destinationId="-1";
        this.sendpacketId=0;
        this.rcvpacketId=1;
        this.state="INIT";
        this.waitForACK=true;
        send(HELLO);
    }

    // don't change this definition
    @Override
    public void bind(Handler above) {
        if (!this.upsideHandlers.isEmpty())
            throw new IllegalArgumentException(
            "cannot bind a second handler onto this "
              + this.getClass().getName());
        this.aboveHandler = above;
        super.bind(above);
    }

    @Override
    public synchronized void handle(Message message) {

        String[] rcvdmsg=message.payload.split(";",4);
        String remoteId=rcvdmsg[0];
        String senderId = rcvdmsg[1];
        int packetNumber = Integer.parseInt(rcvdmsg[2]);
        String payload = rcvdmsg[3];
        if (payload.equals(HELLO) && (!remoteId.equals("-1"))&&senderId.equals("-1")&&(packetNumber == 0)){
            this.destinationId=remoteId;
            if (this.state.equals("INIT")) this.state = "RCVD_HELLO";
            String msg=Integer.toString(localId)+";"+destinationId+";0;"+ACK;
            this.downside.send(msg, destination);
        }        
        if (this.state.equals("RCVD_HELLO")){ 
            if (payload.equals(ACK) && this.destinationId.equals(remoteId) 
            && senderId.equals(Integer.toString(this.localId))&&(packetNumber == 0)){
                waitForACK=false;
                this.notify();
                this.sendpacketId++;
                this.state="CONNECTED";
    		} 
        } else if (this.state.equals("CONNECTED")) {
            if (payload.equals(ACK) && this.destinationId.equals(remoteId) 
            && senderId.equals(Integer.toString(this.localId)) && packetNumber==this.sendpacketId){
                waitForACK=false;
                this.notify();
                this.sendpacketId++;
            } 
            if ((!payload.equals(ACK)) &&(!payload.equals(HELLO))  && destinationId.equals(remoteId)  && 
        			senderId.equals(Integer.toString(this.localId))) {
                if (packetNumber==this.rcvpacketId-1){
                    String msg=Integer.toString(localId)+";"+destinationId+";"+Integer.toString(packetNumber)+";"+ACK;
                    this.downside.send(msg, destination);
                }
                if (packetNumber==this.rcvpacketId){
                    this.rcvpacketId++;
                    Message newmessage=new Message(payload,message.sourceAddress);
                    if (this.aboveHandler!=null){
                        this.aboveHandler.receive(newmessage);
                        String msg=Integer.toString(localId)+";"+destinationId+";"+Integer.toString(packetNumber)+";"+ACK;
                        this.downside.send(msg, destination);
                    }
                }
        	}
        }
    }

    @Override
    public synchronized void send(String payload) {
        this.waitForACK=true;
        if (this.state.equals("INIT")){
        	dest = "-1";
        } else {
            dest = destinationId;
        }
        
        String msg = Integer.toString(localId)+";"+dest+";"+Integer.toString(this.sendpacketId)+";"+payload;
        //System.out.println(msg);
		TimerTask task = new TimerTask() {  
            @Override
            public synchronized void run() {                 
                downside.send(msg,destination);
            }  
        };
       
        TIMER.schedule(task, 0, DELAY);
        while (waitForACK) {
	    	try{
	            this.wait();
	        }catch(InterruptedException e){
	            e.printStackTrace();
	        }
        }
        task.cancel();
    		
    }

    @Override
    public void send(String payload, String destinationAddress) {
        no_send();
    }

    @Override
    public void close() {
        // to be completed
        super.close();
    }

}
