package ts;

public class TsItemVar extends TsItem {
    public int taille;

    public TsItemVar(String identif, int taille){
	//	super(identif);
	this.identif = identif;
	this.taille = taille;
	this.adresse = 0;
	this.portee = null;
    }

    public int getTaille(){return this.taille;}
    public int getAdresse(){return this.adresse;}
    public Ts getTable(){return null;}

    public String toString(){
    	return "VAR\t" + this.identif + "\t" + this.taille + "\t" + this.adresse;
    }
    
}

