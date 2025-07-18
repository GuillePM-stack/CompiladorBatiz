//package Compiler;

import javax.swing.JOptionPane;
import ArbolSintactico.*;
import java.util.Vector;

public class Parser {
    Programax p = null;
    String[] tipo = null;
    String[] variable;
    String byteString;
    private Vector tablaSimbolos = new Vector();
    private final Scanner s;
    final int ifx = 1, thenx = 2, elsex = 3, beginx = 4, endx = 5, printx = 6, semi = 7,
            sum = 8, igual = 9, igualdad = 10, intx = 11, floatx = 12, id = 13,
            longx = 14, doublex = 15, res = 16, div = 17, mul = 18, whilex = 19, dox = 20, repeatx = 21, untilx = 22;
    private int tknCode, tokenEsperado;
    private String token, tokenActual, log;

    private int cntBC = 0;
    private String bc; // String temporal de bytecode
    private int jmp1, jmp2, jmp3;
    private int aux1, aux2, aux3;
    private String pilaBC[] = new String[100];
    private String memoriaBC[] = new String[10];
    private String pilaIns[] = new String[50];
    private int retornos[] = new int[10];
    private int cntIns = 0;
    // ---------------------------------------------

    public Parser(String codigo) {
        s = new Scanner(codigo);
        token = s.getToken(true);
        tknCode = stringToCode(token);
        p = P();
    }

    // INICIO DE ANÁLISIS SINTÁCTICO
    public void advance() {
        token = s.getToken(true);
        tokenActual = s.getToken(false);
        tknCode = stringToCode(token);
    }

    public void eat(int t) {
        tokenEsperado = t;
        if (tknCode == t) {
            setLog("Token: " + token + "\n" + "Tipo:  " + s.getTipoToken());
            advance();
        } else {
            error(token, "token tipo:" + t);
        }
    }

    public Programax P() {
        Declarax d = D();
        createTable();
        Statx s = S();

        return new Programax(tablaSimbolos, s);
    }

