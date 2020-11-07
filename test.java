
import java.util.regex.*;
public class test {
    public interface URLhandler {
        void takeUrl(String url);
    }
  
    public static URLhandler handler = new URLhandler() {
        @Override
        public void takeUrl(String url) {
            System.out.println(url);        // DON'T change anything here
        }
    };
  
    /**
     * Parse the given buffer to fetch embedded links and call the handler to
     * process these links.
     * 
     *   @param data
     *          the buffer containing the html document
     */
    public static void parseBuffer(CharSequence data) {
        //String link_regex ="GET\\s+ (?<pathtofile>[^\\s]*) \\s+ HTTP/1\\.1";
        String link_regex ="GET\\s+(?<pathtofile>[^\\s]*)\\s+HTTP/1\\.1";
        //System.out.println(link_regex);
        Pattern p = Pattern.compile(link_regex);
        Matcher m = p.matcher(data);

        System.out.println(data);
        while (m.find()) {
            System.out.println("Find!");
        }
        
    }

    public static void main(String[] args){
        CharSequence data="GET / HTTP/1.1";
        parseBuffer(data);
    }
}