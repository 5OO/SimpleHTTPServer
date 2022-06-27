import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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


                    String[] fileNameSplit = resource.split("\\.");
                    String fileNameLeadString = fileNameSplit[0].substring(0, 1);
                    if (fileNameLeadString.matches("/")) {
                        String fileName = fileNameSplit[0].substring(1);
                    } else {
                        String fileName = fileNameSplit[0];
                    }
                    System.out.println("  fileNameSplit.length > 1 " + (fileNameSplit.length > 1));
                    String fileName = fileNameSplit[0].substring(1);
                    String fileExtension = "";
                    if (fileNameSplit.length > 1) {
                        fileExtension = fileNameSplit[1];
                    }


                    if (fileExtension.equals("jpg")) {

                        FileInputStream image = new FileInputStream("./resources/" + fileName + ".jpg");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK}\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
//                          clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());  // https://stackoverflow.com/questions/1321878/how-to-prevent-favicon-ico-requests
                        clientOutput.write(image.readAllBytes());
                        clientOutput.flush();
                        client.close();
                    } else if (resource.equals("/hello")) {


                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK}\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(("See on Hello World!\r\n").getBytes());
                        clientOutput.flush();
                        client.close();
                    } else if (resource.equals("/") || resource.equals("/index.htm") || resource.equals("/index.html")) {

                        FileInputStream fileInputStream = new FileInputStream("./index.html");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK}\r\n").getBytes());
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
                }


            }
        } catch (
                NullPointerException e) {
            System.out.println("NullPointerException thrown! ln 79");
        }
    }

    private static String getRequestedResourceString(StringBuilder request) {
        String firstLine = String.valueOf(request.toString().split("\n")[0]);
        String resource = firstLine.split(" ")[1];
//        System.out.println("see ongi  resurss " +resource);
        return resource;
    }
}
