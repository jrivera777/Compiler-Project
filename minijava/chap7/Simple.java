class Test {
    public static void main(String[] args) {
	System.out.println(new C().foo(1)[3]);
    }
}

class C {
    int i;
    boolean b;
    int [] arr;

    public int[] foo(int i)
    {
	i = i * 16;
	return arr;
    }

    public int bar(C x) {
	i = x.bar(this);
	return i;
    }
}

