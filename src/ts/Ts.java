package ts;
import java.io.*;
import java.util.*;

public class Ts
{
    private Map< String, TsItem> hash;
    private int adrVarCourante;
    private int adrArgCourant;
    public boolean isGlobal;


    public int getAdrVarCourante(){return adrVarCourante;}
    
    public Ts(){
	this.hash = new HashMap< String, TsItem>();
	this.adrVarCourante = 0;
	this.adrArgCourant = 0;
	this.isGlobal = false;
    }

    
    public Map<String, TsItem> getHash(){return this.hash;}

    public void addItem(String identif, TsItem item){
	item.portee = this;
	this.hash.put(identif, item);
	if(item instanceof TsItemVar){
	    item.adresse = this.adrVarCourante;
	    this.adrVarCourante += item.getTaille();
	}
	else if(item instanceof TsItemParam){
	    item.adresse = this.adrArgCourant;
	    this.adrArgCourant += item.getTaille();
	}
    }

    public TsItem getItem(String identif){
	return this.hash.get(identif);
    }

    public Ts getTableLocale(String identif){
	TsItem item = this.getItem(identif);
	return item.getTable();
    }
    
    public void afficheTout(String baseFileName){
	String fileName;
	PrintStream out = System.out;

	if (baseFileName != null){
	    try {
		baseFileName = baseFileName;
		fileName = baseFileName + ".ts";
		out = new PrintStream(fileName);
	    }
	    
	    catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
	    }
	}
       	out.println("TABLE GLOBALE");
	this.affiche(out);
	this.afficheTablesLocales(out);
    }
	 
    public void affiche(PrintStream out){
	Set< Map.Entry< String, TsItem> > st = this.hash.entrySet();    
	for (Map.Entry< String, TsItem> me:st){ 
	    out.println(me.getKey() + ":\t" + me.getValue());
	}
    }
	
    public void afficheTablesLocales(PrintStream out){
	Set< Map.Entry< String, TsItem> > st = this.hash.entrySet();    
	for (Map.Entry< String, TsItem> me:st){
	    if(me.getValue().getTable() != null){
		out.println("TABLE LOCALE : " + me.getKey());
		me.getValue().getTable().affiche(out);
	    }
	}
    }
}
