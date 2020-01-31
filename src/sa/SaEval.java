package sa;
import java.util.*;
import ts.*;


// P -> LDEC LDEC 

// DEC -> var id taille 
// DEC -> fct id LDEC LDEC LINST 
// DEC -> var id 

// LDEC -> DEC LDEC 
// LDEC -> null 

// VAR  ->simple id 
// VAR  ->indicee id EXP

// LINST -> INST LINST 
// LINST -> null 

// INST -> aff VAR EXP 
// INST -> si EXP LINST LINST 
// INST -> tq EXP LINST 
// INST -> app APP 
// INST -> ret EXP 
// INST -> ecr EXP 

// APP -> id LEXP 

// LEXP -> EXP LEXP 
// LEXP -> null 

// EXP -> op2 EXP EXP 
// EXP -> op1 EXP 
// EXP -> VAR 
// EXP -> entier 
// EXP -> APP 
// EXP -> lire


//**********

// VAR  ->simple id 
// VAR  ->indicee id EXP

// LINST -> INST LINST 
// LINST -> null 

// INST -> aff VAR EXP 
// INST -> si EXP LINST LINST 
// INST -> tq EXP LINST 
// INST -> app APP 
// INST -> ecr EXP 

// APP -> id LEXP 

// LEXP -> EXP LEXP 
// LEXP -> null 


// EXP -> op1 EXP 
// EXP -> VAR 

// EXP -> lire


public class SaEval extends SaDepthFirstVisitor <Integer> {
    private Ts tableGlobale;
    private Ts tableLocaleCourante;

    public SaEval(SaNode root, Ts tableGlobale){
	this.tableGlobale = tableGlobale;
	this.tableLocaleCourante = null;
	root.accept(this);
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
	node.getVar().accept(this);
	defaultOut(node);
	return 1;
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
	node.getLhs().accept(this);
	node.getRhs().accept(this);
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
	defaultOut(node);
	return 1;
    }
    
    public Integer visit(SaAppel node)
    {
	defaultIn(node);
	if(node.getArguments() != null) node.getArguments().accept(this);
	defaultOut(node);
	return 1;
    }
    
    public Integer visit(SaExpAppel node)
    {
	defaultIn(node);
	node.getVal().accept(this);
	defaultOut(node);
	return 1;
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
	return 1;
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
	node.getVal().accept(this);
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
	defaultOut(node);
	return 1;
    }
    
}
