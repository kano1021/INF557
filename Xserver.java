import java.io.*;  
import java.net.*;  
import java.util.StringTokenizer;
//import java.lang.Runnable;
import java.util.regex.*;

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

public class Xserver extends Thread{
    public static int port;//the port we want to bind
    static ServerSocket serverSocket=null;
    public static String ROOT;
    public static int nThread;
    private static InputStream input;
    private static OutputStream out;
    private final static COUNTER cnt=new COUNTER(0);
    static synchronized void handleConnection(Socket socket) {
        //System.out.println("HandleConnection...");
		try{
            cnt.countup();
            input = socket.getInputStream();
            out = socket.getOutputStream();
            BufferedReader reader= new BufferedReader(new InputStreamReader(input));
            while (!socket.isClosed())try{
                String link_regex1 = "GET\\s+(?<pathtofile>[^\\s]*)\\s+HTTP/1\\.1";
                Pattern p1 = Pattern.compile(link_regex1);
                String link_regex2 = "Host:\\s+(?<host>[^\\s]*)";
                Pattern p2 = Pattern.compile(link_regex2);
                String line=null;
                String Path=null;
                String hostport=null;
                //boolean findpath=false;
                CharSequence data=reader.readLine();
                Matcher m = p1.matcher(data);
                //System.out.println(data);
                while (m.find()) {
                    Path=m.group("pathtofile");
                    //System.out.println(Path);
                }
                data=reader.readLine();
                Matcher m2 = p2.matcher(data);
                while (m2.find()) {
                    hostport=m2.group("host");
                }do {
                    line=reader.readLine();
                }while (line!=null &&line.length()>0);

                if (Path==null||hostport==null|| Path.equals("")) {
            	    try {
                        StringBuffer error = new StringBuffer();
                        String context="<h1>Bad Request</h1>";
    				    error.append("HTTP/1.1 400 Bad request\r\n");
			            error.append("Content-Type: text/html\r\n");
			            error.append("Content-Length: "+context.length()+"\r\n").append("\r\n");
			            error.append(context);
    				    out.write(error.toString().getBytes());
                        out.flush();
    				    out.close();
    			    } catch (IOException e) {
    				    e.printStackTrace();
    			    }
                }else {
                    //System.err.println("find path");
            	    if (Path.equals("/")) {
                        //System.err.println("find path /");
            		    try {
                            System.err.println("find path /");
                            StringBuffer welcome = new StringBuffer();
                            String context="<h1>Welcome..</h1>";
                            welcome.append("HTTP/1.1 200 ok\r\n");
                            welcome.append("Content-Type: text/html\r\n");
            				welcome.append("Content-Length: "+context.length()+"\r\n").append("\r\n");
			                welcome.append(context);
        				    out.write(welcome.toString().getBytes());
                            out.flush();
                            
        				    //out.close();
            		    }catch(Exception e) {
            			    e.printStackTrace();
            		    }
            	    }else {
            		    File file = new File(ROOT + Path);
                	    if (file.exists()) {
                		    try {
                                BufferedReader txt = new BufferedReader(new FileReader(file));
                                int l=(int)file.length();
                                char[] context=new char[l];
            				    txt.read(context);
                                txt.close();
            				    StringBuffer result = new StringBuffer();
                                result.append("HTTP/1.1 200 ok\r\n");
                                result.append("Content-Type: text/html\r\n");
            				    result.append("Content-Length: " + file.length() + "\r\n");
            				    result.append("\r\n" + context);
            				    out.write(result.toString().getBytes());
            				    out.flush();
            			    } catch (Exception e) {
            				    e.printStackTrace();
            			    }
                	    }else {
                		    try {
                                StringBuffer error = new StringBuffer();
                                String context="<h1>File Not Found..</h1>";
                                error.append("HTTP/1.1 404 Not Found\r\n");
			                    error.append("Content-Type:text/html\r\n");
			                    error.append("Content-Length: "+context.length()+"\r\n").append("\r\n");
			                    error.append(context);
            				    out.write(error.toString().getBytes());
            				    out.flush();
            				    //out.close();
            			    } catch (IOException e) {
            				    e.printStackTrace();
            			    }
                	    }
            		
            	    }
                }
                //while ((line=reader.readLine())!=null){}
            }catch(Exception e) {}
            cnt.countdown();
        }catch (IOException e){
            e.printStackTrace();
        }
		//cnt.countdown();
    }

    
    public static void main(String[] args){
        if (args.length < 2) {
            System.err.println("Please enter serverport and rootdir");
            System.exit(-1);
        }else{
            ROOT=args[1];
            try{
                port = Integer.parseInt(args[0]);
            }catch(Exception ex){
                System.err.println("Invalid port number");
            }
        }
        if (args.length==2){
            nThread=1;
        }else{
            nThread=Integer.parseInt(args[2]);
        }
        Thread[] pool= new Thread[nThread];
        try{
            ServerSocket serverSocket = new ServerSocket(port,10);
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            for (int i=0; i<=nThread; ++i){
                Socket socket=serverSocket.accept();
                pool[i] =new Thread(()->handleConnection(socket));
                pool[i].start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        cnt.waitBackToZero();
        for (int i=0;i<nThread;++i){
            pool[i].interrupt();
        }
        try{
            for (int i=0;i<nThread;++i){
                pool[i].join();
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
 // author Xinyi DAI; Zhengqing LIU;
 //resources:   https://blog.csdn.net/HaHa_Sir/article/details/80594982
