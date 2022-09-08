package br.edu.ifpb.gugawag.so.sockets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);
        System.out.println("==Servidor==");
        while(true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Cliente " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            out.println("Tchau!");
            out.close();
            clientSocket.close();
        }

    }
}