    public Declarax D() {
        if (tknCode == id) {
            String varName = token;
            eat(id);

            if (tknCode == intx || tknCode == floatx || tknCode == longx || tknCode == doublex) {
                Typex t = T();
                eat(semi);

                tablaSimbolos.addElement(new Declarax(varName, t));

                return D();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Typex T() {
        if (tknCode == intx) {
            eat(intx);
            return new Typex("int");
        } else if (tknCode == floatx) {
            eat(floatx);
            return new Typex("float");
        } else if (tknCode == longx) {
            eat(longx);
            return new Typex("long");
        } else if (tknCode == doublex) {
            eat(doublex);
            return new Typex("double");
        } else {
            error(token, "(int / float / long / double)");
            return null;
        }

    }

    public Statx S() {
        switch (tknCode) {
            case ifx:
                Expx e1;
                Statx s1, s2;
                eat(ifx);
                e1 = E();
                eat(thenx);
                s1 = S();
                eat(elsex);
                s2 = S();
                return new Ifx(e1, s1, s2);

            case whilex:
                Expx condExp;
                Statx loopBody;
                eat(whilex);
                condExp = E();
                eat(dox);
                loopBody = S();
                return new Whilex(condExp, loopBody);

            case repeatx:
                Statx repeatBody;
                Expx untilExp;
                eat(repeatx);
                repeatBody = S();
                eat(untilx);
                untilExp = E();
                return new Repeatx(repeatBody, untilExp);

            case beginx:
                eat(beginx);
                Vector<Statx> statements = new Vector<>();

                while (tknCode != endx) {
                    Statx stmt = S();
                    if (stmt != null) {
                        statements.add(stmt);
                    }

                    if (tknCode == semi) {
                        eat(semi);
                        if (tknCode == endx) {
                            break;
                        }
                    } else if (tknCode != endx) {
                        error(token, "(; | end)");
                        break;
                    }
                }

                eat(endx);
                return new Bloquex(statements);

            case id:
                Idx i;
                Expx e;
                eat(id);
                i = new Idx(tokenActual);

                if (tknCode != igual && tknCode != sum && tknCode != res
                        && tknCode != mul && tknCode != div && tknCode != igualdad) {
                    error(token, "Después de un identificador solo se permite una asignación o una operación.");
                    return null;
                }

                declarationCheck(tokenActual);
                byteCode("igual", tokenActual);
                eat(igual);
                e = E();
                return new Asignax(i, e);

            case printx:
                Expx ex;
                eat(printx);
                ex = E();
                return new Printx(ex);

            default:
                error(token, "(if | while | begin | repeat | until | id | print)");
                return null;
        }
    }

    public void L(Vector<Statx> statements) {
        switch (tknCode) {
            case endx:
                eat(endx);
                break;

            case semi:
                eat(semi);
                Statx nextStmt = S();
                if (nextStmt != null) {
                    statements.add(nextStmt);
                }
                break;
            default:
                error(token, "(end | ;)");
        }
    }

    public Expx E() {
        Idx i1, i2;
        String comp1, comp2;

        if (tknCode == id) {
            comp1 = token;
            declarationCheck(comp1);
            eat(id);
            i1 = new Idx(comp1);
            switch (stringToCode(token)) {
                case sum:
                    eat(sum);
                    comp2 = token;
                    eat(id);
                    i2 = new Idx(comp2);
                    declarationCheck(comp2);
                    compatibilityCheck(comp1, comp2);
                    byteCode("suma", comp1, comp2);
                    System.out.println("Operación: " + comp1 + "+" + comp2);
                    return new Sumax(i1, i2);

                case res:
                    eat(res);
                    comp2 = token;
                    eat(id);
                    i2 = new Idx(comp2);
                    declarationCheck(comp2);
                    compatibilityCheck(comp1, comp2);
                    byteCode("resta", comp1, comp2);
                    System.out.println("Operación: " + comp1 + "-" + comp2);
                    return new Restax(i1, i2);

                case mul:
                    eat(mul);
                    comp2 = token;
                    eat(id);
                    i2 = new Idx(comp2);
                    declarationCheck(comp2);
                    compatibilityCheck(comp1, comp2);
                    byteCode("multiplicación", comp1, comp2);
                    System.out.println("Operación: " + comp1 + "*" + comp2);
                    return new Multiplicax(i1, i2);

                case div:
                    eat(div);
                    comp2 = token;
                    eat(id);
                    i2 = new Idx(comp2);
                    declarationCheck(comp2);
                    compatibilityCheck(comp1, comp2);
                    byteCode("división", comp1, comp2);
                    System.out.println("Operación: " + comp1 + "/" + comp2);
                    return new Divisionx(i1, i2);

                case igualdad:
                    eat(igualdad);
                    comp2 = token;
                    eat(id);
                    i2 = new Idx(comp2);
                    declarationCheck(comp2);
                    compatibilityCheck(comp1, comp2);
                    byteCode("igualdad", comp1, comp2);
                    return new Comparax(i1, i2);

                default:
                    error(token, "(+ / == / - / * / /)");
                    return null;
            }
        } else {
            error(token, "(id)");
            return null;
        }
    } // FIN DEL ANÁLISIS SINTÁCTICO

    public void error(String token, String t) {
        switch (JOptionPane.showConfirmDialog(null,
                "Error sintáctico:\n"
                        + "El token:(" + token + ") no concuerda con la gramática del lenguaje,\n"
                        + "se espera: " + t + ".\n"
                        + "¿Desea detener la ejecución?",
                "Ha ocurrido un error",
                JOptionPane.YES_NO_OPTION)) {
            case JOptionPane.NO_OPTION:
                double e = 1.1;
                break;

            case JOptionPane.YES_OPTION:
                // System.exit(0);
                break;
        }
    }

    public int stringToCode(String t) {
        int codigo = 0;
        switch (t) {
            case "if":
                codigo = ifx;
                break;
            case "then":
                codigo = thenx;
                break;
            case "else":
                codigo = elsex;
                break;
            case "begin":
                codigo = beginx;
                break;
            case "end":
                codigo = endx;
                break;
            case "print":
                codigo = printx;
                break;
            case ";":
                codigo = semi;
                break;
            case "+":
                codigo = sum;
                break;
            case ":=":
                codigo = igual;
                break;
            case "==":
                codigo = igualdad;
                break;
            case "int":
                codigo = intx;
                break;
            case "float":
                codigo = floatx;
                break;
            case "long":
                codigo = longx;
                break;
            case "double":
                codigo = doublex;
                break;
            case "-":
                codigo = res;
                break;
            case "*":
                codigo = mul;
                break;
            case "/":
                codigo = div;
                break;
            case "while":
                codigo = whilex;
                break;
            case "do":
                codigo = dox;
                break;
            case "repeat":
                codigo = repeatx;
                break;
            case "until":
                codigo = untilx;
                break;
            default:
                codigo = id;
                break;
        }
        return codigo;
    }

    public void setLog(String l) {
        if (log == null) {
            log = l + "\n \n";
        } else {
            log = log + l + "\n \n";
        }
    }

    public String getLog() {
        return log;
    }

    public void createTable() {
        variable = new String[tablaSimbolos.size()];
        tipo = new String[tablaSimbolos.size()];

        System.out.println("-----------------");
        System.out.println("TABLA DE SÍMBOLOS");
        System.out.println("-----------------");
        for (int i = 0; i < tablaSimbolos.size(); i++) {
            Declarax dx;
            Typex tx;
            dx = (Declarax) tablaSimbolos.get(i);
            variable[i] = dx.s1;
            tipo[i] = dx.s2.getTypex();
            System.out.println(variable[i] + ": " + tipo[i]);
        }

        System.out.println("-----------------\n");
    }

    public void declarationCheck(String s) {
        boolean valido = false;
        for (int i = 0; i < tablaSimbolos.size(); i++) {
            if (s.equals(variable[i])) {
                valido = true;
                break;
            }
        }
        if (!valido) {
            System.out.println("La variable " + s + " no está declarada.\nSe detuvo la ejecución.");
            javax.swing.JOptionPane.showMessageDialog(null, "La variable [" + s + "] no está declarada", "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void compatibilityCheck(String s1, String s2) {
        Declarax elementoCompara1 = null;
        Declarax elementoCompara2 = null;

        System.out.println("CHECANDO COMPATIBILIDAD ENTRE TIPOS (" + s1 + ", " + s2 + "). ");

        for (int i = 0; i < tablaSimbolos.size(); i++) {
            Declarax temp = (Declarax) tablaSimbolos.elementAt(i);
            if (s1.equals(temp.s1)) {
                elementoCompara1 = temp;
                break;
            }
        }

        for (int j = 0; j < tablaSimbolos.size(); j++) {
            Declarax temp = (Declarax) tablaSimbolos.elementAt(j);
            if (s2.equals(temp.s1)) {
                elementoCompara2 = temp;
                break;
            }
        }

        if (elementoCompara1 == null) {
            error(s1, "La variable '" + s1 + "' no ha sido declarada para la comprobación de compatibilidad.");
            return;
        }
        if (elementoCompara2 == null) {
            error(s2, "La variable '" + s2 + "' no ha sido declarada para la comprobación de compatibilidad.");
            return;
        }

        String tipo1 = elementoCompara1.s2.getTypex();
        String tipo2 = elementoCompara2.s2.getTypex();

        if (compatibilityTypes(tipo1, tipo2)) {
            System.out.println("Tipos compatibles: " + tipo1 + " y " + tipo2 + ".");
        } else {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Incompatibilidad de tipos: La operación no puede realizarse entre "
                            + elementoCompara1.s1 + " (tipo: " + tipo1 + ") y "
                            + elementoCompara2.s1 + " (tipo: " + tipo2 + ").",
                    "Error Semántico",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean compatibilityTypes(String tipo1, String tipo2) {

        String[] numericosValidos = { "int", "long", "float", "double" };

        boolean tipo1EsNumerico = java.util.Arrays.asList(numericosValidos).contains(tipo1);
        boolean tipo2EsNumerico = java.util.Arrays.asList(numericosValidos).contains(tipo2);

        if (!tipo1EsNumerico || !tipo2EsNumerico)
            return false;

        if (tipo1.equals(tipo2))
            return true;

        return (tipo1.equals("int") && tipo2.equals("long")) ||
                (tipo1.equals("long") && tipo2.equals("int")) ||
                (tipo1.equals("float") && tipo2.equals("double")) ||
                (tipo1.equals("double") && tipo2.equals("float"));
    }

    public void byteCode(String tipoOperacion, String s1, String s2) {
        int pos1 = -1, pos2 = -1;
        String tipo1 = null, tipo2 = null;

        for (int i = 0; i < tablaSimbolos.size(); i++) {
            Declarax temp = (Declarax) tablaSimbolos.elementAt(i);
            if (s1.equals(temp.s1)) {
                pos1 = i;
                tipo1 = temp.s2.getTypex();
            }
            if (s2.equals(temp.s1)) {
                pos2 = i;
                tipo2 = temp.s2.getTypex();
            }
        }

        if (tipo1 == null) {
            error(s1, "La variable '" + s1 + "' no tiene un tipo definido o no fue declarada.");
            return;
        }
        if (tipo2 == null) {
            error(s2, "La variable '" + s2 + "' no tiene un tipo definido o no fue declarada.");
            return;
        }

        String tipoFinal = tipoDominante(tipo1, tipo2);
        String prefix = tipoInstruccion(tipoFinal);

        switch (tipoOperacion) {
            case "igualdad":
                ipbc(cntIns + ": " + prefix + "load_" + pos1);
                ipbc(cntIns + ": " + prefix + "load_" + pos2);
                ipbc(cntIns + ": ifne " + (cntIns + 4));
                jmp1 = cntBC;
                break;

            case "suma":
            case "resta":
            case "multiplicación":
            case "división":
                ipbc(cntIns + ": " + prefix + "load_" + pos1);
                ipbc(cntIns + ": " + prefix + "load_" + pos2);

                String opInstr = switch (tipoOperacion) {
                    case "suma" -> prefix + "add";
                    case "resta" -> prefix + "sub";
                    case "multiplicación" -> prefix + "mul";
                    case "división" -> prefix + "div";
                    default -> "";
                };
                ipbc(cntIns + ": " + opInstr);
                jmp2 = cntBC;
                break;
        }
    }

    public void byteCode(String tipoOperacion, String s1) {
        int pos1 = -1;
        String tipo1 = null;

        for (int i = 0; i < tablaSimbolos.size(); i++) {
            Declarax temp = (Declarax) tablaSimbolos.elementAt(i);
            if (s1.equals(temp.s1)) {
                pos1 = i;
                tipo1 = temp.s2.getTypex();
                break;
            }
        }

        if (tipo1 == null) {
            error(s1, "La variable '" + s1 + "' no tiene un tipo definido o no fue declarada.");
            return;
        }

        String prefix = tipoInstruccion(tipo1);

        switch (tipoOperacion) {
            case "igual":
                ipbc(cntIns + ": " + prefix + "store_" + pos1);
                break;
        }
    }

    public String tipoInstruccion(String tipo) {
        return switch (tipo) {
            case "int" -> "i";
            case "long" -> "l";
            case "float" -> "f";
            case "double" -> "d";
            default -> "i";
        };
    }

    public String tipoDominante(String tipo1, String tipo2) {
        String[] jerarquia = { "int", "long", "float", "double" };
        int idx1 = java.util.Arrays.asList(jerarquia).indexOf(tipo1);
        int idx2 = java.util.Arrays.asList(jerarquia).indexOf(tipo2);
        return jerarquia[Math.max(idx1, idx2)];
    }

    public void ipbc(String ins) {
        while (cntBC < pilaBC.length && pilaBC[cntBC] != null) {
            cntBC++;
        }
        if (cntBC < pilaBC.length) {
            cntIns++;
            pilaBC[cntBC] = ins;
            cntBC++;
        } else {
            System.err.println("Error: Pila de bytecode llena. No se pudo agregar la instrucción: " + ins);
        }
    }

    public String getBytecode() {
        String JBC = "";
        for (int i = 0; i < pilaBC.length; i++) {
            if (pilaBC[i] != null) {
                JBC = JBC + pilaBC[i] + "\n";
            }
        }
        return JBC;
    }
}