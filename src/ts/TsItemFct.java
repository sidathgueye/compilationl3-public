package ts;

public class TsItemFct extends TsItem {
    public int nbArgs;
    private Ts table;
    
    public TsItemFct(String identif, int nbArgs, Ts table){
	//	super(identif);
	this.identif = identif;
	this.nbArgs = nbArgs;
	this.table = table;
	this.portee = null;
    }

    public int getNbArgs(){return this.nbArgs;}
    public int getTaille(){return 0;}
    public Ts getTable(){return this.table;}
    public String toString(){
    	return "FCT\t" + this.identif + "\t" + this.nbArgs;
    }

}

