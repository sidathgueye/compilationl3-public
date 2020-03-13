import sa.*;
import sc.analysis.DepthFirstAdapter;


import sc.node.*;

public class Sc2sa extends DepthFirstAdapter{

    private SaNode returnValue;

    private <T> T retrieveNode(Node node){
        if(node == null)
            return null;
        node.apply(this);
        return (T)returnValue;
    }

    public SaNode getRoot() {
        return returnValue;
    }

    @Override
    public void caseStart(Start node) {
        inStart(node);
        node.getPProgramme().apply(this);
        node.getEOF().apply(this);
        outStart(node);
    }
    //programme = {decvarldecfonc} optdecvar listedecfonc
    @Override
    public void caseADecvarldecfoncProgramme(ADecvarldecfoncProgramme node) {
        SaLDec var = retrieveNode(node.getOptdecvar());
        SaLDec fonc = retrieveNode(node.getListedecfonc());
        this.returnValue = new SaProg(var, fonc);
    }

    @Override
    public void caseALdecfoncProgramme(ALdecfoncProgramme node) {
        SaLDec var = null;
        SaLDec fonc = retrieveNode(node.getListedecfonc());
        this.returnValue = new SaProg(var,fonc);
    }
   //listedecvar ={decvarldecvar} decvar listedecvarbis
    @Override
    public void caseADecvarldecvarListedecvar(ADecvarldecvarListedecvar node) {
        SaDec tete = retrieveNode(node.getDecvar());
        SaLDec queue = retrieveNode(node.getListedecvarbis());
        this.returnValue = new SaLDec(tete, queue);
    }
    //listedecvar = {decvar} decvar;
    @Override
    public void caseADecvarListedecvar(ADecvarListedecvar node) {
        SaDec tete = retrieveNode(node.getDecvar());
        SaLDec queue = null;
        this.returnValue = new SaLDec(tete, queue);
    }
    //listedecvarbis = {decvarldecvar} virgule decvar listedecvarbis
    @Override
    public void caseADecvarldecvarListedecvarbis(ADecvarldecvarListedecvarbis node) {
        SaDec tete = retrieveNode(node.getDecvar());
        SaLDec queue = retrieveNode(node.getListedecvarbis());
        this.returnValue = new SaLDec(tete,queue);
    }

   //listedecvarbis =  {decvar} virgule decvar ;
    @Override
    public void caseADecvarListedecvarbis(ADecvarListedecvarbis node) {
        SaDec tete = retrieveNode(node.getDecvar());
        SaLDec queue = null;
        this.returnValue = new SaLDec(tete,queue);
    }
    @Override
    public void caseADecvarentierDecvar(ADecvarentierDecvar node) {
         String nom = null;
        // String nom = (String) retrieveNode(node.getIdentif());
        if (node.getIdentif() != null) {
            node.getIdentif().apply(this);
            nom = node.getIdentif().getText();
        }
        this.returnValue = new SaDecVar(nom);
    }

    @Override
    public void caseADecvartableauDecvar(ADecvartableauDecvar node) {
          String nom =  node.getIdentif().getText();;
         int taille = Integer.parseInt(node.getNombre().getText());;
         this.returnValue = new SaDecTab(nom,taille);
    }
    @Override
    public void caseALdecfoncrecListedecfonc(ALdecfoncrecListedecfonc node) {
        SaDec fnct = retrieveNode(node.getDecfonc());
		SaLDec fncts = retrieveNode(node.getListedecfonc());
        this.returnValue = new SaLDec(fnct,fncts);
    }
    @Override
    public void caseALdecfoncfinalListedecfonc(ALdecfoncfinalListedecfonc node) {
        this.returnValue = null;
    }
    @Override
    //decfonc = {decvarinstr} identif listeparam optdecvar instrbloc
    public void caseADecvarinstrDecfonc(ADecvarinstrDecfonc node) {
        String nom = node.getIdentif().getText();;
        SaLDec parametres = retrieveNode(node.getListeparam());
        SaLDec variables = retrieveNode(node.getOptdecvar());
        SaInst corps = retrieveNode(node.getInstrbloc());
        this.returnValue = new SaDecFonc(nom, parametres, variables, corps);
    }
   //decfonc = {instr} identif listeparam instrbloc ;
    @Override
    public void caseAInstrDecfonc(AInstrDecfonc node) {
        String nom = node.getIdentif().getText();
        SaLDec parametres = retrieveNode(node.getListeparam());
        SaLDec variables = null;
        SaInst corps = retrieveNode(node.getInstrbloc());
        this.returnValue = new SaDecFonc(nom, parametres, variables, corps);
    }
    //listeparam = {sansparam} parenthese_ouvrante parenthese_fermante |
    @Override
    public void caseASansparamListeparam(ASansparamListeparam node) {
        this.returnValue = null;
    }
    @Override
    public void caseAAvecparamListeparam(AAvecparamListeparam node) {
        node.getListedecvar().apply(this);
    }
    @Override
    public void caseAInstrblocInstr(AInstrblocInstr node) {
        SaLInst val =retrieveNode(node.getInstrbloc());
        this.returnValue = new SaInstBloc(val);
    }

