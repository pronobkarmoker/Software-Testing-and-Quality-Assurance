package math;

import static org.junit.Assert.*;

public class ArithmeticOperationsTest {

    private final ArithmeticOperations calculator = new ArithmeticOperations();

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void testDivide() {
        // Valid division
        assertEquals(5, calculator.divide(10, 2), 0.00001);

        // Division by zero
        try {
            calculator.divide(10, 0);
            fail("Expected ArithmeticException for division by zero");
        } catch (ArithmeticException e) {
            assertEquals("Cannot divide with zero", e.getMessage());
        }
    }

    @org.junit.Test
    public void testMultiply() {
        // Valid multiplication
        assertEquals(45, calculator.multiply(5, 9));

        // Negative number check
        try {
            calculator.multiply(-5, 9);
            fail("Expected IllegalArgumentException for negative number");
        } catch (IllegalArgumentException e) {
            assertEquals("x & y should be >= 0", e.getMessage());
        }

        // Overflow check
        try {
            calculator.multiply(Integer.MAX_VALUE, 2);
            fail("Expected IllegalArgumentException for overflow");
        } catch (IllegalArgumentException e) {
            assertEquals("The product does not fit in an Integer variable", e.getMessage());
        }
    }

    @org.junit.Test
    public void testAdd() {
        // Valid addition
        assertEquals(10, calculator.add(4, 6));

        // Overflow check
        try {
            calculator.add(Integer.MAX_VALUE, 1);
            fail("Expected IllegalArgumentException for overflow");
        } catch (IllegalArgumentException e) {
            assertEquals("The result does not fit in an Integer variable", e.getMessage());
        }

        // Underflow check
        try {
            calculator.add(Integer.MIN_VALUE, -1);
            fail("Expected IllegalArgumentException for underflow");
        } catch (IllegalArgumentException e) {
            assertEquals("The result does not fit in an Integer variable", e.getMessage());
        }
    }
}
