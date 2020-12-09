//Authur: Xinyi DAI Zhengqing LIU
import java.net.URL;
import java.util.HashSet;
import java.lang.Runnable;


public class Wget {

    public static void iterativeDownload(String initialURL) {
        final URLQueue queue = new ListQueue();
        final HashSet<String> seen = new HashSet<String>();
    // defines a new URLhandler 

        DocumentProcessing.handler = new DocumentProcessing.URLhandler() {
      // this method will be called for each matched url
            @Override
            public void takeUrl(String url) {
            // to be completed at exercise 2
                if (!seen.contains(url)){
                    seen.add(url);
                    queue.enqueue(url);
                }
            }
        };
    // to start, we push the initial url into the queue
        DocumentProcessing.handler.takeUrl(initialURL);
        while (!queue.isEmpty()) {
            String url = queue.dequeue();
            Xurl.download(url); // don't change this line
        }
    }


    public static void multiThreadedDownload(String initialURL) {
        final URLQueue queue = new SynchronizedListQueue();
        final HashSet<String> seen = new HashSet<String>();
        int cnt = Thread.activeCount();
        DocumentProcessing.handler = new DocumentProcessing.URLhandler() {
            @Override
            public void takeUrl(String url) {
                synchronized(seen){
                    if (seen.add(url)){
                        queue.enqueue(url);
                    }
                }
            }
        };
        
        DocumentProcessing.handler.takeUrl(initialURL);
        do {
            while (!queue.isEmpty()) {
                String url = queue.dequeue();
                Thread t=new Thread(()->Xurl.download( url));
                t.start();
            }
            try{
                Thread.sleep(100);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }while (Thread.activeCount()>cnt||!queue.isEmpty());
    // to be completed later
    }

    @SuppressWarnings("unused")
    public static void threadPoolDownload(int poolSize, String initialURL) {
        final URLQueue queue = new BlockingListQueue();
        final HashSet<String> seen = new HashSet<String>();

        class COUNTER{
            private int counter;
            public COUNTER(int cnt){
                this.counter=cnt;
            }

            public synchronized void countup(){
                this.counter++;
            }
            public synchronized void countdown(){
                this.counter--;
                if (this.counter==0)this.notify();
            }
            public synchronized int number(){
                return this.counter;
            }
            public synchronized void waitBackToZero() {
                while (this.counter > 0) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        final COUNTER cnt=new COUNTER(0);
        DocumentProcessing.handler = new DocumentProcessing.URLhandler() {
            @Override
            public void takeUrl(String url) {
                synchronized(seen){
                    if (seen.add(url)){
                        cnt.countup();
                        queue.enqueue(url);
                    }
                }
            }
        };
        DocumentProcessing.handler.takeUrl(initialURL);
        Runnable worker=new Runnable(){
            public void run(){
                while (!Thread.interrupted()){
                    String url=queue.dequeue();
                    if (Thread.interrupted())return;
                    Xurl.download(url);
                    cnt.countdown();
                }
            }
        };
        
        Thread[] pool= new Thread[poolSize];
        
        for (int i=0;i<poolSize;++i){
            pool[i]=new Thread (worker,"worker_"+i);
            pool[i].start();
        }
        cnt.waitBackToZero();
        for (int i=0;i<poolSize;++i){
            pool[i].interrupt();
        }
        try{
            for (int i=0;i<poolSize;++i){
                pool[i].join();
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }          
        

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Wget url");
            System.exit(-1);
        }
        threadPoolDownload(Integer.parseInt(args[0]),args[1]);
    }

}