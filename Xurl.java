import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class Xurl {

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

    public static void download(String requestedURL) {
        try {
            URL url = new URL(requestedURL);
            URLConnection urlConnection = null;
            BufferedReader receivingReader = null;
            urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            DataOutputStream dout=new DataOutputStream(urlConnection.getOutputStream());  
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));  
            String str=br.readLine();
            dout.write(str.getBytes());
            dout.flush();  
            dout.close();  
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
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-2);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Xurl url");
            System.exit(-1);
        }
        download(args[0]);
    }

}