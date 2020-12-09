package quiz1;
import java.io.*;

class ThreadA extends Thread{
    public void run(){
       synchronized(Test.MUTEXa){
          try{ Thread.sleep(1000);} catch(InterruptedException e){ }
          synchronized(Test.MUTEXb){
             System.out.println("ThreadA got both MUTEXa and MUTEXb");
          } 
        } 
    }
 } 
 
 class ThreadB extends Thread{
    public void run(){
       synchronized(Test.MUTEXb){
           try{ Thread.sleep(1000);} catch(InterruptedException e){ }
           synchronized(Test.MUTEXa){
              System.out.println("ThreadB got both MUTEXa and MUTEXb");
           }
         }
       }
    }
 
 class Test{   //获取开始时间  
    public static Object MUTEXa = new Object();
    public static Object MUTEXb = new Object();
    public static void main(String[] args){
        long startTime=System.currentTimeMillis();
        Thread t1 = new ThreadA();
        Thread t2 = new ThreadB();
        t1.start(); t2.start();
        System.out.println("The World Is Wonderful");
        long endTime=System.currentTimeMillis(); //获取结束时间  
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms"); 
    }
 }