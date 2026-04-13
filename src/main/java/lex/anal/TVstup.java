package lex.anal;

public class TVstup {

    char znak;
    int cisloRad;
    int pozice;
    boolean konec;


    public TVstup(char znak, int cisloRad, int pozice, boolean konec) {
        this.znak = znak;
        this.cisloRad = cisloRad;
        this.pozice = pozice;
        this.konec = konec;
    }



    public TVstup() {
        this.konec = false;
        this.pozice = 0;
        this.cisloRad = 1;
        this.znak = '\0';
    }




}
