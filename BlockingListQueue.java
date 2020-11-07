import java.util.LinkedList;
public class BlockingListQueue implements URLQueue {
    
    private final LinkedList<String> queue;

    public BlockingListQueue() {
        this.queue = new LinkedList<String>();
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.queue.size() == 0;
    }

    @Override
    public synchronized boolean isFull() {
        return false;
    }

    @Override
    public synchronized void enqueue(String url) {
        this.queue.add(url);
        this.notify();
    }

    @Override
    public synchronized String dequeue() {
        while(isEmpty()){
            try{
                this.wait();
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                return "**STOP**";
            }
        }
        String result = this.queue.remove();
        if(result.equals("**STOP**")) Thread.currentThread().interrupt();
		return result;
    }

}

//Authur: Xinyi DAI Zhengqing LIU
