import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println(" lihtne HTTP server käivitatud \n ootame uusi sõnumeid");

            while (true) {

                try (Socket client = serverSocket.accept()) {

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

                    String fileExtension = "";

                    String[] fileNameSplitByDot = resource.split("\\.");
                    String fileName = fileNameSplitByDot[0].substring(1);

                    if (fileNameSplitByDot.length > 1) {
                        fileExtension = fileNameSplitByDot[1];
                    }


                    if (fileExtension.equals("jpg")) {
                        // TODO tee siia faili olemasoli kontroll

                        FileInputStream image = new FileInputStream("./" + fileName + ".jpg");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
//                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
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


                    } else if (resource.equals("/")) {

                        FileInputStream fileInputStream = new FileInputStream("./index.html");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.0 200 OK \r\n").getBytes());
                        clientOutput.write((" \r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
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


    private static String getRequestedResourceString(StringBuilder request) {
        String firstLine = String.valueOf(request.toString().split("\n")[0]);
        String resource = firstLine.split(" ")[1];
        return resource;
    }
}

// https://stackoverflow.com/questions/1321878/how-to-prevent-favicon-ico-requests