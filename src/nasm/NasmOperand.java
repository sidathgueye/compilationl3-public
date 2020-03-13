package nasm;

public abstract class NasmOperand{
    public boolean use = false;
    public boolean def = false;

    public boolean isGeneralRegister(){
	return false;
    }
}
