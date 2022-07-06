import java.net.MalformedURLException;
import java.net.URL;

public class ParseURL {

    private String protocol;
    private String authority;
    private String host;
    private String port;
    private String path;
    private String query;
    private String filename;
    private String ref;

    public ParseURL() {
    }

    public ParseURL(String protocol, String authority, String host, String port, String path, String query, String filename, String ref) {
        this.protocol = protocol;
        this.authority = authority;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.filename = filename;
        this.ref = ref;
    }

    public ParseURL(String query) {
        this.query = query;
    }

//
//        URL aURL = new URL("http://example.com:80/docs/books/tutorial"
//                + "/index.html?name=networking#DOWNLOADING");
//        URL aURL = new URL("http://codeborne.com:8080/api/salary/index.html?a=7&b=8#tag");
//
//
//        System.out.println("protocol = " + aURL.getProtocol());
//        System.out.println("authority = " + aURL.getAuthority());
//        System.out.println("host = " + aURL.getHost());
//        System.out.println("port = " + aURL.getPort());
//        System.out.println("path = " + aURL.getPath());
//        System.out.println("query = " + aURL.getQuery());
//        System.out.println("filename = " + aURL.getFile());
//        System.out.println("ref = " + aURL.getRef());

    // näide 1 ("http://example.com:80/docs/books/tutorial/index.html?name=networking#DOWNLOADING");
//        protocol = http
//        authority = example.com:80
//        host = example.com
//        port = 80
//        path = /docs/books/tutorial/index.html
//        query = name=networking
//        filename = /docs/books/tutorial/index.html?name=networking
//        ref = DOWNLOADING

// näide 2 "http://codeborne.com:8080/api/salary/index.html?a=7&b=8#tag");
//        protocol = http
//        authority = codeborne.com:8080
//        host = codeborne.com
//        port = 8080
//        path = /api/salary/index.html
//        query = a=7&b=8
//        filename = /api/salary/index.html?a=7&b=8
//        ref = tag
    }



