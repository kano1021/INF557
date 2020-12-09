//interface URLQueue as the abstract definition of a queue,
//class ListQueue is given as a first simple implementation of a queue, to be used as it is for this exercise,
//skeleton file Wget.java where you have to write some lines for this exercise.

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.lang.Runnable;

public class Wget {
//you have to complete method iterativeDownload of Wget, which will call 
//Xurl.download, which calls in turn DocumentProcessing.parseBuffer and which,
//at last for each url found, calls the new handler, which is now defined in iterativeDownload.
    
	public static void iterativeDownload(String initialURL) {
	final URLQueue queue = new SynchronizedListQueue();
    final HashSet<String> seen = new HashSet<String>();
    
    // defines a new URLhandler
    DocumentProcessing.handler = new DocumentProcessing.URLhandler() {
      // this method will be called for each matched url
      //@Override
      public void takeUrl(String url) {
    	  /*
          todo.enqueue(starting_node)
          seen.add(starting_node)
          while todo.notEmpty()
             x ¡û todo.dequeue()
             for each neighbor y of x
                if y not in seen
                   todo.enqueue(y)
                   seen.add(y)
                   predecessor[y] ¡û x
    	   */
        // to be completed at exercise 2
        synchronized(seen) {
    	  if (seen.add(url)) {
    	   queue.enqueue(url);
          }  
        }
      }
    };
    
    
    
    // to start, we push the initial url into the queue
    DocumentProcessing.handler.takeUrl(initialURL);
	while(!queue.isEmpty()) {
	    String url= queue.dequeue();
	    Thread  t= new Thread(url);
	    t.start();
	    }
	
  }

  
  public static void multiThreadedDownload(String initialURL)  {
	  
	  final URLQueue queue = new SynchronizedListQueue();
	  final HashSet<String> seen = new HashSet<String>();
	  int initialCount = Thread.activeCount();
	    DocumentProcessing.handler = new DocumentProcessing.URLhandler() {
	        // this method will be called for each matched url
	        //@Override
	        public void takeUrl(String url) {

	          synchronized(seen) {
	      	  if (seen.add(url)) {
	      	   queue.enqueue(url);
	            }  
	          }
	        }
	      };
	      

	    DocumentProcessing.handler.takeUrl(initialURL);
	    class URLprocessor implements Runnable{
			private String url;
			public URLprocessor(String _url) {
				this.url=_url;
				}
			public void run() {
				Xurl.download(this.url);
			}
		}

	  do {
		  while (!queue.isEmpty()) { //optional
		    String url= queue.dequeue();
		    Thread  t= new Thread(new URLprocessor(url));
		    t.start();
		  }
			  
			  try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
		  
	  } while (Thread.activeCount()>initialCount
		  ||!queue.isEmpty());
	  		
  }

  
  public static void threadPoolDownload(int poolSize, String initialURL) {
    // to be completed later
	  /*
	  Runnable worker = new Runnable() {
		  public void run() {
			  while(!Thread.interrupted()) {
				  String url = queue.dequeue();
				  if(Thread.interrupted())return;
				  Xurl.download(url);
			  }
		  }
	  };
	  
	  Thread[] pool = new Thread[poolSize];
	  for(int i=0;i<pool.length;++i) {
		  pool[i]=new Thread(worker+"worker_"+i);
		  pool[i].start();
	  }
	  */
  }
      

  
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: java Wget url");
      System.exit(-1);
    }
    //iterativeDownload(args[0]);
    multiThreadedDownload(args[0]);
  }

}