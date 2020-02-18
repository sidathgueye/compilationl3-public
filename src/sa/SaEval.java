package sa;
import java.util.*;
import ts.*;

public class SaEval extends SaDepthFirstVisitor <Integer> {
    private Ts tableGlobale;
    private SaEnvironment curEnv;
    private int[] varGlob;
    
    public SaEval(SaNode root, Ts tableGlobale){
	this.tableGlobale = tableGlobale;
	curEnv = null;
	varGlob = new int[tableGlobale.nbVar()];

	SaAppel appelMain = new SaAppel("main", null);
	appelMain.tsItem = tableGlobale.getFct("main");
	
	appelMain.accept(this);
    }
    
    public void defaultIn(SaNode node)
    {
    }

    public void defaultOut(SaNode node)
    {
    }

    // P -> LDEC LDEC 
    public Integer visit(SaProg node)
    {
	defaultIn(node);
	if(node.getVariables() != null)
	    node.getVariables().accept(this);
	if(node.getFonctions() != null)
	    node.getFonctions().accept(this);
	defaultOut(node);
	return 1;
    }
    
    // DEC -> var id taille 
    public Integer visit(SaDecTab node){
	defaultIn(node);
	defaultOut(node);
	return 1;
    }
    
    public Integer visit(SaExp node)
    {
	defaultIn(node);
	defaultOut(node);
	return 1;
    }
    
    // EXP -> entier
    public Integer visit(SaExpInt node)
    {
	defaultIn(node);
	defaultOut(node);
	return node.getVal();
    }
    public Integer visit(SaExpVar node)
    {
	defaultIn(node);
	int val = node.getVar().accept(this);
	defaultOut(node);
	return val;
    }
    
    public Integer visit(SaInstEcriture node)
    {
	defaultIn(node);
	int arg = node.getArg().accept(this);
	System.out.println(arg);
	defaultOut(node);
	return 1;
    }
    
    public Integer visit(SaInstTantQue node)
    {
	defaultIn(node);
	int test = node.getTest().accept(this);
	while (test != 0){
	    node.getFaire().accept(this);
	    test = node.getTest().accept(this);
	}
	defaultOut(node);
	return 1;
    }
    public Integer visit(SaLInst node)
    {
	defaultIn(node);
	node.getTete().accept(this);
	if(node.getQueue() != null) node.getQueue().accept(this);
	defaultOut(node);
	return 1;
    }

    // DEC -> fct id LDEC LDEC LINST 
    public Integer visit(SaDecFonc node)
    {
	defaultIn(node);
	if(node.getParametres() != null) node.getParametres().accept(this);
	if(node.getVariable() != null) node.getVariable().accept(this);
	node.getCorps().accept(this);
	defaultOut(node);
	return 1;
    }
    
    // DEC -> var id 
    public Integer visit(SaDecVar node)
    {
	defaultIn(node);
	defaultOut(node);
	return 1;
    }
    
    public Integer visit(SaInstAffect node)
    {
	defaultIn(node);
	int val = node.getRhs().accept(this);
	

	if(node.getLhs() instanceof SaVarIndicee){ // c'est une case de tableau, donc forcément globale
	    SaVarIndicee lhsIndicee = (SaVarIndicee) node.getLhs();
	    int indice = lhsIndicee.getIndice().accept(this);
	    int base = lhsIndicee.tsItem.adresse;
	    varGlob[base + indice] = val;
	}
	else{// lhs est une variable simple, trois cas possibles : une variable locale, une variable globale ou un argument
	    SaVarSimple lhsSimple = (SaVarSimple) node.getLhs();
	    if(lhsSimple.tsItem.portee == this.tableGlobale){ // variable globale
		varGlob[lhsSimple.tsItem.adresse] = val;
	    }
	    else if(lhsSimple.tsItem.isParam){ // parametre
		curEnv.setArg(lhsSimple.tsItem.adresse, val);
	    }
	    else { // variable locale
		curEnv.setVar(lhsSimple.tsItem.adresse, val);
	    }
	}
	defaultOut(node);
	return 1;
    }
    
    // LDEC -> DEC LDEC 
    // LDEC -> null 
    public Integer visit(SaLDec node)
    {
	defaultIn(node);
	node.getTete().accept(this);
	if(node.getQueue() != null) node.getQueue().accept(this);
	defaultOut(node);
	return 1;
    }
    
