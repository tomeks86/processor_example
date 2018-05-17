package edu.annotations;

@ToString
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @ToString
    public String getName() {
        return name;
    }

    @ToString
    public int getAge() {
        return age;
    }
}
