// Some tricky cases involving precedence.

class Preced {
  public static void main (String [] a) {
    System.out.println(new C().foo(15, true));
  }
}

class C {
  public int foo(int n, boolean b) {
    int i;
    int[] a;

    i = 3 + a [031];
    a[i] = 4 + a.length;
    return ! b.g(true);
  }
}
