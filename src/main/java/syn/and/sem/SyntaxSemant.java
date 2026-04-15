package syn.and.sem;

import lex.anal.LexikarniAnalizator;
import lex.anal.TTypSymbolu;

import static lex.anal.TTypSymbolu.*;

import java.io.IOException;


public class SyntaxSemant {
    LexikarniAnalizator lexer;

    String filenameDefault = "testInput.txt";

    public SyntaxSemant() throws IOException {
        lexer = new LexikarniAnalizator(filenameDefault);
    }

    public SyntaxSemant(String filepath) throws IOException {
        lexer = new LexikarniAnalizator(filepath);
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
                DecPart();
                CodePart();
                break;
            default:
                throw new IOException();

        }
    }

    // 2 3
    private void DecPart() throws IOException {
        switch (lexer.getSymbol()) {
            case S_VAR:
                pop(S_VAR);
                pop(S_ID);
                DecInit();
                pop(S_SEM);
                DecPart();
                break;
            case S_BEG:
                break;
            default:
                throw new IOException();
        }
    }


    // 6 7
    private void DecInit() throws IOException {
        switch (lexer.getSymbol()) {
            case S_EQ:
                pop(S_EQ);
                pop(S_NUM);
                break;
            case S_SEM:
                break;
            default:
                throw new IOException();
        }
    }
    // 8
    private void CodePart() throws IOException {
        switch (lexer.getSymbol()){
            case S_BEG:
                pop(S_BEG);
                D();
                pop(S_END);
                //TODO check if EOF
                break;
            default:
                throw new IOException();
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
                throw new IOException();
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
                throw new IOException();
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
                throw new IOException();
        }
    }

    // 14
    private void Assigh() throws IOException {
        switch (lexer.getSymbol()){
            case S_ID:
                pop(S_ID);
                A();
                break;
            default:
                throw new IOException();
        }
    }
    // 15 16
    private void A() throws IOException {
        switch (lexer.getSymbol()){
            case S_IS:
                pop(S_IS);
                V();
                pop(S_SEM);
                break;
            case S_LP:
                pop(S_LP);
                ArgList();
                pop(S_RP);
                pop(S_SEM);
                break;
            default:
                throw new IOException();
        }
    }
    // 17 18
    private void ArgList() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:
                V();
                break;
            case S_RP:
                break;
            default:
                throw new IOException();
        }
    }

    // 19
    private void Ifstm() throws IOException {
        switch (lexer.getSymbol()){
            case S_IF:
                pop(S_IF);
                pop(S_LP);
                P();
                pop(S_RP);
                pop(S_CBL);
                D();
                pop(S_CBR);
                ElsePart();
                break;
            default:
                throw new IOException();
        }
    }
    // 20 21 22
    private void ElsePart() throws IOException {
        switch (lexer.getSymbol()){
            case S_EF:
                pop(S_EF);
                pop(S_LP);
                P();
                pop(S_RP);
                pop(S_CBL);
                D();
                pop(S_CBR);
                ElsePart();
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
                throw new IOException();
        }
    }
    // 23
    private void P() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:
                V();
                Q();
                V();
                break;
            default:
                throw new IOException();
        }
    }
    // 24 25 26 27 28 29
    private void Q() throws IOException {
        switch (lexer.getSymbol()){
            case S_EQ:
                pop(S_EQ);
                break;
            case S_LQ:
                pop(S_LQ);
                break;
            case S_NEQ:
                pop(S_NEQ);
                break;
            case S_GQ:
                pop(S_GQ);
                break;
            case S_G:
                pop(S_G);
                break;
            case S_L:
                pop(S_L);
                break;
            default:
                throw new IOException();
        }
    }


    // 30
    private void V() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:
                R();
                K();
                break;
            default:
                throw new IOException();
        }
    }

    private void R() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
            case S_ID:
            case S_NUM:
                L();
                M();
                break;
            default:
                throw new IOException();
        }
    }


    // 32 33 34
    private void K() throws IOException {
        switch (lexer.getSymbol()){
            case S_PLU:
                pop(S_PLU);
                R();
                K();
                break;
            case S_MNU:
                pop(S_MNU);
                R();
                K();
                break;
            case S_RP:
            case S_EQ:
            case S_LQ:
            case S_NEQ:
            case S_GQ:
            case S_G:
            case S_L:
            case S_SEM:
                break;
            default:
                throw new IOException();
        }
    }
    // 35 36 37
    private void L() throws IOException {
        switch (lexer.getSymbol()){
            case S_LP:
                pop(S_LP);
                V();
                pop(S_RP);
                break;
            case S_ID:
                pop(S_ID);
                break;
            case S_NUM:
                pop(S_NUM);
                break;
            default:
                throw new IOException();
        }
    }
    // 38 39 40
    private void M() throws IOException {
        switch (lexer.getSymbol()){
            case S_MUL:
                pop(S_MUL);
                L();
                M();
                break;
            case S_DIV:
                pop(S_DIV);
                L();
                M();
                break;
            case S_PLU:
            case S_MNU:
            case S_RP:
            case S_EQ:
            case S_LQ:
            case S_NEQ:
            case S_GQ:
            case S_G:
            case S_L:
            case S_SEM:
                break;
            default:
                throw new IOException();
        }
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
