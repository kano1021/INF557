import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

public class Xurl_ex1 {
    public static final boolean SAVE_FILE = true;

    public static void save(StringBuilder buffer, String path)
        throws FileNotFoundException {
        String fileName = null;
        if (path.isEmpty())
            fileName = "index";
        else {
            String[] names = path.split("/");
            fileName = names[names.length - 1];
        }
        PrintWriter file;
        file = new PrintWriter(fileName);
        file.write(buffer.toString());
        file.flush();
        file.close();
    }

    public static void download(String urlstring) {
        try {
            MyURL url = new MyURL(urlstring);
            if (!url.islegal()){
                System.err.println("Please entre a valid url");
            }
            String fileName=getFileName(url.getPath());
            System.out.println(fileName);
            File file=new File(file,"~/");
            URLConnection urlConnection = null;
            BufferedReader receivingReader = null;
            urlConnection = url.openConnection();
            receivingReader = new BufferedReader(
            new InputStreamReader(urlConnection.getInputStream()));
            String line = null;
            StringBuilder buffer = new StringBuilder();
            do {
                line = receivingReader.readLine();
                buffer.append(line).append('\n');
            } while (line != null);
            DocumentProcessing.parseBuffer(buffer.toString());
            if (SAVE_FILE) {
                save(buffer, url.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-2);
        }
    }

    public static String getFileName(String path){
        String name="index";
        int position =path.lastIndexOf("/");
        if (position>0){
            name=path.substring(position+1);
            if (name.trim().length()>0)return name;
        }
        return name;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Xurl url");
            System.exit(-1);
        }
        download(args[0]);
    }
}
