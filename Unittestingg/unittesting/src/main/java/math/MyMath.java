package math;

/**
 * The MyMath provides simple methods such as computing a factorial or finding
 * whether an integer is prime
 *
 * @author pandeliskirpoglou
 * @version 1.0
 * @since 2020-04-15
 */

public class MyMath {

	/**
	 * Gets one integer and returns it's factorial.
	 * 
	 * @param n the number of which we want the factorial
	 * @return fact the factorial of the number n
	 * @exception IllegalArgumentException when inputs n < 0 and n > 12
	 */

	public int factorial(int n) {
		int fact = 1;
		if (n < 0 || n > 12) {
			throw new IllegalArgumentException("number should be 0 or above and 12 or below");
		} else {
			for (int i = 1; i <= n; i++) {
				fact = fact * i;
			}
		}

		return fact;
	}

	/**
	 * Gets one integer and returns true if it is prime and false if it is not.
	 * 
	 * @param n the number we are trying to find out whether it is prime or not
	 * @return isPrimeNumber true if n is prime | false if n is not prime
	 * @exception IllegalArgumentException when inputs n < 2
	 */

	public boolean isPrime(int n) {
		boolean isPrimeNumber = true;
		if (n < 2) {
			throw new IllegalArgumentException("No prime numbers below 2");
		} else {
			for (int i = 2; i <= n / 2; ++i) {  // Checking to n/2 for complexity
				if (n % i == 0) {
					isPrimeNumber = false;
					break;
				}
			}
		}

		return isPrimeNumber;
	}

}
