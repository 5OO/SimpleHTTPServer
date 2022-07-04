import java.net.MalformedURLException;
import java.net.URL;

public class ParseURL {
    public static void main(String[] args) throws MalformedURLException {


//
//        URL aURL = new URL("http://example.com:80/docs/books/tutorial"
//                + "/index.html?name=networking#DOWNLOADING");
        URL aURL = new URL("http://example.com:80/docs/books/tutorial"
                + "/index.html?a=7&b=8#tag");


        System.out.println("protocol = " + aURL.getProtocol());
        System.out.println("authority = " + aURL.getAuthority());
        System.out.println("host = " + aURL.getHost());
        System.out.println("port = " + aURL.getPort());
        System.out.println("path = " + aURL.getPath());
        System.out.println("query = " + aURL.getQuery());
        System.out.println("filename = " + aURL.getFile());
        System.out.println("ref = " + aURL.getRef());

//        protocol = http
//        authority = example.com:80
//        host = example.com
//        port = 80
//        path = /docs/books/tutorial/index.html
//        query = name=networking
//        filename = /docs/books/tutorial/index.html?name=networking
//        ref = DOWNLOADING
    }
}


