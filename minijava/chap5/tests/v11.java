class Foo {										// [27] [28] [29] 
  public static void main(String[] a) {
    System.out.println(new Bar().f(3));
    
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
	
	b = x.f(7);
	b = bb.x(4);
	b = bb.f(x);

    return b+1;
  }

  public int g(int a) {
	return a+12;
  }
}
