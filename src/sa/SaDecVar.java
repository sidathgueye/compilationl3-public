package sa;
import ts.*;

public class SaDecVar implements SaDec{
    private String nom;
    public TsItem tsItem;

    public SaDecVar(String nom){
	this.nom = nom;
	this.tsItem = null;
    }

    public String getNom(){return this.nom;}
    
    public <T> T accept(SaVisitor <T> visitor) {
        return visitor.visit(this);
    }
}
