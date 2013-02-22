// Given an integer n > 0, set n to 3n+1 if n is odd, and n/2 if n is even.
// The Collatz conjecture states that this process eventually reaches 1,
// for any n.  For example, starting with 3, we reach 1 after 7 steps:
//   3 -> 10 -> 5 -> 16 -> 8 -> 4 -> 2 -> 1
// In this program, we calculate the maximum number of steps needed to
// reach 1, over all starting values from 1 to 77670.
// (We stop there because if we start with 77671 we eventually reach
// 1570824736, and this causes half(n) to overflow a 32-bit integer.)
// By the way, the maximum number of steps should be 350 (taken by 77031).

class Benchmark {
  public static void main(String [] args) {
    System.out.println(new Collatz().countMaxSteps(77670));
  }
}

class Collatz {
  // Given a natural number n, return n/2 if n is even and -1 if n is odd.
  // This is hard to do efficiently in MiniJava, since we don't have division!
  public int half(int n) {
    int answer; int pow2;

    answer = 0;
    while (1 < n) {
      pow2 = 1;
      while (!(n < 4*pow2))
        pow2 = 2*pow2;
      answer = answer + pow2;
      n = n - 2*pow2;
    }
    if (0 < n)			// n was odd
      answer = 0 - 1;
    else { }
    return answer;
  }

  public int countMaxSteps(int limit) {
    int max; int start; int n; int count; int half;

    max = 0;
    start = 0;
    while (start < limit) {
      start = start + 1;
      n = start;
      count = 0;
      while (1 < n) {
        half = this.half(n);
        if (half < 0)		// n is odd
	  n = 3*n + 1;
        else			// n is even
	  n = half;
        count = count + 1;
      }
      if (max < count)		// a new record!
	max = count;
      else { }
    }
    return max;
  }
}
