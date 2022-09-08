package br.edu.ifpb.diegofigmedeiros.nfssockets;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class Servidor2 {
    private static final Logger LOG = Logger.getLogger("Servidor");

    private static final String HOME = System.getProperty("user.home");
    private static final String NFS_HOME = HOME + "\\Documents\\GitHub\\nfs-sockets\\files";

    // ??
    public static String readdir() throws IOException {

        LOG.info("Informando Arquivos do Servidor");

        Path diretorioRaiz = Paths.get(NFS_HOME);

        // Maneiras de fazer usando Stream de Path
        //String lista = Files.list(diretorioRaiz).map(x -> x.toString().substring(x.toString().indexOf("files\\") + 6)).collect(Collectors.joining("\n"));
        //String lista = Files.list(diretorioRaiz).map(x -> x.toString().substring(x.toString().lastIndexOf("\\" ) + 1)).collect(Collectors.joining("\n"));
        String lista = Files.list(diretorioRaiz).map(x -> x.toString().split("\\\\")[x.toString().split("\\\\").length - 1]).collect(Collectors.joining("\n"));
        
        //Outra maneira de fazer usando classe File
        //File diretorioRaizF = new File(NFS_HOME);
        //String list = String.join("\n", Arrays.stream(Objects.requireNonNull(diretorioRaizF.list())).toList());
        return lista;
    }

    // Moving Files - https://www.baeldung.com/java-nio-2-file-api#moving-files
    public static String rename(String nomeArquivo, String nomeArquivoRenomeado) throws IOException {
        LOG.info(String.format("Renomeando arquivo: <%s> para <%s>\n", nomeArquivo, nomeArquivoRenomeado));

        Path arquivoOriginal = Paths.get(NFS_HOME + "/" + nomeArquivo);
        Path arquivoRenomeado = Paths.get(NFS_HOME + "/" + nomeArquivoRenomeado);

        if (Files.exists(arquivoOriginal) && !Files.exists(arquivoRenomeado)) {
            String mensagem = "Arquivo %s renomeado para %s";
            LOG.info(String.format(mensagem, nomeArquivo, nomeArquivoRenomeado));
            Files.move(arquivoOriginal, arquivoRenomeado);
            //usando REPLACE_EXISTE evita erro quando arquivo ja existe
            //Files.move(arquivoOriginal, arquivoNovo, StandardCopyOption.REPLACE_EXISTING);
            return String.format(mensagem, nomeArquivo, nomeArquivoRenomeado);
        } else if (Files.exists(arquivoRenomeado)) {
            String mensagem = "Não é possivel renomear o arquivo pois %s já existe no diretório";
            LOG.warning(String.format(mensagem, nomeArquivoRenomeado));
            return String.format(mensagem, nomeArquivo, nomeArquivoRenomeado);
        } else if (!Files.exists(arquivoOriginal)) {
            String mensagem = "Arquivo %s não encontrado";
            LOG.warning(String.format(mensagem, nomeArquivo));
            return String.format(mensagem, nomeArquivo);
        }

        return String.format("Arquivo %s renomeado para %s", nomeArquivo, nomeArquivoRenomeado);
    }

    // Creating files - https://www.baeldung.com/java-nio-2-file-api#creating-files
    public static String create(String nomeArquivo) throws IOException {
        LOG.info(String.format("Criando arquivo: <%s>\n", nomeArquivo));

        Path p = Paths.get(NFS_HOME + "/" + nomeArquivo);


        if (!Files.exists(p)) {
            String mensagem = "Arquivo %s criado";
            LOG.info(String.format(mensagem, nomeArquivo));
            Files.createFile(p);
            return String.format(mensagem, nomeArquivo);
        } else {
            String mensagem = "Arquivo %s já existe, não pode ser criado";
            LOG.warning(String.format(mensagem, nomeArquivo));
            return String.format(mensagem, nomeArquivo);
        }

    }

    // Deleting a File - https://www.baeldung.com/java-nio-2-file-api#deleting-a-file
    public static String remove(String nomeArquivo) throws IOException {
        LOG.info(String.format("Criando arquivo: <%s>\n", nomeArquivo));

        Path p = Paths.get(NFS_HOME + "/" + nomeArquivo);

        if (Files.exists(p)) {
            String mensagem = "Arquivo %s foi excluido";
            LOG.info(String.format(mensagem, nomeArquivo));
            Files.deleteIfExists(p);
            return String.format(mensagem, nomeArquivo);
        } else {
            String mensagem = "Arquivo %s não existe";
            LOG.warning(String.format(mensagem, nomeArquivo));
            return String.format(mensagem, nomeArquivo);
        }

    }

    public static void main(String[] args) throws IOException {
        int port = 7001;

        // Configurando o socket
        LOG.info(String.format("Servidor iniciando na porta: %s\n", port));
        ServerSocket serverSocket = new ServerSocket(port);
        Socket socket = serverSocket.accept();
        // Cliente conectou
        LOG.info(String.format("Cliente conectou com o endereço: %s:%s%n", socket.getInetAddress(), socket.getPort()));


        // pegando uma referência do canal de saída do socket. Ao escrever nesse canal, está se enviando dados para o
        // servidor
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        // pegando uma referência do canal de entrada do socket. Ao ler deste canal, está se recebendo os dados
        // enviados pelo servidor
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // laço infinito do servidor
        while (true) {
            System.out.println("Cliente> " + socket.getInetAddress());


            String mensagem = dis.readUTF();

            switch (mensagem.split(" ")[0].toLowerCase()) {
                case "readdir" -> dos.writeUTF(readdir());
                case "rename" -> dos.writeUTF(rename(mensagem.split(" ")[1], mensagem.split(" ")[2]));
                case "create" -> dos.writeUTF(create(mensagem.split(" ")[1]));
                case "remove" -> dos.writeUTF(remove(mensagem.split(" ")[1]));
            }

        }
        /*
         * Observe o while acima. Perceba que primeiro se lê a mensagem vinda do cliente (linha 29, depois se escreve
         * (linha 32) no canal de saída do socket. Isso ocorre da forma inversa do que ocorre no while do Cliente2,
         * pois, de outra forma, daria deadlock (se ambos quiserem ler da entrada ao mesmo tempo, por exemplo,
         * ninguém evoluiria, já que todos estariam aguardando.
         */
    }
}
