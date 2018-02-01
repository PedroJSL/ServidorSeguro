import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

class EnviarLista extends Thread {
    List<Usuario> lUsers;

    public EnviarLista(List<Usuario> lUsers) {
        this.lUsers = lUsers;
    }

    @Override
    public void run() {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(4444);
            System.out.println("Multicast Creado");
            InetAddress grupo = InetAddress.getByName("224.0.0.1");
            multicastSocket.joinGroup(grupo);
            String usuarios = "";
            for (Usuario u : lUsers) {
                usuarios += " " + u.getNombre() + " ";
            }
            usuarios = usuarios.trim();
            System.out.println(usuarios);
            DatagramPacket packet = new DatagramPacket(usuarios.getBytes(), usuarios.length(), grupo, 4444);
            multicastSocket.send(packet);
            System.out.println("Enviado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
