import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println(" lihtne HTTP server käivitatud \n ootame uusi sõnumeid");

            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleRequest(client);
                } catch (Exception e) {
                    System.err.format("Exception: %s%n ", e);
                }
            }
        }

    }

    private static void handleRequest(Socket client) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        List<String> request = readRequest(bufferedReader);

        String resource = getRequestedResource(request);

        // TODO uus2 API päring /salary - siis käivitab palgakalkulaatori ja annab vastuse JSON; + POST päringud.
        // TODO uus3 parameetritega päring api/salary?a=7/b=8 get päring paalga parameetritega
//                        TODO uus4 POST päringud


        if (resource.equals("/")) {
            resource = "/index.html";
        }

        if (resource.equals("/api/salary")) {
            // ....
            // sendResponse(...)

        } else if (verifyIfFile(resource)) {
            sendResponse(client, "HTTP/1.0 200 OK \r\n", Files.readAllBytes(Path.of(".", resource)));
        } else if (verifyIfDirectoryExists(resource)) {
            generateDirectoryListingLong(client, resource); // kas siin saaks ka sendResponse meetodit kasutada? kui generateDirectoryListingLong() annaks valja byte[]?
        } else {
            sendResponse(client, "HTTP/1.0 404 Not Found}\r\n", ("error 404: resource  " + resource + " not found!\r\n").getBytes());
        }
    }

    private static void sendFileToOutputStream(Socket client, String resource) throws IOException {

        PrintWriter printWriter = new PrintWriter(client.getOutputStream());

        printWriter.write("HTTP/1.0 200 OK \r\n");
        printWriter.write("\r\n");
        printWriter.write(resource);
        printWriter.flush();
        printWriter.close();
    }

    private static List<String> readRequest(BufferedReader bufferedReader) throws IOException {
        List<String> request = new ArrayList<>();
        String line;
        line = bufferedReader.readLine();           // loeme puhvrist rida haaval
        while (!line.isBlank()) {
            request.add(line);    // lisame objekti REQUEST sisse line sisu + reavahetus \r\n return carriage/newline
            line = bufferedReader.readLine();
        }
        return request;
    }

    private static boolean verifyIfFile(String fullPath) throws IOException {
        return new File("." + fullPath).isFile();
    }


    private static boolean verifyIfDirectoryExists(String fileName) throws IOException {
        File newFile = new File("." + fileName);
        boolean isDirectory = false;
        if (newFile.exists()) {
            if (newFile.isDirectory()) {
                isDirectory = true;
            }
        }
        return isDirectory;
    }

      private static void generateDirectoryListingLong(Socket client, String fileName) throws IOException {
        File folder = new File("." + fileName);
        File[] listOfFiles = folder.listFiles();

        PrintWriter printWriter = new PrintWriter(client.getOutputStream());
        printWriter.write("HTTP/1.0 200 OK \r\n");
        printWriter.write("\r\n");
        printWriter.write("content listing for: " + fileName + "  \r  \n");
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                printWriter.write("File      " + listOfFiles[i] + "  \r\n");
            } else if (listOfFiles[i].isDirectory()) {
                printWriter.write("Directory " + listOfFiles[i] + " \r \n");
            }
        }
        printWriter.flush();
        printWriter.close();
    }

    private static void sendResponse(Socket client, String status, byte[] response) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(status.getBytes());
        clientOutput.write(("\r\n").getBytes());
        clientOutput.write(response);
        clientOutput.flush();
    }

    private static String getRequestedResource(List<String> request) {
        String firstLine = request.get(0);
        return firstLine.split(" ")[1];
    }
}

