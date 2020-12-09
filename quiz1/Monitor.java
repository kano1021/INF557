package quiz1;
class TheThread extends Thread{
    TheMonitor myMonitor;
    public TheThread (TheMonitor m){
       myMonitor = m;
    }
    public void run(){
       myMonitor.doSomething();
    } 
  } 
  
  class TheMonitor{
     private TheMonitor otherGuy = null;
     public synchronized void setOtherGuy (TheMonitor m){
         otherGuy=m;
     }
     public synchronized void doSomething(){
        try{Thread.sleep(1000);}catch (InterruptedException e){}
        otherGuy.doSomething();
     }
  }
  
  class MonitorTest{
     public static void main(String[] args){
        TheMonitor monitorA = new TheMonitor();
        TheMonitor monitorB = new TheMonitor();
        monitorA.setOtherGuy(monitorB);
        monitorB.setOtherGuy(monitorA);
        Thread t1 = new TheThread(monitorA);
        Thread t2 = new TheThread(monitorB);
        t1.start(); t2.start();
        try{
           t1.join();
           t2.join();
        }catch (InterruptedException e){}
        System.out.println("Here we go");
      }
  }