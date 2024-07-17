package demo.server;

import com.sun.net.httpserver.SimpleFileServer;

import java.net.InetSocketAddress;
import java.nio.file.Path;

public class FileServerMain {

    record RunParameters (int serverPort, String localDirectoryAbsolutePath){}

    public static void main(String[] args) {

        RunParameters runParameters = new RunParameters(8000, "/Users/tom/javas");

        var server = SimpleFileServer.createFileServer(
                new InetSocketAddress(runParameters.serverPort),
                Path.of(runParameters.localDirectoryAbsolutePath),
                SimpleFileServer.OutputLevel.VERBOSE
        );
        server.start();
    }
}
