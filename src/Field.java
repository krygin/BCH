import java.util.Stack;

/**
 * Created by Ivan on 24.05.2014 in 22:41.
 */
public class Field {
    private int p;
    private int n;
    private FieldElement[] fieldElements;
    private int[] polynomial;

    public Field(int p, int n, int[] polynomial) {
        this.p = p;
        this.n = n;
        this.polynomial = polynomial;
        fieldElements = new FieldElement[(int) Math.pow(p, n)];
    }

    public void init () {
        for (int i = 0; i < fieldElements.length; i++) {
            fieldElements[i] = new FieldElement(i, p, n);
            fieldElements[i].setOrder(countOrder(fieldElements[i]));
        }
    }

    private int countOrder(FieldElement fieldElement) {
        int[] polynomial = fieldElement.getPolynomial();
        int[] result = polynomial.clone();
        int order = 1;
        if (isZero(polynomial)) {
            return -1;
        }
        while (!isNeutral(result)) {
            result = modulo(multiply(result, polynomial), this.polynomial);
            order++;
        }
        return order;
    }



    private boolean isNeutral(int [] polynomial) {
        for (int i = polynomial.length - 1; i >0; i--)
            if (polynomial[i] != 0)
                return false;
        if (polynomial[0] == 1)
            return true;
        return false;
    }

    private boolean isZero(int [] polynomial) {
        for (int i = 0; i < polynomial.length; i++)
            if (polynomial[i] != 0)
                return false;
        return true;
    }
    private int[] multiply(int[] polynomial1, int[] polynomial2) {
        int[] result = new int[polynomial1.length + polynomial2.length - 1];

        for (int i = 0; i < polynomial1.length; i++) {
            for (int j = 0; j < polynomial2.length; j++) {
                result[i + j] = add(result[i+j],multiply(polynomial1[i], polynomial2[j]));
            }
        }
        return result;
    }

    private int[] modulo(int[] divident, int[] divider) {

        int dividerDegree = 0;
        int dividentDegree = 0;
        for (int i = divider.length-1; i >= 0; i--)
            if (divider[i] != 0) {
                dividerDegree = i;
                break;
            }
        for (int i = divident.length - 1; i >= 0; i--)
            if (divident[i] != 0) {
                dividentDegree = i;
                break;
            }
        int [] dividentPrepared = new int[dividentDegree+1];
        int [] dividerPrepared = new int[dividerDegree + 1];
        int[] result = new int[n];

        System.arraycopy(divident, 0, dividentPrepared, 0, dividentPrepared.length);
        System.arraycopy(divider, 0, dividerPrepared, 0, dividerPrepared.length);

        if (dividentPrepared.length < dividerPrepared.length) {
            System.arraycopy(divident, 0, result, 0, result.length);
            return result;
        }


        int [] remainder = dividentPrepared.clone();
        int[] quotient = new int[remainder.length - dividerPrepared.length + 1];

        for (int i = 0; i < quotient.length; i++) {
            int coefficient = divide(remainder[remainder.length - i - 1], dividerPrepared[dividerPrepared.length-1]);
            quotient[quotient.length - i - 1] = coefficient;
            for (int j = 0; j < dividerPrepared.length; j++) {
                int element = multiply(coefficient, divider[dividerPrepared.length - j - 1]);
                remainder[remainder.length - i - j - 1] =
                        subtract(remainder[remainder.length - i - j - 1], element);
            }
        }
        System.arraycopy(remainder, 0, result, 0, result.length);
        return result;
    }
    private int divide(int divident, int divider) {
        Integer inverseElement = divider;
        while ((divider * inverseElement) % p != 1)
            inverseElement = (divider * inverseElement) % p;
        return (divident * inverseElement) % p;
    }

    private int multiply(int number1, int number2) {
        return (number1*number2)%p;
    }

    private int subtract(int minuend, int subtrahend) {
        if (subtrahend > minuend)
            return (minuend - subtrahend + p) % p;
        else
            return (minuend - subtrahend) % p;
    }

    private int add(int number1, int number2) {
        return (number1+number2)%p;
    }
}