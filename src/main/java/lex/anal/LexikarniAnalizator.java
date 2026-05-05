package lex.anal;

import static lex.anal.TTypSymbolu.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LexikarniAnalizator  {
    String urlToFile;
    private BufferedReader reader;
    private TVstup vstup;
    private TSymbol symbol;

    private static final int POCET_STAVU = 13;
    private static final int POCET_ZNAKU = 12;

    private static final int S_CHYBA = 13;
    private static final int S_IF = 14;
    private static final int S_EF = 15;
    private static final int S_EE = 16;
    private static final int S_VAR = 17;
    private static final int S_BEG = 18;
    private static final int S_END = 19;

    private static int[][] cisloRad = new int[POCET_STAVU][POCET_ZNAKU];

    public TTypSymbolu getSymbol(){
        return this.symbol.typ;
    }

    public String getSymbolValue(){
        return symbol.atributo;
    }

    public LexikarniAnalizator(String filename) throws IOException {
        this.reader = new BufferedReader(new FileReader(filename));
        this.vstup = new TVstup();
        this.symbol = new TSymbol();
    }

    //Cte symbol po symbolu, vraci velka pismena (neni case sensetive)
    public char dejZnak() throws IOException {
        int c = reader.read(); // cteme dalsi symbol

        if (c == -1) { // eof
            vstup.konec = true;
            vstup.znak = '\0';
            return vstup.znak;
        }

        vstup.znak = (char) c;

        if (vstup.znak == '\n') { // novy radek
            vstup.cisloRad++;
            vstup.pozice = 0;
        } else {
            vstup.znak = Character.toUpperCase(vstup.znak);
            vstup.pozice++;
        }

        return vstup.znak;
    }



    public boolean jeKonec() {
        return vstup.konec;
    }

    public int getCisloRadku() {
        return vstup.cisloRad;
    }

    public int getPozice() {
        return vstup.pozice;
    }


    private static void nactiTabulkuPrechoduKSlova(){
        for(int i = 0; i < POCET_STAVU; i++){
            for(int j = 0; j < POCET_ZNAKU; j++){
                cisloRad[i][j] = S_CHYBA;
            }

        }
        // V priloze pdf z tabulkou
        cisloRad[0][0] = 1;
        cisloRad[0][2] = 2;
        cisloRad[0][5] = 6;
        cisloRad[0][8] = 8;
        cisloRad[1][1] = S_IF;
        cisloRad[2][3] = 3;
        cisloRad[2][10] = 12;
        cisloRad[3][0] = 4;
        cisloRad[3][4] = 5;
        cisloRad[4][1] = S_EF;
        cisloRad[5][2] = S_EE;
        cisloRad[6][6] = 7;
        cisloRad[7][7] = S_VAR;
        cisloRad[8][2] = 9;
        cisloRad[9][9] = 10;
        cisloRad[10][0] = 11;
        cisloRad[11][10] = S_BEG;
        cisloRad[12][11] = S_END;
    }

    // funkce na£te jeden symbol do globální prom¥nné symbol
    public void lex() throws IOException {

        symbol.atributo = "";

        //preskoci prazdne radky
        while (!vstup.konec && Character.isWhitespace(vstup.znak)) {
            dejZnak();
        }

        //zacina pismenem
        if( vstup.znak >= 'A' && vstup.znak <= 'Z'){
            do{
                symbol.atributo += vstup.znak;
                dejZnak();
            } while(( vstup.znak >= 'A' && vstup.znak <= 'Z')
                    || ( vstup.znak >= '0' && vstup.znak <= '9'));
            symbol.typ = S_ID;
            lexKlicSlova(symbol.atributo);
        }

        //zacina cislem
        else if (vstup.znak >= '0' && vstup.znak <= '9') {
            do {
                symbol.atributo += vstup.znak ;
                dejZnak();
            } while (vstup.znak >= '0' && vstup.znak <= '9') ;
            symbol.typ = S_NUM;
        }

        //Porovnavaci symbol != == <= >= ( ) + - * / ;
        else {
            switch(vstup.znak) {
                case '!':
                    dejZnak();
                    switch(vstup.znak) {
                        case '=':
                            dejZnak();
                            symbol.typ = S_NEQ;
                            break;
                        default:
                            //TODO exception
                            throw new LexicalException("Neznámý znak: " + vstup.znak);
                    }
                    break;
                case  '<':
                    dejZnak();
                    switch(vstup.znak) {
                        case '=':
                            dejZnak();
                            symbol.typ = S_LQ;
                            break;
                        default:
                            symbol.typ = S_L;
                            break;
                    }
                    break;
                case '>':
                    dejZnak();
                    switch(vstup.znak) {
                        case '=':
                            dejZnak();
                            symbol.typ = S_GQ;
                            break;
                        default:
                            symbol.typ = S_G;
                            break;
                    }
                    break;
                case '=':
                    dejZnak();
                    switch(vstup.znak) {
                        case '=':
                            dejZnak();
                            symbol.typ = S_EEQ;
                            break;
                        default:
                            symbol.typ = S_EQ;
                            break;
                    }
                    break;
                case '+':
                    dejZnak();
                    symbol.typ = S_PLU;
                    break;
                case '-':
                    dejZnak();
                    symbol.typ = S_MNU;
                    break;
                case  '*':
                    dejZnak();
                    symbol.typ = S_MUL;
                    break;
                case '/':
                    dejZnak();
                    symbol.typ = S_DIV;
                    break;
                case ':':
                    dejZnak();
                    switch(vstup.znak) {
                        case '=':
                            dejZnak();
                            symbol.typ = S_IS;
                            break;
                        default:
                            //TODO exception
                            throw new LexicalException("Neznámý znak: " + vstup.znak);
                    }
                    break;
                case '(':
                    dejZnak();
                    symbol.typ = S_LP;
                    break;
                case ')':
                    dejZnak();
                    symbol.typ = S_RP;
                    break;
                case ';':
                    dejZnak();
                    symbol.typ = S_SEM;
                    break;
                case  '{':
                    dejZnak();
                    symbol.typ = S_CBL;
                    break;
                case  '}':
                    dejZnak();
                    symbol.typ = S_CBR;
                    break;
                case '\0':
                    symbol.typ = S_ENDOFFILE;
                    break;
                default:
                    // TODO exception
                    throw new LexicalException("Neznámý znak: " + vstup.znak);
            }
        }
    }


    //Overuje zda string je klicove slovo
    private void lexKlicSlova(String str) throws IOException {
        int stav = 0;
        int delka = str.length();
        int pozice = 0;

        List<Integer> zakodSlovo = zakodujSlovo(str);

        while(stav != S_CHYBA && pozice < delka) {
            if(zakodSlovo.get(pozice) == -1) {
                stav = S_CHYBA;
                break;
            }
            stav = cisloRad[stav][zakodSlovo.get(pozice)];
            pozice++;
        }

        switch(stav) {
            case S_IF:
                symbol.typ = TTypSymbolu.S_IF;
                break;
            case S_EF:
                symbol.typ = TTypSymbolu.S_EF;
                break;
            case S_EE:
                symbol.typ = TTypSymbolu.S_EE;
                break;
            case S_VAR:
                symbol.typ = TTypSymbolu.S_VAR;
                break;
            case S_BEG:
                symbol.typ = TTypSymbolu.S_BEG;
                break;
            case S_END:
                symbol.typ = TTypSymbolu.S_END;
                break;
            default: symbol.typ = S_ID;
        }
    }

    //Pomocna funkce pro prevod kic slova do cisel at muze pohybovat po tabulce
    private List<Integer> zakodujSlovo(String str) throws RuntimeException {
        List<Integer> list = new ArrayList<>();

        for(int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
                case 'I': list.add(0); break;
                case 'F': list.add(1); break;
                case 'E': list.add(2); break;
                case 'L': list.add(3); break;
                case 'S': list.add(4); break;
                case 'V': list.add(5); break;
                case 'A': list.add(6); break;
                case 'R': list.add(7); break;
                case 'B': list.add(8); break;
                case 'G': list.add(9); break;
                case 'N': list.add(10); break;
                case 'D': list.add(11); break;
                default:
                    list.clear();
                    list.add(-1);
                    break;
            }
        }
        //System.out.println("Zakoduj slovo: " + list);
        return list;
    }

    public void initLex() throws IOException {
        nactiTabulkuPrechoduKSlova();
        dejZnak();
    }

//    public static void main(String[] args) throws IOException {
//        LexikarniAnalizator la = new LexikarniAnalizator("testInput.txt");
//        BufferedWriter bw = new BufferedWriter(new FileWriter("lexVystup.txt"));
//
//        la.initLex();
//        la.lex();
//        System.out.println(la.symbol.typ.toString() + " " + la.symbol.atributo);
//        bw.write(la.symbol.typ.toString() + " " + la.symbol.atributo);
//        bw.newLine();
//        while(la.symbol.typ != S_ENDOFFILE){
//            la.lex();
//            System.out.println(la.symbol.typ.toString() + " " + la.symbol.atributo);
//            bw.write(la.symbol.typ.toString() + " " + la.symbol.atributo);
//            bw.newLine();
//        }
//        bw.close();
//    }




}
