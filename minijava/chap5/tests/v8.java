class Foo {										// [19] [20] [21] [22] [23]
  public static void main(String[] a) {
    System.out.println(new Bar().f(3));
  }
}

class Bar {
  int f;

  public int f(int d) {
    int a;
    int b;
    int c;
    int[] e;
    boolean x;
    boolean y;
    boolean z;

    x = y&&b;
    x = y<3;
    a = z+c;
    a = y-c;
    z = a-c;
    z = x*c;

    while (b<4) {System.out.println(b);}

    return b+5;
  }

  public int g(int a) {
	return a+12;
  }
}