    public Integer visit(SaVarSimple node)
    {
	defaultIn(node);
	int val = 0;

	if(node.tsItem.portee == this.tableGlobale){ // variable globale
	    val = varGlob[node.tsItem.adresse];
	}
	else if(node.tsItem.isParam){ // parametre
	    val = curEnv.getArg(node.tsItem.adresse);
	}
	else { // variable locale
	    val = curEnv.getVar(node.tsItem.adresse);
	}
	
	defaultOut(node);
	return val;
    }
    
    public Integer visit(SaAppel node)
    {
	defaultIn(node);
	TsItemFct fct = node.tsItem;
	SaLExp lArgs = null;
	SaLExp l;
	Ts localTable = fct.getTable();
	int i = 0;
	SaEnvironment newEnv = new SaEnvironment(node.tsItem);

	//	localTable.affiche(System.out);

	for(lArgs = node.getArguments(); lArgs != null; lArgs = lArgs.getQueue()){
	    int val = lArgs.getTete().accept(this);
	    newEnv.setArg(i, val);
	    i++;
	}
	//sauvegarde de l'environnement courant pour le restaurer après l'appel
	SaEnvironment oldEnv = curEnv;
	// le nouvel environnement devient l'environnement courant
	curEnv = newEnv;
	// on exécute le corps de la fonction
	if(fct.saDecFonc.getCorps() != null)
	    fct.saDecFonc.getCorps().accept(this);

	int returnValue = curEnv.getReturnValue();
	
	//restauration de l'environnement d'avant appel
	curEnv = oldEnv;
	defaultOut(node);
	return returnValue;
    }
    
    public Integer visit(SaExpAppel node)
    {
	defaultIn(node);
	int val = node.getVal().accept(this);
	defaultOut(node);
	return val;
    }

    // EXP -> add EXP EXP
    public Integer visit(SaExpAdd node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return op1 + op2;
    }

    // EXP -> sub EXP EXP
    public Integer visit(SaExpSub node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return op1 - op2;
    }

    // EXP -> mult EXP EXP
    public Integer visit(SaExpMult node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return op1 * op2;
    }

    // EXP -> div EXP EXP
    public Integer visit(SaExpDiv node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return (int)(op1 / op2);
    }
    
    // EXP -> inf EXP EXP
    public Integer visit(SaExpInf node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return (op1 < op2)? 1 : 0;
    }

    // EXP -> eq EXP EXP
    public Integer visit(SaExpEqual node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return (op1 == op2)? 1 : 0;
    }

    // EXP -> and EXP EXP
    public Integer visit(SaExpAnd node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return (op1 != 0 && op2 != 0) ? 1 : 0;
    }
    

    // EXP -> or EXP EXP
    public Integer visit(SaExpOr node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	int op2 = node.getOp2().accept(this);
	defaultOut(node);
	return (op1 != 0 || op2 !=0)? 1 : 0;
    }

    // EXP -> not EXP
    public Integer visit(SaExpNot node)
    {
	defaultIn(node);
	int op1 = node.getOp1().accept(this);
	defaultOut(node);
	return (op1 == 0) ? 1 : 0;
    }


    public Integer visit(SaExpLire node)
    {
	defaultIn(node);
	defaultOut(node);
	return 1;
    }

    public Integer visit(SaInstBloc node)
    {
	defaultIn(node);
	node.getVal().accept(this);
	defaultOut(node);
	return 1;
    }
    
    public Integer visit(SaInstSi node)
    {
	defaultIn(node);
	int test = node.getTest().accept(this);
	if(test != 0)
	    node.getAlors().accept(this);
	else
	    if(node.getSinon() != null)
		node.getSinon().accept(this);
	defaultOut(node);
	return 1;
    }

// INST -> ret EXP 
    public Integer visit(SaInstRetour node)
    {
	defaultIn(node);
	curEnv.setReturnValue(node.getVal().accept(this));
	defaultOut(node);
	return 1;
    }

    
    public Integer visit(SaLExp node)
    {
	defaultIn(node);
	node.getTete().accept(this);
	if(node.getQueue() != null)
	    node.getQueue().accept(this);
	defaultOut(node);
	return 1;
    }

    public Integer visit(SaVarIndicee node)
    {
	defaultIn(node);
	node.getIndice().accept(this);
	int indice = node.getIndice().accept(this);
	int base = node.tsItem.adresse;
	defaultOut(node);
	return varGlob[base + indice];
    }
    
}
