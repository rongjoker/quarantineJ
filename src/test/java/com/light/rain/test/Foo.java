package com.light.rain.test;

public class Foo {

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public Foo setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Foo setAge(Integer age) {
        this.age = age;
        return this;
    }
}
