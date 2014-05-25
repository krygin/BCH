import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by Ivan on 25.05.2014 in 0:52.
 */
public class FieldElement {
    private int p;
    private int n;
    private int number;
    private int [] polynomial;
    private int degree;
    private int order;
    private int[][] minPolynomials;
    private boolean isPrimitive;

    public FieldElement (int number, int p, int n) {
        this.number = number;
        this.p = p;
        this.n = n;
        this.polynomial = numberToPolynomial(number);
    }
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int[] getPolynomial() {
        return polynomial;
    }

    public void setPolynomial(int[] polynomial) {
        this.polynomial = polynomial;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
        if (order == Math.pow(p, n) - 1)
            isPrimitive = true;
    }
    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public int[][] getMinPolynomials() {
        return minPolynomials;
    }

    public void setMinPolynomials(int[][] minPolynomials) {
        this.minPolynomials = minPolynomials;
    }



    private int[] numberToPolynomial(int number) {
        int [] result = new int[n];
        Queue<Integer> remains = new LinkedList<>();
        while (number > 0 ) {
            remains.add(number%p);
            number/=p;
        }
        for (int i = 0; i < result.length && !remains.isEmpty(); i++)
            result[i] = remains.remove();
        return result;
    }
}
