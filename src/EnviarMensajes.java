import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class EnviarMensajes extends Thread {
    Usuario emisor;
    List<Usuario> usuariosConectados;
    Socket dest;
    int puerto;
    public EnviarMensajes(List<Usuario> usuariosConectados, Usuario emisor,int puerto) {
        this.usuariosConectados = usuariosConectados;
        this.emisor = emisor;
        this.puerto = puerto;
    }

    public Usuario buscarUsuario(String nombre) {
        for (Usuario u : usuariosConectados) {
            if (u.getNombre().equals(nombre)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            String mensajeEncriptado;
            ServerSocket serverSocket = new ServerSocket(puerto);

            do {
                Socket entrada = serverSocket.accept();
                InputStream is = entrada.getInputStream();
                String[] v_recibido;
                byte[] msj = new byte[1000];
                is.read(msj);
                String recibido = new String(msj);
                v_recibido = recibido.split(":");
                String s_receptor = v_recibido[0];
                mensajeEncriptado = v_recibido[1];
                Usuario receptor;
                if (buscarUsuario(s_receptor) != null) {
                    receptor = buscarUsuario(s_receptor);
                    try {
                        dest = new Socket(receptor.getIp(), ++puerto);
                        OutputStream reenviar = dest.getOutputStream();
                        String mensaje = emisor.getNombre() + ":" + mensajeEncriptado;
                        reenviar.write(mensaje.trim().getBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }


            } while (!mensajeEncriptado.equals("salir"));
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }
    }
}
