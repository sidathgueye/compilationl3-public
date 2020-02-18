package sa;
import java.util.*;
import ts.*;

public class SaEnvironment {
    private int[] vars;
    private int[] args;
    private int returnValue;

    public SaEnvironment (TsItemFct fct)
    {
	SaLExp lArgs = null;
	Ts localTable = fct.getTable();
	int i = 0;

	//	System.out.println("allocation d'un nouvel environnement");
	//	System.out.println("nb var = " + localTable.nbVar());
	//	System.out.println("nb arg = " + localTable.nbArg());
	
       	args = new int[localTable.nbArg()];
	vars = new int[localTable.nbVar()];
	returnValue = 0;
    }

    public int getVar(int i){return vars[i];}
    public void setVar(int i, int val){vars[i] = val;}
    
    public int getArg(int i){return args[i];}
    public void setArg(int i, int val){args[i] = val;}
    
    public int getReturnValue(){return returnValue;}
    public void setReturnValue(int val){returnValue = val;}
}
