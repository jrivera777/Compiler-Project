//I should be ignored*/
//Me too.

public static void main(String[] args)
{
    int[] arr = new int[5];
    int counter = 0;
    
    while(counter < arr.length)
    {
	System.out.println(arr[counter]);
	boolean test = counter * 2 < 20;

	if(test)
	    System.out.println(counter);
	else
	    counter = counter + 1;
    }
    /*This is a
     *multi-line comment*/
}

public static boolean alwaysReturnTrue(boolean val) //worthless function
{
    if(true && val)
	return true;
    return true;
}