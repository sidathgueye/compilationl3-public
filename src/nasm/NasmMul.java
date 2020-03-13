package nasm;

public class NasmMul extends NasmInst {

    public NasmMul(NasmOperand label, NasmOperand source, String comment){
	source.use = true;
	this.label = label;
	this.source = source;
	this.comment = comment;
    }

    public NasmMul(NasmOperand label, NasmOperand destination, NasmOperand source, String comment){
	destination.use = true;
	destination.def = true;
	source.use = true;
	this.label = label;
	this.destination = destination;
	this.source = source;
	this.comment = comment;
    }

    public <T> T accept(NasmVisitor <T> visitor) {
        return visitor.visit(this);
    }

    /*    public String toString(){
	return super.formatInst(this.label, "imul", this.source, null, this.comment);
	}*/

    public String toString(){
	return super.formatInst(this.label, "imul", this.destination, this.source, this.comment);
    }

}
