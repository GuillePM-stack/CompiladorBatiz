package ArbolSintactico;

public class Repeatx  extends Statx {
    private Statx body;
    private Expx condition;

    public Repeatx(Statx body, Expx condition) {
        this.body = body;
        this.condition = condition;
    }

    public Object[] getVariables() {
        Object obj[] = new Object[2];
        obj[0] = body;
        obj[1] = condition;
        return obj;
    }
    
}
