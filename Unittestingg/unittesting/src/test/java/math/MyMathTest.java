package math;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyMathTest {

    private MyMath math = new MyMath();

    // Test cases for factorial method
    @Test
    public void testFactorialZero() {
        assertEquals("Factorial of 0 should be 1", 1, math.factorial(0));
    }

    @Test
    public void testFactorialOne() {
        assertEquals("Factorial of 1 should be 1", 1, math.factorial(1));
    }

    @Test
    public void testFactorialFive() {
        assertEquals("Factorial of 5 should be 120", 120, math.factorial(5));
    }

    @Test
    public void testFactorialTwelve() {
        assertEquals("Factorial of 12 should be 479001600", 479001600, math.factorial(12));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFactorialNegative() {
        math.factorial(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFactorialAboveTwelve() {
        math.factorial(13);
    }

    // Test cases for isPrime method
    @Test
    public void testIsPrimeTwo() {
        assertTrue("2 should be prime", math.isPrime(2));
    }

    @Test
    public void testIsPrimeThree() {
        assertTrue("3 should be prime", math.isPrime(3));
    }

    @Test
    public void testIsPrimeLargePrime() {
        assertTrue("97 should be prime", math.isPrime(97));
    }

    @Test
    public void testIsPrimeFour() {
        assertFalse("4 should not be prime", math.isPrime(4));
    }

    @Test
    public void testIsPrimeComposite() {
        assertFalse("100 should not be prime", math.isPrime(100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsPrimeOne() {
        math.isPrime(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsPrimeZero() {
        math.isPrime(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsPrimeNegative() {
        math.isPrime(-1);
    }
}