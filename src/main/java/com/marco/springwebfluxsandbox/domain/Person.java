package com.marco.springwebfluxsandbox.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Person {

    private String name;

    private String lastName;

    private Integer age;

}
