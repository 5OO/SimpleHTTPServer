import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println(" lihtne HTTP server käivitatud \n ootame uusi sõnumeid");

            while (true) {

                try (Socket client = serverSocket.accept()) {
//                    System.out.println("Debug: tuli uus sõnum_ " + client.toString());

                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder request = new StringBuilder();

                    String line;
                    line = bufferedReader.readLine();           // loeme puhvrist rida haaval
                    while (!line.isBlank()) {
                        request.append(line).append("\r\n");    // lisame objekti REQUEST sisse line sisu + reavahetus \r\n return carriage/newline
                        line = bufferedReader.readLine();
                    }

                    String resource = getRequestedResourceString(request);

                    String fileName = "";
                    String fileExtension = "";

                    String[] fileNameSplitByDot = resource.split("\\.");
                    int length = fileNameSplitByDot.length;
                    String fileNameLeadString = fileNameSplitByDot[0].substring(0, 1);
                    if (fileNameLeadString.matches("/")) {
                        fileName = fileNameSplitByDot[0].substring(1);
                    } else {
                        fileName = fileNameSplitByDot[0];
                    }
//                    System.out.println("  fileNameSplitByDot.length > 1 " + (fileNameSplitByDot.length > 1));
                    fileName = fileNameSplitByDot[0].substring(1);

                    if (fileNameSplitByDot.length > 1) {
                        fileExtension = fileNameSplitByDot[1];
                    }

                    Path browserPathAbsolute = Paths.get(resource).toAbsolutePath();
                    Path browserPathRealPath = Paths.get(resource).toRealPath();
                    System.out.println( " path abs + real");
                    System.out.println(browserPathAbsolute + " absolute path");
                    System.out.println(browserPathRealPath + " real path on see");
//                    getPathNames(resource);
                    System.out.format(resource + "   ****   resursi värk  on siin  %s%n");

                    if (fileExtension.equals("jpg")) {
                        FileInputStream image = new FileInputStream("./" + fileName + ".jpg");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
//                          clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());  // https://stackoverflow.com/questions/1321878/how-to-prevent-favicon-ico-requests
                        clientOutput.write(image.readAllBytes());
                        clientOutput.flush();
                        client.close();


                    } else if (resource.equals("/hello")) {


                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(("See on Hello World!\r\n").getBytes());
                        clientOutput.flush();
                        client.close();


                    } else if (resource.equals("/") || resource.equals("/index.htm") || resource.equals("/index.html")) {

                        FileInputStream fileInputStream = new FileInputStream("./index.html");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK \r\n").getBytes());
                        clientOutput.write((" \r\n").getBytes());
//                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(fileInputStream.readAllBytes());
                        clientOutput.flush();
                        client.close();

                    } else if (fileExtension.equals("html")) {

                        FileInputStream fileInputStream = new FileInputStream("./" + fileName + ".html");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK \r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(fileInputStream.readAllBytes());
                        clientOutput.flush();
                        client.close();


                    } else {
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.1 404 Not Found}\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(("error 404: resource  " + resource + " not found!\r\n").getBytes());
                        clientOutput.flush();
                        client.close();
                    }
                } catch (IOException x) {
                    System.err.format("IOExeption: %s%n ", x);
                }


            }
        } catch (
                NullPointerException e) {
            System.err.format("NullPointerException thrown! %s%n", e);
        }
    }


    private static void getPathNames(String directoryPath) {
        String[] pathNames;
        File file = new File(directoryPath);
        pathNames = file.list();
        for (String pathName : pathNames) {
            System.out.println(pathName);
        }
    }


    private static String getRequestedResourceString(StringBuilder request) {
        String firstLine = String.valueOf(request.toString().split("\n")[0]);
        String resource = firstLine.split(" ")[1];
//        System.out.println("see ongi  resurss " +resource);
        return "." + resource;
    }
}
