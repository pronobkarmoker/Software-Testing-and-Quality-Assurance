package math;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import io.FileIO;

public class ArrayOperationsTest {

    private ArrayOperations arrayOperations;
    private StubFileIO fileIO;
    private StubMyMath myMath;

    @Before
    public void setUp() {
        arrayOperations = new ArrayOperations();
        fileIO = new StubFileIO();
        myMath = new StubMyMath();
    }

    @Test
    public void testFindPrimesInFileWithValidInput() {
        // Arrange
        fileIO.setNumbers(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

        // Act
        int[] result = arrayOperations.findPrimesInFile(fileIO, "validFile.txt", myMath);

        // Assert
        int[] expected = {2, 3, 5, 7};
        assertArrayEquals("Should return array of prime numbers", expected, result);
    }

    @Test
    public void testFindPrimesInFileWithEmptyFile() {
        // Arrange
        fileIO.setNumbers(new int[]{});

        // Act
        int[] result = arrayOperations.findPrimesInFile(fileIO, "emptyFile.txt", myMath);

        // Assert
        int[] expected = {};
        assertArrayEquals("Should return empty array for empty file", expected, result);
    }

    @Test
    public void testFindPrimesInFileWithNoPrimes() {
        // Arrange
        fileIO.setNumbers(new int[]{1, 4, 6, 8, 9});

        // Act
        int[] result = arrayOperations.findPrimesInFile(fileIO, "noPrimes.txt", myMath);

        // Assert
        int[] expected = {};
        assertArrayEquals("Should return empty array when no primes are found", expected, result);
    }

    @Test
    public void testFindPrimesInFileWithNegativeNumbers() {
        // Arrange
        fileIO.setNumbers(new int[]{-5, -3, -2, 0, 2, 3});

        // Act
        int[] result = arrayOperations.findPrimesInFile(fileIO, "negativeNumbers.txt", myMath);

        // Assert
        int[] expected = {2, 3};
        assertArrayEquals("Should correctly handle negative numbers and return primes", expected, result);
    }

    @Test(expected = NullPointerException.class)
    public void testFindPrimesInFileWithNullFileIO() {
        // Act
        arrayOperations.findPrimesInFile(null, "validFile.txt", myMath);
    }

    @Test(expected = NullPointerException.class)
    public void testFindPrimesInFileWithNullFilePath() {
        // Act
        arrayOperations.findPrimesInFile(fileIO, null, myMath);
    }

    @Test(expected = NullPointerException.class)
    public void testFindPrimesInFileWithNullMyMath() {
        // Arrange
        fileIO.setNumbers(new int[]{2, 3, 4});

        // Act
        arrayOperations.findPrimesInFile(fileIO, "validFile.txt", null);
    }

    @Test
    public void testFindPrimesInFileWithAllPrimes() {
        // Arrange
        fileIO.setNumbers(new int[]{2, 3, 5, 7, 11});

        // Act
        int[] result = arrayOperations.findPrimesInFile(fileIO, "allPrimes.txt", myMath);

        // Assert
        int[] expected = {2, 3, 5, 7, 11};
        assertArrayEquals("Should return all numbers when all are primes", expected, result);
    }

    // Stub implementation of FileIO
    private static class StubFileIO extends FileIO {
        private int[] numbers;

        public void setNumbers(int[] numbers) {
            this.numbers = numbers;
        }

        @Override
        public int[] readFile(String filepath) {
            return numbers != null ? numbers : new int[]{};
        }
    }

    // Stub implementation of MyMath
    private static class StubMyMath extends MyMath {
        @Override
        public boolean isPrime(int n) {
            if (n <= 1) {
                return false;
            }
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}