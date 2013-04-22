package tree;

public class NAME extends Exp {
  public temp.Label label;
  public NAME(temp.Label l) {label=l;}
  public ExpList kids() {return null;}
  public Exp build(ExpList kids) {return this;}
}

