import c3a.*;
import ts.Ts;
import sa.*;
import ts.TsItemFct;
import ts.TsItemVar;

public class Sa2c3a extends SaDepthFirstVisitor<C3aOperand> {

    private C3a c3a;
    private Ts table;
    private Ts currentTable;

    public Sa2c3a(SaNode root, Ts table){
        this.c3a = new C3a();
        this.table = table;
        root.accept(this);
    }

    private TsItemVar getVar(String name){

        if(this.currentTable.getVar(name) != null){
            return this.currentTable.getVar(name);
        } else{
            return this.table.getVar(name);
        }
    }

    @Override
    public C3aOperand visit(SaProg node) {
        node.getFonctions().accept(this);
        return null;
    }

    @Override
    public C3aOperand visit(SaExpInt node) {
        return new C3aConstant(node.getVal());
    }

    @Override
    public C3aOperand visit(SaExpVar node) {
        return node.getVar().accept(this);
    }

    @Override
    public C3aOperand visit(SaInstEcriture node) {
        C3aInstWrite inst = new C3aInstWrite(node.getArg().accept(this),"");
        c3a.ajouteInst(inst);
        return null;
    }

    @Override
    public C3aOperand visit(SaInstTantQue node) {
        C3aLabel test = c3a.newAutoLabel();
        C3aLabel suite = c3a.newAutoLabel();
        c3a.addLabelToNextInst(test);
        C3aOperand codeTest = node.getTest().accept(this);
        c3a.ajouteInst(new C3aInstJumpIfEqual(codeTest,c3a.False,suite,""));
        node.getFaire().accept(this);
        c3a.ajouteInst(new C3aInstJump(test,""));
        c3a.addLabelToNextInst(suite);
        return null;
    }

    @Override
    public C3aOperand visit(SaDecFonc node){
        TsItemFct fct = table.getFct(node.getNom());
        C3aInstFBegin begin = new C3aInstFBegin(fct,"entree fonction");
        c3a.ajouteInst(begin);
        this.currentTable = this.table.getTableLocale(node.getNom());
        if(node.getCorps() != null)
            node.getCorps().accept(this);
        C3aInstFEnd end = new C3aInstFEnd("");
        c3a.ajouteInst(end);
        return null;
    }

    @Override
    public C3aOperand visit(SaInstAffect node) {
        C3aInstAffect instAffect = new C3aInstAffect(
                node.getRhs().accept(this),
                node.getLhs().accept(this),
                "");
        c3a.ajouteInst(instAffect);
        return null;
    }

    @Override
    public C3aOperand visit(SaVarSimple node) {
        TsItemVar tsItemVar = this.getVar(node.getNom());
        return new C3aVar(tsItemVar,null);
    }

    @Override
    public C3aOperand visit(SaAppel node) {
        if(node.getArguments() != null){
            node.getArguments().accept(this);
        }
        C3aFunction function = new C3aFunction(this.table.getFct(node.getNom()));
        C3aOperand result = c3a.newTemp();
        C3aInstCall call = new C3aInstCall(function, result, "");
        c3a.ajouteInst(call);
        return result;
    }

    @Override
    public C3aOperand visit(SaExpAppel node) {
        return node.getVal().accept(this);
    }

    @Override
    public C3aOperand visit(SaExpAdd node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp result = c3a.newTemp();
        c3a.ajouteInst(new C3aInstAdd(op1,op2,result,""));
        return result;
    }

    @Override
    public C3aOperand visit(SaExpSub node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp result = c3a.newTemp();
        c3a.ajouteInst(new C3aInstSub(op1,op2,result,""));
        return result;
    }

    @Override
    public C3aOperand visit(SaExpMult node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp result = c3a.newTemp();
        c3a.ajouteInst(new C3aInstMult(op1,op2,result,""));
        return result;
    }

    @Override
    public C3aOperand visit(SaExpDiv node) {
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        C3aTemp result = c3a.newTemp();
        c3a.ajouteInst(new C3aInstDiv(op1,op2,result,""));
        return result;
    }

