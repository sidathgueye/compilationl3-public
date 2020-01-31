package ts;


public abstract class TsItem {
    public Ts portee;
    public int adresse;
    public String identif;

    //    public TsItem(String identif){
    //	this.identif = identif;
    //}

    public String getIdentif(){return this.identif;}
    public abstract int getTaille();
    public abstract String toString();
    public abstract Ts getTable();
}
