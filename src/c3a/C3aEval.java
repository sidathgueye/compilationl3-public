package c3a;
import java.util.*;
import java.io.*;
import ts.*;

public class C3aEval implements C3aVisitor <Integer> {
  private C3a c3a;
  private Ts tableGlobale;
  private TsItemFct currentFct;
  private TsItemFct storeCurrentFct;
  private int[] reg;
  private int[] stack;
  private int[] varGlob;
  private int eip;
  private int ebp;
  private int esp;
  private int stackSize;
  private Map< Integer, Integer> label2index;
  private Map< String, Integer> function2index;
  private C3aOperand storeReturnValue;
  private boolean debug;
  private boolean stop;
  private ArrayList<String> programOutput = new ArrayList<String>();
    
  public C3aEval(C3a c3a, Ts tableGlobale){
  	debug = false;
  	this.c3a = c3a;
  	stackSize = 1000;
  	reg = new int[c3a.getTempCounter()];
  	stack = new int[stackSize];
  	esp = 0; 
  	varGlob = new int[tableGlobale.nbVar()];
  	this.tableGlobale = tableGlobale;
  	this.currentFct = null;
  	storeReturnValue = null;
  	label2index = new HashMap< Integer, Integer>();
  	function2index = new HashMap< String, Integer>();
  	C3aInst c3aInst = null;
  	for(int i = 0; i < c3a.listeInst.size(); i++){
  	    c3aInst = c3a.listeInst.get(i);
  	    if(c3aInst.label != null){
  		label2index.put(c3aInst.label.number, i);
  	    }
  	    if(c3aInst instanceof C3aInstFBegin){
  		String identif = ((C3aInstFBegin)c3aInst).val.identif;
  		function2index.put(identif, i);
  		if(identif.equals("main")){
  		    eip = i;
  		}
  	    }
  	}
  	push(-1); // popping -1 after call to main, mean that we are done
  	push(-1); // popping -1 after call to main, mean that we are done
  	stop = false;
  	while(!stop){
  	    c3a.listeInst.get(eip).accept(this);
  	}
  }


  public void affiche(String baseFileName){
	  String fileName;
	  PrintStream out = System.out;

	  if (baseFileName != null){
	    try {
		    baseFileName = baseFileName;
		    fileName = baseFileName + ".c3aout";
		    out = new PrintStream(fileName);
	    }
	    
	    catch (IOException e) {
		    System.err.println("Error: " + e.getMessage());
	    }
	  }
    for (String line : programOutput)
      out.println(line);
  }

    
    public void printStack(){
	for(int i = esp-1; i >= 0; i--){
	    System.out.println(i + ":" + stack[i]);
	}
    }

    public void push(int i){
	if(esp == stackSize){
	    System.out.println("stack overflow !!!");
	    System.exit(1);
	}
	stack[esp] = i;
	esp++;
    }

    public int pop(){
	if(esp == 0){
	    System.out.println("cannot pop empty stack !!!");
	    System.exit(1);
	}
	esp--;
	return stack[esp];
    }
    
    public Integer visit(C3aConstant constant){
	return constant.val;
    }
    
    public Integer visit(C3aTemp temp){
	return reg[temp.num];
    }
 
    public Integer visit(C3aVar var){
	int adresse = var.item.adresse;
	// variable globale
	if(var.item.portee == this.tableGlobale){ 
	    int offset = (var.index != null)? var.index.accept(this) : 0;
	    return varGlob[adresse + offset];
	}
	if(var.item.isParam){ // parametre
	    int nbArgs = this.currentFct.nbArgs;
	    return stack[ebp - 2 - nbArgs + adresse];
	}
	// variable locale
	return stack[ebp + 1 + adresse];
    }

    public void affect(C3aOperand op, int val){
	if(op instanceof C3aVar){
	    C3aVar var = (C3aVar) op;
	    int adresse = var.item.adresse;
	    // variable globale
	    if(var.item.portee == this.tableGlobale){ 
		int offset = (var.index != null)? var.index.accept(this) : 0;
		varGlob[adresse + offset] = val;
	    }
	    else if(var.item.isParam){ // parametre
		int nbArgs = this.currentFct.nbArgs;
		stack[ebp - 2 - nbArgs + adresse] = val;
	    }
	    else // variable locale
		stack[ebp + 1 + adresse] = val;
	}
	else if(op instanceof C3aTemp){
	    C3aTemp temp = (C3aTemp) op;
	    reg[temp.num] = val;
	}
    }

    public Integer visit(C3aFunction fct){
	return null;
    }

