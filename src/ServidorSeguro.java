import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLServerSocketFactory;

public class ServidorSeguro extends Thread {
    static Socket nuevoSocket;
    static int puertoEnvio = 9091;
    static List<Usuario> usuariosConectados;


    public static void main(String[] args) {
        usuariosConectados = new ArrayList();
        try {
            System.out.println("Creando socket seguro...");

            System.setProperty("javax.net.ssl.keyStore", "./src/certs/serverKey.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "servpass");
            System.setProperty("javax.net.ssl.trustStore", "./src/certs/serverTrustedCerts.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "servpass");

            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket serverSocket = factory.createServerSocket();

            System.out.println("Realizando bind");
            InetSocketAddress address = new InetSocketAddress(9090);
            serverSocket.bind(address);
            System.out.println("Esperando conexiones...");

            while (true) {
                nuevoSocket = serverSocket.accept();
                NuevoUsuario nu = new NuevoUsuario();
                nu.start();
                puertoEnvio++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class NuevoUsuario extends Thread {

        @Override
        public void run() {
            try {
                try {
                    InputStream is = nuevoSocket.getInputStream();
                    byte[] msj = new byte[1000];
                    is.read(msj);
                    String usuario = new String(msj);
                    String ip = nuevoSocket.getInetAddress().toString();
                    ip = ip.substring(1);
                    System.out.println(usuario + ":" + ip);
                    Usuario u = new Usuario(usuario, ip);
                    usuariosConectados.add(u);
                    new EnviarLista(usuariosConectados).start();
                    EnviarMensajes em = new EnviarMensajes(usuariosConectados, u,puertoEnvio);
                    em.start();

                    em.join();
                    System.out.println(u.getNombre()+" se ha desconectado.");
                    usuariosConectados.remove(u);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (SSLHandshakeException e) {
                System.out.println("Certificado desconocido.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
