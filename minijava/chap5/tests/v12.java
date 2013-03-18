class Foo {										// [30] [31]
  public static void main(String[] a) {
    System.out.println(new Bar().f(this));
  }
}
class Bar {
  int f;

  public int f(int d) {
    int b;

    return b+5;
  }

  public int g(int a) {
	return a+12;
  }
}

class Ber {
  int f;

  public int f(int d) {
    int b;
    boolean x;
	Bar bb;    
	
	b = xyz.f(7);

    return b+1;
  }

  public int g(int a) {
	return a+12;
  }
}
