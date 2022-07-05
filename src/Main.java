import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static final String CODE200 = "HTTP/1.0 200 OK \r\n";

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
            List<byte[]> fileListing = generateDirectoryListingLong(client, resource); // kas siin saaks ka sendResponse meetodit kasutada? kui generateDirectoryListingLong() annaks valja byte[]?
            byte[] response = listToByteArray(fileListing);
            sendResponse(client, CODE200, response);
        } else {
            sendResponse(client, "HTTP/1.0 404 Not Found}\r\n", ("error 404: resource  " + resource + " not found!\r\n").getBytes());
        }
    }

    private static byte[] listToByteArray(List<byte[]> fileListing) {
        byte[] newByteArray = new byte[0];
        int byteLength = 0;
        for (byte[] bytes : fileListing) {
            newByteArray = Arrays.copyOf(newByteArray, byteLength + bytes.length + 2);
            for (int i = 0; i < bytes.length; i++) {
                newByteArray[byteLength + i] = bytes[i];
                newByteArray[byteLength + i + 1] = 13;
                newByteArray[byteLength + i + 2] = 10;

            }

            byteLength = newByteArray.length;
        }
        return newByteArray;
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
        line = bufferedReader.readLine();
        while (!line.isBlank()) {
            request.add(line);
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

    private static List<byte[]> generateDirectoryListingLong(Socket client, String fileName) throws IOException {
        File folder = new File("." + fileName);
        File[] listOfFiles = folder.listFiles();
        List<byte[]> fileListing = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                fileListing.add(listOfFiles[i].getName().getBytes(StandardCharsets.UTF_8));

            } else if (listOfFiles[i].isFile()) {
                fileListing.add(listOfFiles[i].getName().getBytes(StandardCharsets.UTF_8));
            }
        }
        return fileListing;
    }


    //byte array stringiks
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

