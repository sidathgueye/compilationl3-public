package nasm;

public class NasmMov extends NasmInst {
    
    public NasmMov(NasmOperand label, NasmOperand destination, NasmOperand source, String comment){
	destination.def = true;
	destination.use = false;
	source.use = true;
	source.def = false;
	this.label = label;
	this.destination = destination;
	this.source = source;
	this.comment = comment;
    }

    public <T> T accept(NasmVisitor <T> visitor) {
        return visitor.visit(this);
    }

    public String toString(){
	return super.formatInst(this.label, "mov", this.destination, this.source, this.comment);
    }
    
}
