import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ivan on 24.05.2014 in 22:41.
 */
public class Field {
    private int p;
    private int n;
    private FieldElement[] fieldElements;
    private int[] polynomial;
    private int cardinality;
    private Map<Integer, Integer> degreeToNumber;
    private List<Integer> extentionDegrees;

    public Field(int p, int n, int[] polynomial) {
        this.p = p;
        this.n = n;
        this.polynomial = polynomial;
        this.cardinality = (int) Math.pow(p,n);
        fieldElements = new FieldElement[(int) Math.pow(p, n)];
        degreeToNumber = new HashMap<>();
        extentionDegrees = new ArrayList<>();
    }

    public void init () {
        for (int i = 0; i < fieldElements.length; i++) {
            fieldElements[i] = new FieldElement(i, p, n);
            fieldElements[i].setOrder(countOrder(fieldElements[i]));
        }
        degreeToNumber.put(0, 1);
        fieldElements[0].setDegree(-1);
        fieldElements[1].setDegree(0);
        for (int i = 2; i < fieldElements.length; i++) {
            if (fieldElements[i].getOrder() == cardinality-1) {
                degreeToNumber.put(1, i);
                fieldElements[i].setDegree(1);
                break;
            }
        }
        for (int i = 2; i < cardinality - 1; i++) {
            int number = getNumberByPolynomial(pow(fieldElements[degreeToNumber.get(1)], i));
            degreeToNumber.put(i, number);
            fieldElements[number].setDegree(i);
        }


        for (int i = 1; i <= n; i++) {
            if (n%i == 0)
                extentionDegrees.add(i);
        }

        for (int i = 0; i < fieldElements.length; i++) {
            countMinPolynomials(fieldElements[i]);
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
                result[i + j] = add(result[i + j], multiply(polynomial1[i], polynomial2[j]));
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

    private int [] pow(FieldElement element, int degree) {
        int [] polynomial = element.getPolynomial();
        int [] temp = element.getPolynomial();
        for (int i = 1; i < degree; i++) {
            temp = modulo(multiply(temp, polynomial), this.polynomial);
        }
        return temp;
    }

    private int add(int number1, int number2) {
        return (number1+number2)%p;
    }

    private int getNumberByPolynomial(int [] vector) {
        for (int i = 0; i < fieldElements.length; i++) {
            if (polynomialsAreEquals(fieldElements[i].getPolynomial(), vector)) {
                return i;
            }
        }
        return -1;
    }

    private boolean polynomialsAreEquals(int[] polynomial1, int[] polynomial2) {
        int length;
        if (polynomial1.length < polynomial2.length) {
            length = polynomial1.length;
            for (int i = polynomial2.length - 1; i >= polynomial.length; i--)
                if (polynomial2[i] != 0)
                    return false;
        } else {
            length = polynomial2.length;
            for (int i = polynomial1.length - 1; i >= polynomial2.length; i--)
                if (polynomial1[i] != 0)
                    return false;
        }
        for (int i = 0; i < length; i++) {
            if (polynomial1[i] != polynomial2[i])
                return false;
        }
        return true;
    }

    private  FieldElement multiply(FieldElement element1, FieldElement element2) {
        if (isZero(element1.getPolynomial()) || isZero(element2.getPolynomial()))
            return fieldElements[0];
        int resultDegree = (element1.getDegree()+element2.getDegree())%(cardinality-1);
        return fieldElements[degreeToNumber.get(resultDegree)];
    }

    private FieldElement add(FieldElement element1, FieldElement element2) {
        int[] result = add(element1.getPolynomial(), element2.getPolynomial());
        return fieldElements[getNumberByPolynomial(result)];
    }

    private int[] add(int[] polynomial1, int[] polynomial2) {
        int [] result = new int[Math.max(polynomial1.length, polynomial2.length)];
        int length;
        if (polynomial1.length < polynomial2.length) {
            length = polynomial1.length;
        } else {
            length = polynomial2.length;
        }
        for (int i = 0; i < length; i++) {
            result[i] = add(polynomial1[i], polynomial2[i]);
        }
        return result;
    }

    private FieldElement getAdditiveInverse(FieldElement element) {
        int[] result = element.getPolynomial();
        for (int i = 0; i<result.length; i++)
            subtract(result[i], p);
        int resultNumber = getNumberByPolynomial(result);
        return fieldElements[resultNumber];
    }


    private FieldElement[] multiply(FieldElement[] polynomial1, FieldElement[] polynomial2) {
        FieldElement[] result = new FieldElement[polynomial1.length + polynomial2.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = fieldElements[0];
        }
        for (int i = 0; i < polynomial1.length; i++) {
            for (int j = 0; j < polynomial2.length; j++) {
                result[i + j] = add(result[i + j], multiply(polynomial1[i], polynomial2[j]));
            }
        }
        return result;
    }

    private void countMinPolynomials(FieldElement element) {
        if (element.getMinPolynomials().size() != 0)
            return;
        FieldElement[] result;
        Map<Integer, FieldElement[]> resultMap = new HashMap<>();
        for (int degree:extentionDegrees) {
            result = multiplyAll(getConjugateElements(element,degree));
            resultMap.put(degree,result);
        }
        element.setMinPolynomials(resultMap);
    }

    public FieldElement[] multiplyAll(List<FieldElement> elements) {
        FieldElement [] result = new FieldElement[]{fieldElements[1]};
        for (FieldElement element: elements) {
            result = multiply(result, new FieldElement[]{getAdditiveInverse(element), fieldElements[1]});
        }
        return result;
    }

    private List<FieldElement> getConjugateElements(FieldElement element, int extentionDegree) {
        List<FieldElement> elements = new ArrayList<>();
        elements.add(element);
        if (isZero(element.getPolynomial()))
            return elements;
        FieldElement temp = element;
        while ((temp = fieldElements[degreeToNumber.get((temp.getDegree()*(int)Math.pow(p,extentionDegree))%(cardinality-1))]).getNumber() != element.getNumber()){
            elements.add(temp);
        }
        return elements;
    }
}