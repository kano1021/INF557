import java.io.*;
import java.util.*;
import java.lang.*;

class MyURL{
    String url; //full url
	private String protocol; // protocol type: http, ftp, etc...
	private String host; // host name
	private int port; 	//port number
    private String path; //path
    private boolean islegal;
    public MyURL(){  
    }
    public MyURL(String url){
        this.url=url;
        this.islegal=true;
        this.setmyurl();
    }

    public boolean islegal(){
        return this.islegal;
    }
    public static void main(String[] args){
        String test1="http://www.example.com/foobar.html";
        String test2="http://www.example.com:8080/foo/bar/42.pdf";
        MyURL myURL=new MyURL(test2);
        System.out.println(myURL.getProtocol());
        System.out.println(myURL.getHost());
        System.out.println(myURL.getPort());
        System.out.println(myURL.getPath());
    }

    public int strStr(String sentence, String key) {
        if (sentence==null)    
            return -1;
        for(int i=0; i<sentence.length(); i++){
            if (i + key.length() > sentence.length())
                return -1;
            boolean find=true;
            for(int j=0; j<key.length(); j++){
                if(key.charAt(j)!=sentence.charAt(i+j)){
                    find=false;
                    break;
                }
            }    
            if (find) {
                return i;
            }
        }   
        return -1;
    }

    public String getProtocol(){
        return this.protocol;
    }
    public String getHost(){
        return this.host;
    }
    public int getPort(){
        return this.port;
    }
    public String getPath(){
        return this.path;
    }

    public void setmyurl(){
        int idx=this.strStr(this.url,"://");
        //System.out.println(idx);
        if (idx==-1) {//no protocol type: using http by default.
            this.protocol="http";
            idx=0;
        }else{
            String s=this.url.substring(0,idx);
            if (s==null || this.strStr(s, ":")>-1|| this.strStr(s, "/")>-1) {
                System.err.println("Invalid protocol");
                this.islegal=false;
            }else{
                //System.out.println("hello");
                this.protocol=s;
                idx=idx+3;
            }
        }
        //System.out.println(this.protocol);
        //System.out.println(idx);
        String urlnow=this.url.substring(idx);
        //System.out.println(urlnow);
        String[] part=urlnow.split(":");
        //System.out.println(part.length);
        if (part.length<0){
            System.err.println("Invalid Hostname");
            this.islegal=false;
        }else if (part.length==1){
            this.port=-1;
            int pathbegin=this.strStr(urlnow, "/");
            if (pathbegin<-1) {
                this.islegal=false;
                System.err.println("Invalid Hostname");
            }
            if (idx>-1){
                this.host=part[0].substring(0,pathbegin);
                idx=idx+pathbegin;
            }else{
                this.islegal=false;
                System.err.println("Invalid Hostname");
            }
        }else{
            if (this.strStr(part[0],"/")>0) {
                this.islegal=false;
                System.err.println("Invalid Port");
            }
            else this.host=part[0];
            idx=idx+this.host.length()+1;
            int portnum=0;
            while (idx<this.url.length() && this.url.charAt(idx)!='/'){
                if (this.url.charAt(idx)<'0' ||this.url.charAt(idx)>'9'){
                    this.islegal=false;
                    System.err.println("Invalid Port");
                }
                portnum=portnum*10+(this.url.charAt(idx)-'0');
                idx++;
            }
            this.port=portnum;
        }
        this.path=this.url.substring(idx);
    }
}
