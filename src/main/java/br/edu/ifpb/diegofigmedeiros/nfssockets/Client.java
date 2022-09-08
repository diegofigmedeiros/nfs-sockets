package br.edu.ifpb.gugawag.so.sockets;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 7000);
        Scanner in = new Scanner(socket.getInputStream());
        while(in.hasNextLine()) {
            System.out.println(in.nextLine());
        }
        in.close();
        socket.close();
    }
}
