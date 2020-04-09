package nasm;

import c3a.*;
import ts.Ts;
import ts.TsItemFct;

import java.util.HashMap;
import java.util.Map;

public class C3a2nasm implements C3aVisitor<NasmOperand> {
    private C3a c3a;
    private Nasm nasm;
    private Ts table;
    private TsItemFct currentFct;

    private Map<C3aOperand, NasmOperand> map0;
    private Map<C3aInstFBegin, NasmOperand> map1;


    public C3a2nasm(C3a c3a, Ts table) {

        this.c3a = c3a;
        this.table = table;
        nasm = new Nasm(table);
        map0 = new HashMap<>();
        map1 = new HashMap<>();
        NasmLabel main = null;
        NasmRegister eax = nasm.newRegister();
        NasmRegister ebx = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);
        ebx.colorRegister(Nasm.REG_EBX);


        for (var instruct : c3a.listeInst) {
            if (instruct.label != null) {
                map0.put(instruct.label, new NasmLabel(instruct.label.toString()));
            }
            if (instruct instanceof C3aInstFBegin) {
                if (((C3aInstFBegin) instruct).val.identif.equals("main")) {
                    map1.put((C3aInstFBegin) instruct, main = new NasmLabel(((C3aInstFBegin) instruct).val.identif));

                } else {
                    map1.put((C3aInstFBegin) instruct, new NasmLabel(((C3aInstFBegin) instruct).val.identif));
                }
            }
        }
        nasm.ajouteInst(new NasmCall(null, main, ""));
        nasm.ajouteInst(new NasmMov(null, ebx, new NasmConstant(0), ""));
        nasm.ajouteInst(new NasmMov(null, eax, new NasmConstant(1), ""));

        nasm.ajouteInst(new NasmInt(null, ""));

        for (var variable : c3a.listeInst) {
            variable.accept(this);
        }
    }

    public Nasm getNasm() {
        return nasm;
    }

    @Override
    public NasmOperand visit(C3aInstAdd inst) {
        NasmOperand label = (inst.label != null) ? map1.get(inst.label) : null;
        map1.get(inst.label);
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmAdd(null, dest, oper2, ""));

        return dest;
    }

    @Override
    public NasmOperand visit(C3aInstMult inst) {
        NasmOperand label = (inst.label != null) ? map1.get(inst.label) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmMul(null, dest, oper2, ""));

        return dest;
    }

    @Override
    public NasmOperand visit(C3aInstSub inst) {
        NasmOperand label = (inst.label != null) ? map1.get(inst.label) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        nasm.ajouteInst(new NasmMov(label, dest, oper1, ""));
        nasm.ajouteInst(new NasmSub(null, dest, oper2, ""));

        return dest;
    }

    @Override
    public NasmOperand visit(C3aInstDiv inst) {
        NasmOperand label = (inst.label != null) ? map1.get(inst.label) : null;
        NasmOperand oper1 = inst.op1.accept(this);
        NasmOperand oper2 = inst.op2.accept(this);
        NasmOperand dest = inst.result.accept(this);
        NasmRegister eax = nasm.newRegister();
        NasmRegister reg = nasm.newRegister();
        eax.colorRegister(Nasm.REG_EAX);
        nasm.ajouteInst(new NasmMov(label, eax, oper1, ""));
        nasm.ajouteInst(new NasmMov(null, reg, oper2, ""));
        nasm.ajouteInst(new NasmDiv(null, reg, ""));
        nasm.ajouteInst(new NasmMov(null, dest, eax, ""));

        return dest;
    }

    @Override
    public NasmOperand visit(C3aConstant oper) {
        return map0.computeIfAbsent(oper, k -> new NasmConstant(oper.val));

    }

    @Override
    public NasmOperand visit(C3aLabel oper) {
        return map0.computeIfAbsent(oper, k -> new NasmLabel(oper.toString()));
    }

    @Override
    public NasmOperand visit(C3aTemp oper) {
        return map0.computeIfAbsent(oper, K -> nasm.newRegister());
    }

    @Override
    public NasmOperand visit(C3aVar oper) {
        // TODO avec les conditions à vérifier à revoir avec Alex.
        return null;
    }

    @Override
    public NasmOperand visit(C3aFunction oper) {
        return map0.computeIfAbsent(oper, k -> new NasmLabel(oper.val.identif));
    }

    @Override
    public NasmOperand visit(C3aInstCall inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstFBegin inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInst inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfLess inst) {
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstRead inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstAffect inst) {
        return null;
    }


    @Override
    public NasmOperand visit(C3aInstFEnd inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfEqual inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJumpIfNotEqual inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstJump inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstParam inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstReturn inst) {
        return null;
    }

    @Override
    public NasmOperand visit(C3aInstWrite inst) {
        return null;
    }


}
