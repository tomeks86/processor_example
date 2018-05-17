package edu.annotations;

public class Main {
    public static void main(String[] args) {
        Complex number = new Complex(5, 3);
        System.out.println(number);
        System.out.println(ToStringGenerator.toString(number));

        Person stefan = new Person("Stefan", 15);
        System.out.println(ToStringGenerator.toString(stefan));
    }
}
