import domain.GrossSalary;
import service.SalaryInformationResponse;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static service.SalaryCalculatorService.getSalaryInformation;

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

        //  tehtud  uus2 API päring /salary - siis käivitab palgakalkulaatori ja annab vastuse
        // tehtud  uus3 parameetritega päring api/salary?a=7/b=8 get päring palga parameetritega
//                        TODO uus4 POST päringud JSON; + POST päringud.


        if (resource.equals("/")) {
            resource = "/index.html";
        }
        if (resource.contains("?")) {
            String queryParameter = extractQueryParameter(resource);
            int input = Integer.parseInt(queryParameter);
            SalaryInformationResponse response = getSalaryInformation(new GrossSalary(BigDecimal.valueOf(input)));
            System.out.println(response);
        } else if (resource.equals("/api/salary")) {
            SalaryInformationResponse response = getSalaryInformation(new GrossSalary(BigDecimal.valueOf(5000)));
            System.out.println(response);

            String toOutput = String.valueOf(response);
            byte[] newOutput = toOutput.getBytes(StandardCharsets.UTF_8);
            sendResponse(client, CODE200, newOutput);


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

    private static String extractQueryParameter(String resource) {
        String queryParameter = resource.substring(resource.indexOf("?"));
        queryParameter = queryParameter.split("&")[0];
        return queryParameter.split("=")[1];
    }



    private static byte[] listToByteArray(List<byte[]> fileListing) {
        byte[] newByteArray = new byte[0];
        int byteArrayLength = 0;
        for (byte[] bytes : fileListing) {
            newByteArray = Arrays.copyOf(newByteArray, byteArrayLength + bytes.length + 2);
            for (int i = 0; i < bytes.length; i++) {
                newByteArray[byteArrayLength + i] = bytes[i];
                newByteArray[byteArrayLength + i + 1] = 13;  // lisa rea lõppu  return carriage
                newByteArray[byteArrayLength + i + 2] = 10; // lisa rea lõppu ka new line
            }

            byteArrayLength = newByteArray.length;
        }
        return newByteArray;
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

    private static String parseURL(List<String> request) {
        String host, path, query, queryParam1, queryParam2;
        host = request.get(1);
        host = host.split("\\s")[1];
        path = getRequestedResource(request);
        query = path.split("\\?")[1];
        queryParam1 = query.split("&")[0];
        queryParam2 = query.split("&")[1];
        queryParam1 = queryParam1.split("=")[1];
        queryParam2 = queryParam2.split("=")[2];
        return queryParam1;
    }

    private static void sendFileToOutputStream(Socket client, String resource) throws IOException {

        PrintWriter printWriter = new PrintWriter(client.getOutputStream());

        printWriter.write("HTTP/1.0 200 OK \r\n");
        printWriter.write("\r\n");
        printWriter.write(resource);
        printWriter.flush();
        printWriter.close();
    }
}

