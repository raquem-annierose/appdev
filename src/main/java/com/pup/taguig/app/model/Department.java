package com.pup.taguig.app.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Department {
    private int id;
    private String name;
    private String displayName;

    private List<StudentM> students;

}