    //instrtantque = tantque exp faire instrbloc ;

    @Override
    public void caseAInstrvideInstr(AInstrvideInstr node) {
        this.returnValue = null;
    }
    @Override
    public void caseAInstraffect(AInstraffect node) {
         SaVar lhs = retrieveNode(node.getVar());
         SaExp rhs = retrieveNode(node.getExp());
      this.returnValue = new SaInstAffect(lhs,rhs);
    }
    @Override
    public void caseAInstrbloc(AInstrbloc node) {
         SaLInst val = retrieveNode(node.getListeinst());
       this.returnValue = new SaInstBloc(val);
    }
    @Override
    public void caseALinstrecListeinst(ALinstrecListeinst node) {
        SaInst tete = retrieveNode(node.getInstr());
        SaLInst queue = retrieveNode(node.getListeinst());
        this.returnValue = new SaLInst(tete, queue);
    }
    @Override
    public void caseALinstfinalListeinst(ALinstfinalListeinst node) {
       this.returnValue = null;
    }
    @Override
    public void caseAAvecsinonInstrsi(AAvecsinonInstrsi node) {
        SaExp test  = retrieveNode(node.getExp());
        SaInst Instbloc = retrieveNode(node.getInstrbloc());
        SaInst sinon = retrieveNode(node.getInstrsinon());
        this.returnValue = new SaInstSi(test, Instbloc, sinon);
    }
    @Override
    public void caseASanssinonInstrsi(ASanssinonInstrsi node) {
        SaExp test  = retrieveNode(node.getExp());
        SaInst Instbloc = retrieveNode(node.getInstrbloc());
        SaInst sinon = null;
        this.returnValue = new SaInstSi(test, Instbloc, sinon);
    }

