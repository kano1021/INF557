//Authur: Xinyi DAI Zhengqing LIU
import java.util.LinkedList;

/**
 * Basic implementation with a LinkedList.
 */
public class SynchronizedListQueue implements URLQueue {

    private final LinkedList<String> queue;

    public SynchronizedListQueue() {
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
    }

    @Override
    public synchronized String dequeue() {
        return this.queue.remove();
    }

}
