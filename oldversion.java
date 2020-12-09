public class oldversion {
    '''
        if (this.state.equals("INIT")||this.state.equals("RCVD_HELLO")){
        	dest = "-1";
        } else {
            dest = destinationId;
        }
        
        String msg = Integer.toString(localId)+";"+dest+";"+Integer.toString(packetId)+";"+payload;
 
		TimerTask task = new TimerTask() {  
            @Override
            public synchronized void run() {                 
                if (!state.equals("SENT")){
                    downside.send(msg,destination);
                    if (!state.equals("INIT"))state="SENT";
                }	
            }  
        };
       
        TIMER.schedule(task, 0, 200);
		
        while (waitForACK) {
	    	try{
	            this.wait();
	        }catch(InterruptedException e){
	            e.printStackTrace();
	        }
        }
      
		if (this.state.equals("CONNECTED")||this.state.equals("RCVD_ACK")){
            task.cancel();
        }
    	'''
}
