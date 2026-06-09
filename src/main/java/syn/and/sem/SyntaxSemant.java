package syn.and.sem;

import lex.anal.LexikarniAnalizator;
import lex.anal.TTypSymbolu;

import javax.sound.midi.Soundbank;

import static lex.anal.TTypSymbolu.*;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.Scanner;


public class SyntaxSemant {
    LexikarniAnalizator lexer;

    String filenameDefault = "testInput.txt";

    private HashMap<String, Integer> symTable;
    private List<String> decVar;
    private List<String> functions;

    // flag pro if else
    private boolean execute = true;

    public SyntaxSemant() throws IOException {
        lexer = new LexikarniAnalizator(filenameDefault);
        Inicializations();
    }

    public SyntaxSemant(String filepath) throws IOException {
        lexer = new LexikarniAnalizator(filepath);
        Inicializations();
    }

    private void Inicializations(){
        symTable = new HashMap<>();
        functions = new ArrayList<>();
        decVar = new ArrayList<>();
    }

    public void S_Analiza() throws IOException {
        Init();
        Start();
        Done();
    }


    // 1
    private void Start() throws IOException {
        switch (lexer.getSymbol()) {
            case S_VAR:
            case S_BEG:
                functions.add("READLINE");
                functions.add("WRITELINE");
                decVar.add("READLINE");
                DecPart();
                CodePart();
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());

        }
    }

    // 2 3
    private void DecPart() throws IOException {
        switch (lexer.getSymbol()) {
            case S_VAR:
                pop(S_VAR);
                String id = lexer.getSymbolValue();

                if(decVar.contains(id)){
                    throw new SemanticException("Variable: " + id + " is already declined.");
                } else {
                    //Declaration
                    decVar.add(id);
                }

                pop(S_ID);
                DecInit(id);
                pop(S_SEM);
                DecPart();
                break;
            case S_BEG:
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }


    // 6 7
    private void DecInit(String id) throws IOException {
        switch (lexer.getSymbol()) {
            case S_EQ:
                pop(S_EQ);
                // Sym
                String value = lexer.getSymbolValue();
                int v = 0;
                try{
                    v = Integer.parseInt(value);
                } catch (Exception ex){
                    throw new NumberFormatException();
                }

                if(decVar.contains(id)) {
                    symTable.put(id,v);
                }

                // Sym end
                pop(S_NUM);
                break;
            case S_SEM:
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 8
    private void CodePart() throws IOException {
        switch (lexer.getSymbol()){
            case S_BEG:
                pop(S_BEG);
                D();
                pop(S_END);
                //check if EOF
                if (lexer.getSymbol() != S_ENDOFFILE) {
                    throw new IOException("Expected EOF");
                }
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }

    //9
    private void D() throws IOException {
        switch (lexer.getSymbol()){
            case S_ID:
            case S_IF:
            case S_END:
            case S_CBR:
                StmtList();
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 10 11
    private void StmtList() throws IOException {
        switch (lexer.getSymbol()){
            case S_ID:
            case S_IF:
                Stmt();
                StmtList();
                break;
            case S_END:
            case S_CBR:
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 12 13
    private void Stmt() throws IOException {
        switch (lexer.getSymbol()){
            case S_ID:
                Assigh();
                break;
            case S_IF:
                Ifstm();
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }

    // 14
    private void Assigh() throws IOException {
        switch (lexer.getSymbol()){
            case S_ID:
                String sym = lexer.getSymbolValue();
                if(functions.contains(sym)){
                    throw new SemanticException("Cannot assign to function!");
                }
                if(!decVar.contains(sym)){
                    throw new SemanticException("Variable is not declared!");
                }

                pop(S_ID);
                A(sym);
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 15 16
    private void A(String id) throws IOException {
        switch (lexer.getSymbol()){

            case S_IS:
                pop(S_IS);

                int value = V();
                // Sym start

                if(execute){

                    if(!symTable.containsKey(id)){
                        symTable.put(id, value);
                    } else {
                        symTable.replace(id, value);
                    }
                }


                // end
                pop(S_SEM);
                break;
            case S_LP:
                pop(S_LP);
                Integer value1 = ArgList();

                pop(S_RP);
                pop(S_SEM);
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 17 18
    private Integer ArgList() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:
                return V();
            case S_RP:
                return null;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }

    // 19
    private void Ifstm() throws IOException {
        switch (lexer.getSymbol()){
            case S_IF:
                pop(S_IF);
                pop(S_LP);
                boolean cond = P();
                pop(S_RP);
                pop(S_CBL);

                boolean prevExec = execute;

                execute = prevExec && cond;

                D();
                pop(S_CBR);

                execute = prevExec && !cond;

                ElsePart();

                execute = prevExec;
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 20 21 22
    private void ElsePart() throws IOException {
        switch (lexer.getSymbol()){
            case S_EF:
                pop(S_EF);
                pop(S_LP);
                boolean cond = P();
                pop(S_RP);
                pop(S_CBL);

                boolean prevExec = execute;

                execute = prevExec && cond;
                D();
                pop(S_CBR);

                execute = prevExec && !cond;

                ElsePart();

                execute = prevExec;
                break;
            case S_EE:
                pop(S_EE);
                pop(S_CBL);
                D();
                pop(S_CBR);
                break;
            case S_ID:
            case S_IF:
            case S_END:
            case S_CBR:
                break;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 23
    private boolean P() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:

                int value1 = V();
                TTypSymbolu operator = Q();
                int value2 = V();

                // Evaluete statment
                switch (operator){
                    case S_EEQ:
                        return value1 == value2;
                    case S_LQ:
                        return value1 <= value2;
                    case S_NEQ:
                        return value1 != value2;
                    case S_GQ:
                        return value1 >= value2;
                    case S_G:
                        return value1 > value2;
                    case S_L:
                        return value1 < value2;
                    default:
                        throw new SemanticException("Wrong statment.");
                }
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 24 25 26 27 28 29
    private TTypSymbolu Q() throws IOException {
        switch (lexer.getSymbol()){
//            case S_EQ:
//                pop(S_EQ);
//                return S_EQ;
            case S_LQ:
                pop(S_LQ);
                return S_LQ;
            case S_NEQ:
                pop(S_NEQ);
                return S_NEQ;
            case S_GQ:
                pop(S_GQ);
                return S_GQ;
            case S_G:
                pop(S_G);
                return S_G;
            case S_L:
                pop(S_L);
                return S_L;
            case S_EEQ:
                pop(S_EEQ);
                return S_EEQ;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }


    // 30
    private int V() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:
                int v1 = R();
                return K(v1);
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }

    private int R() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:
                int v1 = L();
                return M(v1);
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }


    // 32 33 34
    private int K(int in) throws IOException {
        switch (lexer.getSymbol()){
            case S_PLU:
                pop(S_PLU);
                int v1 = R();
                return K(in + v1);
            case S_MNU:
                pop(S_MNU);
                int v2 = R();
                return K(in - v2);
            case S_RP:
            //case S_EQ:
            case S_LQ:
            case S_NEQ:
            case S_GQ:
            case S_G:
            case S_L:
            case S_SEM:
            case S_EEQ:
                return in;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }
    // 35 36 37
    private int L() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
                pop(S_LP);
                int v1 = V();
                pop(S_RP);
                return v1;
            case S_ID:
                String key = lexer.getSymbolValue();

                Integer value1 = -1;

                pop(S_ID);

                Integer value3 = Param();

                if (key.equals("READLINE")){
                    value1 = ReadInput();
                } else
                if (key.equals("WRITELINE")){
                    System.out.println(value3);
                    value1 = value3;
                } else {
                    if(!symTable.containsKey(key)){
                        throw new SemanticException("Veriable is not initiated!");
                    }
                    value1 = symTable.get(key);
                }

                return value1;
            case S_NUM:
                int value2 = 0;
                try {
                    value2 = Integer.parseInt(lexer.getSymbolValue());
                } catch (Exception ex){
                    throw new SemanticException("Expected integer!");
                }

                pop(S_NUM);
                return value2;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }

    private Integer Param() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
                pop(S_LP);
                Integer value1 = ArgList();
                pop(S_RP);
                return value1;
            case S_DIV:
            case S_MUL:
            case S_PLU:
            case S_MNU:
            case S_RP:
            case S_LQ:
            case S_NEQ:
            case S_GQ:
            case S_G:
            case S_L:
            case S_SEM:
            case S_EEQ:
                return null;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());

        }
    }

    // 38 39 40
    private int M(int in) throws IOException {
        switch (lexer.getSymbol()){
            case S_MUL:
                pop(S_MUL);
                int v1 = L();
                return M(in * v1);
            case S_DIV:
                pop(S_DIV);
                int v2 = L();
                // Osetreni deleni 0
                if(v2 == 0) {
                    throw new SemanticException("Division by zero!");
                }
                return M(in / v2);
            case S_PLU:
            case S_MNU:
            case S_RP:
            case S_LQ:
            case S_NEQ:
            case S_GQ:
            case S_G:
            case S_L:
            case S_SEM:
            case S_EEQ:
                return in;
            default:
                throw new IOException("Unexpected token: " + lexer.getSymbol());
        }
    }

    private int ReadInput() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Zadejte hodnotu");
        int output = -1;
        try {
            output = sc.nextInt();
        } catch (Exception ex){
            throw new IOException("Zadany vstup neni typu Int");
        }

        return output;
    }

    private void pop(TTypSymbolu typ) throws IOException {
        if(lexer.getSymbol() == typ){
            lexer.lex();
        } else {
            throw new IOException("Syntakticka chyba!");
        }
    }

    private void Init() throws IOException {
        lexer.initLex();
        lexer.lex();
    }

    private void Done() {
        System.out.println("Analiza je v poradku");
    }

    public static void main(String[] args) throws IOException {
        SyntaxSemant analizator = new SyntaxSemant();

        analizator.S_Analiza();
    }
}