    public Integer visit(C3aLabel label){
	return label.number;
    }
    /*--------------------------------------------------------------------------------------------------------------*/
    
    public Integer visit(C3aInstAdd inst){
	if(debug){ System.out.println(inst); printStack();}
	affect(inst.result, inst.op1.accept(this) + inst.op2.accept(this));
	eip++;
	return null;
    }

    public Integer visit(C3aInstSub inst){
	if(debug){ System.out.println(inst); printStack();}
	affect(inst.result, inst.op1.accept(this) - inst.op2.accept(this));
	eip++;
	return null;
    }

    public Integer visit(C3aInstMult inst){
	if(debug){ System.out.println(inst); printStack();}
	affect(inst.result, inst.op1.accept(this) * inst.op2.accept(this));
	eip++;
	return null;
    }

    public Integer visit(C3aInstDiv inst){
	if(debug){ System.out.println(inst); printStack();}
	affect(inst.result, inst.op1.accept(this) / inst.op2.accept(this));
	eip++;
	return null;
    }

    public Integer visit(C3aInstJumpIfLess inst){
	if(debug){ System.out.println(inst); printStack();}
	if(inst.op1.accept(this) < inst.op2.accept(this))
	    eip = label2index.get(inst.result.accept(this));
	else
	    eip++;
	return null;
    }

    public Integer visit(C3aInstJumpIfEqual inst){
	if(debug){ System.out.println(inst); printStack();}
	if(inst.op1.accept(this) == inst.op2.accept(this))
	    eip = label2index.get(inst.result.accept(this));
	else
	    eip++;
	return null;
    }

    public Integer visit(C3aInstJumpIfNotEqual inst){
	if(debug){ System.out.println(inst); printStack();}
	if(inst.op1.accept(this) != inst.op2.accept(this))
	    eip = label2index.get(inst.result.accept(this));
	else
	    eip++;
	return null;
    }
    
    public Integer visit(C3aInstJump inst){
	if(debug){ System.out.println(inst); printStack();}
	eip =  label2index.get(inst.result.accept(this));
	return null;
    }

    public Integer visit(C3aInstAffect inst){
	if(debug){ System.out.println(inst); printStack();}
	affect(inst.result, inst.op1.accept(this));
	eip++;
	return null;
    }

    public Integer visit(C3aInstParam inst){
	if(debug){ System.out.println(inst); printStack();}
	push(inst.op1.accept(this));
	eip++;
	return null;
    }

    public Integer visit(C3aInstReturn inst){
	if(debug){ System.out.println(inst); printStack();}
	stack[ebp - 2] = (inst.op1 != null)? inst.op1.accept(this) : 0;
	eip++;
	return null;
    }

    public Integer visit(C3aInstWrite inst){
	if(debug){ System.out.println(inst); printStack();}
	programOutput.add(inst.op1.accept(this).toString());
	eip++;
	return null;
    }

    public Integer visit(C3aInstCall inst){
	if(debug){ System.out.println(inst); printStack();}
	storeReturnValue = inst.result;
	// allocation mémoire pour la valeur de retour
	esp++;
	// sauvegarde de l'index de l'instruction a effectuer après l'appel
	push(eip + 1);
	storeCurrentFct = currentFct;
	eip = function2index.get(inst.op1.val.identif);
	return null;
   }

    public Integer visit(C3aInstFBegin inst){
	if(debug){ System.out.println(inst); printStack();}
	currentFct = inst.val;
	int nbVarLoc = currentFct.getTable().getAdrVarCourante();
	
	// sauvegarde de l'ancienne valeur de ebp
	push(ebp);
	// nouvelle valeur de ebp
	ebp = esp - 1;
	// allocation des variables locales
	esp = esp + nbVarLoc;
	eip++;
	return null;
    }

    public Integer visit(C3aInstFEnd inst){
	if(debug){ System.out.println(inst); printStack();}
	int nbVarLoc = currentFct.getTable().getAdrVarCourante();
	int nbParam = currentFct.nbArgs;
	esp = esp - nbVarLoc;
	ebp = pop();
	eip = pop();
	if(eip == -1){ // popping -1 means we are done
	    stop = true;
	    return null;
	}
	int rv = pop();
	if(storeReturnValue != null){
	    affect(storeReturnValue, rv);
	    storeReturnValue = null;
	}
	currentFct = storeCurrentFct;
	esp = esp - nbParam;
	return null;
    }

    public Integer visit(C3aInstRead inst){
	eip++;
	return null;
    }


    public Integer visit(C3aInst inst){
	return null;}
    
}
