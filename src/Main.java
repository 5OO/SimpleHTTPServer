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

                    StringBuilder request = getStringBuilder(bufferedReader);

                    String resource = getRequestedResourceString(request);

                    String fileExtension = "";

                    String[] fileNameSplitByDot = resource.split("\\.");
                    String fileName = fileNameSplitByDot[0].substring(1);


                    if (fileNameSplitByDot.length > 1) {
                        fileExtension = fileNameSplitByDot[1];
                    }
//                    verifyIfDirectoryExists(resource);
                    if (fileExtension.equals("jpg")) {
                        if (verifyIfFileExists(fileName, fileExtension)) {
                            displayImageToOutput(client, fileName);
                        } else {
                            displayOutputMessage(client, "HTTP/1.0 404 Not Found}\r\n", ("error 404: resource  " + resource + " not found!\r\n").getBytes());
                        }

                    } else if (resource.equals("/hello")) {

                        displayOutputMessage(client, "HTTP/1.0 200 OK\r\n", ("See on Hello World!\r\n").getBytes());

                    } else if (resource.equals("/")) {
                        FileInputStream fileInputStream = new FileInputStream("./index.html");
                        displayOutputMessage(client, "HTTP/1.0 200 OK \r\n", fileInputStream.readAllBytes());

                    } else if (fileExtension.equals("html")) {
                        verifyIfFileExists(fileExtension, fileName);
                        FileInputStream fileInputStream = new FileInputStream("./" + fileName + ".html");
                        displayOutputMessage(client, "HTTP/1.0 200 OK \r\n", fileInputStream.readAllBytes());

                    } else {
// TODO vanan mõte - lõpetada kataloogi sisu kuvamine
                        //TODO uus1 - ükskõik mis failid serveris kuvada gif, wav vms
                        // TODO uus2 API päring /salary - siis käivitab palgakalkulaatori ja annab vastuse JSON; + POST päringud.
                        // TODO uus3 parameetritega päring api/salary?a=7/b=8 get päring paalga parameetritega
//                        TODO uus 4 POST päringud
                        if (verifyIfDirectoryExists(resource)) {

                            String[] directoryListing = generateDirectoryListingSimple(resource);
                            PrintWriter printWriter = new PrintWriter(client.getOutputStream());

                            printWriter.write("HTTP/1.0 200 OK \r\n");
                            printWriter.write("\r\n");
                            for (int i = 0; i < directoryListing.length; i++) {
                                printWriter.write(directoryListing[i]+"\r\n");
                            }
                            printWriter.flush();
                            printWriter.close();

                        } else {
                            displayOutputMessage(client, "HTTP/1.0 404 Not Found}\r\n", ("error 404: resource  " + resource + " not found!\r\n").getBytes());
                        }
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

    private static StringBuilder getStringBuilder(BufferedReader bufferedReader) throws IOException {
        StringBuilder request = new StringBuilder();
        String line;
        line = bufferedReader.readLine();           // loeme puhvrist rida haaval
        while (!line.isBlank()) {
            request.append(line).append("\r\n");    // lisame objekti REQUEST sisse line sisu + reavahetus \r\n return carriage/newline
            line = bufferedReader.readLine();
        }
        return request;
    }

    private static boolean verifyIfFileExists(String fileName, String fileExtension) throws IOException {
        File newFile = new File(fileName + "." + fileExtension);
        boolean fileExists;
        if (newFile.exists()) {
            fileExists = true;
        } else fileExists = false;
        return fileExists;
    }

    private static boolean verifyIfDirectoryExists(String fileName) throws IOException {
        File newFile = new File("." + fileName);
        boolean isDirectory;
        if (newFile.isDirectory()) {
            isDirectory = true;
        } else isDirectory = false;
        return isDirectory;
    }

    private static String[] generateDirectoryListingSimple(String fileName) {
        File folder = new File("." + fileName);
        File[] listOfFiles = folder.listFiles();
        String[] fileList = folder.list();
        return fileList;

    }

    private static String[] generateDirectoryListingLong(String fileName) {
        File folder = new File("." + fileName);
        File[] listOfFiles = folder.listFiles();
        String[] fileList = new String[listOfFiles.length];

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileList[i] = "File      \\\\" + listOfFiles[i];
            } else if (listOfFiles[i].isDirectory()) {
                fileList[i] = "Directory \\\\" + listOfFiles[i];
            }
        }
        return fileList;
    }

    private static void displayImageToOutput(Socket client, String fileName) throws IOException {
        FileInputStream image = new FileInputStream("./" + fileName + ".jpg");
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.0 200 OK\r\n").getBytes());
        clientOutput.write(("\r\n").getBytes());
        clientOutput.write(image.readAllBytes());
        clientOutput.flush();
        client.close();
    }

    private static void displayOutputMessage(Socket client, String x, byte[] resource) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(x.getBytes());
        clientOutput.write(("\r\n").getBytes());
        clientOutput.write(resource);
        clientOutput.flush();
        client.close();
    }

    private static String getRequestedResourceString(StringBuilder request) {
        String firstLine = String.valueOf(request.toString().split("\n")[0]);
        String resource = firstLine.split(" ")[1];
        return resource;
    }
}

// https://stackoverflow.com/questions/1321878/how-to-prevent-favicon-ico-requests
//        clientOutput.write("<head> <link rel=\"icon\" href=\"data:,\"> </head>\r\n".getBytes());
