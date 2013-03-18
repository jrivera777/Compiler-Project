class Foo {
  public static void main(String[] a) {
    System.out.println(new Bar().f(this));
  }
}

class Bar {

  int c;
  boolean c;

  public int f(Foo a) {
    int [] b;
    boolean x;
    boolean a;

    b = new int[5];
    x = b[4];
    b[true] = d;
    b[b[2]] = this.f(c, x, a);
    return b+7;
  }
}

class Bar { }