    @Override
    public void caseAInstrtantque(AInstrtantque node) {
      SaExp test = retrieveNode(node.getExp());
      SaInst faire = retrieveNode(node.getInstrbloc());
      this.returnValue = new SaInstTantQue(test, faire);
    }
    @Override
    public void caseAInstrappel(AInstrappel node) {
        SaAppel val = retrieveNode(node.getAppelfct());
        this.returnValue = new SaExpAppel(val);
    }
    @Override
    public void caseAInstrretour(AInstrretour node) {
        SaExp val = retrieveNode(node.getExp());
        this.returnValue = new SaInstRetour(val);
    }
    @Override
    public void caseAInstrecriture(AInstrecriture node) {
        SaExp arg = retrieveNode(node.getExp());
        this.returnValue = new SaInstEcriture(arg);
    }
    @Override
    public void caseAInstrvide(AInstrvide node) {
        this.returnValue = null;
    }
    @Override
    public void caseAOuExp(AOuExp node) {
        SaExp op1 = retrieveNode(node.getExp());
        SaExp op2 = retrieveNode(node.getExp1());
        this.returnValue = new SaExpOr(op1, op2);
    }
    @Override
    public void caseAEtExp1(AEtExp1 node) {
        SaExp op1 = retrieveNode(node.getExp1());
        SaExp op2 = retrieveNode(node.getExp2());
       this.returnValue = new SaExpAnd(op1, op2);
    }
    @Override
    public void caseAInfExp2(AInfExp2 node) {
        SaExp op1 = retrieveNode(node.getExp2());
        SaExp op2 = retrieveNode(node.getExp3());
         this.returnValue = new SaExpInf(op1, op2);
    }
    @Override
    public void caseAEgalExp2(AEgalExp2 node) {
        SaExp op1 = retrieveNode(node.getExp2());
        SaExp op2 = retrieveNode(node.getExp3());
        this.returnValue = new SaExpEqual(op1, op2);
    }
    @Override
    public void caseAPlusExp3(APlusExp3 node) {
        SaExp op1 = retrieveNode(node.getExp3());
        SaExp op2 = retrieveNode(node.getExp4());
        this.returnValue = new SaExpAdd(op1, op2);
    }
    @Override
    public void caseAMoinsExp3(AMoinsExp3 node) {
        SaExp op1 = retrieveNode(node.getExp3());
        SaExp op2 = retrieveNode(node.getExp4());
        this.returnValue = new SaExpSub(op1, op2);
    }
    @Override
    public void caseAFoisExp4(AFoisExp4 node) {
        SaExp op1 = retrieveNode(node.getExp4());
        SaExp op2 = retrieveNode(node.getExp5());
        this.returnValue = new SaExpMult(op1, op2);
    }
    @Override
    public void caseADiviseExp4(ADiviseExp4 node) {
        SaExp op1 = retrieveNode(node.getExp4());
        SaExp op2 = retrieveNode(node.getExp5());
        this.returnValue = new SaExpDiv(op1, op2);
    }
    @Override
    public void caseANonExp5(ANonExp5 node) {
        SaExp op1 = retrieveNode(node.getExp5());
        this.returnValue = new SaExpNot(op1);
    }
    @Override
    public void caseANombreExp6(ANombreExp6 node) {
        this.returnValue = new SaExpInt(Integer.parseInt(node.getNombre().getText()));
    }
    @Override
    public void caseAAppelfctExp6(AAppelfctExp6 node) {
       SaAppel appel = retrieveNode(node.getAppelfct());
        this.returnValue = new SaExpAppel(appel);
    }
    @Override
    public void caseAVarExp6(AVarExp6 node) {
        SaVar var = retrieveNode(node.getVar());
        this.returnValue = new SaExpVar(var);
    }

    @Override
    public void caseALireExp6(ALireExp6 node){
        this.returnValue = new SaExpLire();
    }
   //var = {vartab} identif crochet_ouvrant exp crochet_fermant
    @Override
    public void caseAVartabVar(AVartabVar node) {
         String nom = node.getIdentif().getText();;
         SaExp indice = retrieveNode(node.getExp());
        this.returnValue = new SaVarIndicee(nom,indice);
    }
    @Override
    public void caseAVarsimpleVar(AVarsimpleVar node) {
        String nom = node.getIdentif().getText();;
        this.returnValue = new SaVarSimple(nom);
    }
    @Override
    public void caseARecursifListeexp(ARecursifListeexp node) {
        SaExp tete = retrieveNode(node.getExp());
        SaLExp queue = retrieveNode(node.getListeexpbis());
        this.returnValue = new SaLExp(tete, queue);
    }
    @Override
    public void caseAFinalListeexp(AFinalListeexp node) {
        this.returnValue = new SaLExp(retrieveNode(node.getExp()),null);
    }
    @Override
    public void caseAFinalListeexpbis(AFinalListeexpbis node) {
        this.returnValue = new SaLExp(retrieveNode(node.getExp()),null);
    }
    @Override
    public void caseARecursifListeexpbis(ARecursifListeexpbis node) {
        SaExp tete = retrieveNode(node.getExp());
        SaLExp queue = retrieveNode(node.getListeexpbis());
        this.returnValue = new SaLExp(tete, queue);
    }
    @Override
    public void caseAAvecparamAppelfct(AAvecparamAppelfct node) {
        String nom = node.getIdentif().getText();;
        SaLExp arguments = retrieveNode(node.getListeexp());
       this.returnValue = new SaAppel(nom, arguments);
    }
    @Override
    public void caseASansparamAppelfct(ASansparamAppelfct node) {
        String nom = node.getIdentif().getText();
        this.returnValue = new SaAppel(nom,null);
    }
}