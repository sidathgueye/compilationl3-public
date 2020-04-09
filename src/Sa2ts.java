import sa.*;
import ts.Ts;

public class Sa2ts extends SaDepthFirstVisitor<Void> {
    Ts global;
    Ts local;
    int parametre = 0;

    public Sa2ts(SaNode saRoot) {
        global = new Ts();
        saRoot.accept(this);
    }

    public Ts getTableGlobale() {
        return global;
    }

    @Override
    public Void visit(SaDecTab node) {
        if (local != null) {
            local.addVar(node.getNom(), node.getTaille());
            if ((global.variables.size() >= parametre)) {
                local.addParam(node.getNom());
            }
        } else {
            global.addVar(node  .getNom(), node.getTaille());
        }
        return super.visit(node);
    }

    @Override
    public Void visit(SaDecFonc node) {
        parametre = (node.getParametres() == null) ?
                0 : node.getParametres().length();
        local = new Ts();
        global.addFct(node.getNom(), parametre, local, node);
        return super.visit(node);

    }

    @Override
    public Void visit(SaDecVar node) {
        if (local != null) {
           local.addVar(node.getNom(),1);
           if (local.variables.size() <= parametre){
              local.addParam(node.getNom());
            }
        } else {
            global.addVar(node.getNom(), 1);
        }
        return super.visit(node);
    }

    @Override
    public Void visit(SaVarSimple node) {
        return super.visit(node);
    }

    @Override
    public Void visit(SaAppel node) {
        return super.visit(node);
    }

    @Override
    public Void visit(SaVarIndicee node) {
        return super.visit(node);
    }
}