    @Override
    public C3aOperand visit(SaExpInf node) {
        C3aTemp temp = c3a.newTemp();
        C3aLabel l0 = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        c3a.ajouteInst(new C3aInstAffect(c3a.True,temp,""));
        c3a.ajouteInst(new C3aInstJumpIfLess(op1,op2,l0,""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False,temp,"") );
        c3a.addLabelToNextInst(l0);
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpEqual node) {
        C3aTemp temp = c3a.newTemp();
        C3aLabel l0 = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        c3a.ajouteInst(new C3aInstAffect(c3a.True,temp,""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1,op2,l0,""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False,temp,"") );
        c3a.addLabelToNextInst(l0);
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpAnd node) {
        C3aTemp temp = c3a.newTemp();
        C3aLabel l0 = c3a.newAutoLabel();
        C3aLabel l1 = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1,c3a.False,l1,""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op2,c3a.False,l1,""));
        c3a.ajouteInst(new C3aInstAffect(c3a.True,temp,""));
        c3a.ajouteInst(new C3aInstJump(l0,""));
        c3a.addLabelToNextInst(l1);
        c3a.ajouteInst(new C3aInstAffect(c3a.False,temp,""));
        c3a.addLabelToNextInst(l0);
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpOr node) {
        C3aTemp temp = c3a.newTemp();
        C3aLabel l0 = c3a.newAutoLabel();
        C3aLabel l1 = c3a.newAutoLabel();
        C3aOperand op1 = node.getOp1().accept(this);
        C3aOperand op2 = node.getOp2().accept(this);
        c3a.ajouteInst(new C3aInstJumpIfNotEqual(op1,c3a.False,l1,""));
        c3a.ajouteInst(new C3aInstJumpIfNotEqual(op2,c3a.False,l1,""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False,temp,""));
        c3a.ajouteInst(new C3aInstJump(l0,""));
        c3a.addLabelToNextInst(l1);
        c3a.ajouteInst(new C3aInstAffect(c3a.True,temp,""));
        c3a.addLabelToNextInst(l0);
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpNot node) {
        C3aTemp temp = c3a.newTemp();
        C3aLabel l0 = c3a.newAutoLabel();
        C3aOperand op1  = node.getOp1().accept(this);
        c3a.ajouteInst(new C3aInstAffect(c3a.True,temp,""));
        c3a.ajouteInst(new C3aInstJumpIfEqual(op1, c3a.False,l0,""));
        c3a.ajouteInst(new C3aInstAffect(c3a.False,temp,""));
        c3a.addLabelToNextInst(l0);
        return temp;
    }

    @Override
    public C3aOperand visit(SaExpLire node) {
        C3aOperand argument = node.accept(this);
        c3a.ajouteInst(new C3aInstRead(argument,""));
        return null;
    }

    @Override
    public C3aOperand visit(SaInstSi node) {
        C3aLabel l0 = c3a.newAutoLabel();
        C3aOperand test = node.getTest().accept(this);
        c3a.ajouteInst(new C3aInstJumpIfEqual(test,c3a.False,l0,""));
        if(node.getAlors()!=null)
            node.getAlors().accept(this);

        if(node.getSinon()!=null){
            C3aLabel l1 = c3a.newAutoLabel();
            c3a.ajouteInst(new C3aInstJump(l1, ""));
            c3a.addLabelToNextInst(l0);
            node.getSinon().accept(this);
            c3a.addLabelToNextInst(l1);
        }
        else{
            c3a.addLabelToNextInst(l0);
        }
        return null;
    }

    @Override
    public C3aOperand visit(SaInstRetour node) {
        C3aOperand argument = node.getVal().accept(this);
        c3a.ajouteInst(new C3aInstReturn(argument,""));
        return null;
    }

    @Override
    public C3aOperand visit(SaLExp node) {
        C3aOperand argument = node.getTete().accept(this);
        c3a.ajouteInst(new C3aInstParam(argument,""));
        if(node.getQueue() != null)
            node.getQueue().accept(this);
        return null;
    }

    @Override
    public C3aOperand visit(SaVarIndicee node) {
        TsItemVar tsItemVar = this.getVar(node.getNom());
        return new C3aVar(tsItemVar,node.getIndice().accept(this));
    }

    public C3a getC3a() {
        return c3a;
    }

    public void setC3a(C3a c3a) {
        this.c3a = c3a;
    }
}
