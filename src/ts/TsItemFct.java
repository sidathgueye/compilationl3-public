package ts;
import sa.*;


public class TsItemFct {//extends TsItem {
    public String identif;
    public int nbArgs;
    private Ts table;
    public SaDecFonc saDecFonc;
	
    public TsItemFct(String identif, int nbArgs, Ts table, SaDecFonc saDecFonc){
	this.identif = identif;
	this.nbArgs = nbArgs;
	this.table = table;
	this.saDecFonc = saDecFonc;
    }

    public int getNbArgs(){return this.nbArgs;}
    public int getTaille(){return 0;}
    public Ts getTable(){return this.table;}
    public String getIdentif(){return this.identif;}
    public String toString(){
    	return this.identif +  "\tFCT\t" + this.nbArgs;
    }

}

