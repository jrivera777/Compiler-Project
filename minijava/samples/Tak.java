// A benchmark using the Takeuchi function, which makes a huge number
// of recursive calls.
// Under javac, this program takes about 38 seconds on goliath.

class Tak {
  public static void main(String[] a){
    System.out.println(new Slow().slow(7));	// Should print 21.
  }
}

class Slow {
  public int slow(int n) {
    return this.tak(3*n, 2*n, n);
  }

  public int tak(int x, int y, int z) {
    int result;

    if (!(y < x))	// (x <= y)
      result = y;
    else
      result = this.tak(this.tak(x-1, y, z),
			this.tak(y-1, z, x),
			this.tak(z-1, x, y));

    return result;
  }
}

