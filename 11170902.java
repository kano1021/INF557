  private static String[] stategroup = {"INIT","HELLO_RECEIVED","CONNECTED"};
  private static String message_State;

    public void handle(Message message) {
		int packetNumber;
		String innerpayload;
		int remoteId;
		Pattern r =Pattern.compile("(\\d+);(\\d+);(\\d+);(\\D+)");
	    Matcher m =r.matcher(message.payload);
	    if(m.find()) {
	    	localId=Integer.parseInt(m.group(1));
	    	remoteId=Integer.parseInt(m.group(2));
	    	packetNumber=Integer.parseInt(m.group(3));
	    	innerpayload=m.group(4);
	    }
	    if(innerpayload==HELLO) {
	    	message_State=stategroup[1];
	    }
	    if(message_State==stategroup[2]) {
	        for (Handler above : Handler.upsideHandlers.values())
	            above.receive(message);
	    }
    }

	@Override
	public void send(final String payload) {
	    // to be completed
		boolean isWaiting;
		TimerTask task = new TimerTask() {  
	    	
			@Override
		    public void run() {  
	    	   synchronized(this){
	    		   while(isWaiting) {
	    			   task.wait();
	    		   }
	    		   while(!isWaiting) {
	    			   task.notify();
	    		   }
	    	   }	
		    }  
		}; 
		TIMER.schedule(task, 1000, 2000);
		
	}