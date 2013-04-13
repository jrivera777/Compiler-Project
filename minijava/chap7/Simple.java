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
	arr = new int[5]; //return CALL object? Or MEM of the CALL object?
//	b = true && true && true; //seems broken
	arr[1] = i * 4;

	return arr;
    }
}

