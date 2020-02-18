package sa;
import java.io.*;

public class Sa2Xml extends SaDepthFirstVisitor < Void > {
    private int indentation = 0;
    private String baseFileName;
    private String fileName;
    private PrintStream out;

    public Sa2Xml(SaNode root, String baseFileName)
    {
	if (baseFileName == null){
	    this.out = System.out;	    
	}
	else{
	    try {
		this.baseFileName = baseFileName;
		this.fileName = baseFileName + ".sa";
		this.out = new PrintStream(this.fileName);
	    }
	    
	    catch (IOException e) {
		System.err.println("Error: " + e.getMessage());
	    }
	}
	root.accept(this);
    }
    
    public void defaultIn(SaNode node)
    {
	for(int i = 0; i < indentation; i++){this.out.print(" ");}
	indentation++;
	this.out.println("<" + node.getClass().getSimpleName() + ">");
    }

    public void defaultOut(SaNode node)
    {
	indentation--;
	for(int i = 0; i < indentation; i++){this.out.print(" ");}
	this.out.println("</" + node.getClass().getSimpleName() + ">");
    }


    public Void visit(SaDecVar node)
    {
	defaultIn(node);
	for(int i = 0; i < indentation; i++){this.out.print(" ");}
	this.out.println("<nom>"+node.getNom()+"</nom>");
	defaultOut(node);
	return null;
    }
    
    public Void visit(SaDecFonc node)
    {
	defaultIn(node);
	for(int i = 0; i < indentation; i++){this.out.print(" ");}
	this.out.println("<nom>"+node.getNom()+"</nom>");
	if(node.getParametres() != null) node.getParametres().accept(this);
	if(node.getVariable() != null) node.getVariable().accept(this);
	if (node.getCorps() != null) node.getCorps().accept(this);
	defaultOut(node);
	return null;
    }

    public Void visit(SaExpInt node)
    {
	defaultIn(node);
	for(int i = 0; i < indentation; i++){this.out.print(" ");}
	this.out.println("<val>"+node.getVal()+"</val>");
	defaultOut(node);
	return null;
    }

    public Void visit(SaVarSimple node)
    {
	defaultIn(node);
	for(int i = 0; i < indentation; i++){this.out.print(" ");}
	this.out.println("<nom>"+node.getNom()+"</nom>");
	defaultOut(node);
	return null;
    }

    
}
