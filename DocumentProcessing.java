//Authur Xinyi

import java.util.regex.*;
public class DocumentProcessing {
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
      // TODO at exercise 1
      // call handler.takeUrl for each matched url

        //String link_regex="<(A|a)(.*)\\s+(h|H)(R|r)(E|e)(F|f)\\s*=\\s*[\"|\'](.*)[\"|\']\\s*(.*)\\s*>";
        String link_regex = "<(A|a)((\\s+)|(\\s[\\S\\s]*\\s))((H|h)(R|r)(E|e)(F|f))(\\s)*=(\\s)*((\"(?<urlgroup>[^\"\']*)\")|(\'(?<urlgroup1>[^\'\"]*)\'))\\s?(\\s[\\S\\s]*)?>";
        //System.out.println(link_regex);
        Pattern p = Pattern.compile(link_regex);
        Matcher m = p.matcher(data);

        //System.out.println(data);
        while (m.find()) {
            String url;
            if (m.group("urlgroup")!=null)url=m.group("urlgroup");
            else url=m.group("urlgroup1");
            MyURL an_url=new MyURL(url);
            //String protocol=an_url.getProtocol();
            //System.out.println(protocol);
            if (an_url.getProtocol().equals("http")){
                //System.out.println("Find!");
                handler.takeUrl(url);
            }
        }
        
    }

    public static void main(String[] args){
        CharSequence data="<a_href='http://name/file.ext'><a href='http://host/file.ext2'>blah</a>";
        parseBuffer(data);
    }
}