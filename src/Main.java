import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println(" server käivitatud\n ootame uusi sõnumeid");

            while (true) {

                try (Socket client = serverSocket.accept()) {
//                    System.out.println("Debug: tuli uus sõnum_ " + client.toString());

                    InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());

                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder request = new StringBuilder();
                    String line;
                    line = bufferedReader.readLine();
                    while (!line.isBlank()) {
                        request.append(line + "\r\n");
                        line = bufferedReader.readLine();
                    }
                    String firstLine = String.valueOf(request.toString().split("\n")[0]);
                    String resource = firstLine.split(" ")[1];
//                    System.out.println(" resurss " +resource);


                    if (resource.equals("/cat")) {
                        System.out.println(resource.equals("/cat"));
                        FileInputStream image = new FileInputStream("me.jpg");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.1 200 OK}\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(image.readAllBytes());
                        clientOutput.flush();
                    } else if (resource.equals("/hello")) {
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.1 200 OK}\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(("See on Hello World!\r\n").getBytes());
                        clientOutput.flush();
                    } else if (resource.equals("/")) {
                        FileInputStream fileInputStream = new FileInputStream("index.html");
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.1 200 OK}\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(fileInputStream.readAllBytes());
                        clientOutput.flush();
                    } else {
                        OutputStream clientOutput = client.getOutputStream();
                        clientOutput.write(("HTTP/1.1 200 OK}\r\n").getBytes());
                        clientOutput.write(("\r\n").getBytes());
                        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
                        clientOutput.write(("404 not found!\r\n").getBytes());
                        clientOutput.flush();
                    }



                    client.close();

                }

            }
        }
    }
}