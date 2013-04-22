package tree;

public class LABEL extends Stm { 
  public temp.Label label;
  public LABEL(temp.Label l) {label=l;}
  public ExpList kids() {return null;}
  public Stm build(ExpList kids) {
    return this;
  }
}

