package ArbolSintactico;

import java.util.Vector;

public class Bloquex extends Statx {
    Vector<Statx> sentencias;

   public Bloquex(Vector<Statx> sentencias) {
        this.sentencias = sentencias;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bloquex:\n");
        for (Statx stmt : sentencias) {
            sb.append("  ").append(stmt.toString()).append("\n");
        }
        return sb.toString();
    }
}
