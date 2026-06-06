package com.pup.taguig.app.dto;



import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
// walang parameter na binabalik
public class DepartmentResponseDTO {


    private int id;
    private String name;
    private String displayName;
    
    private List<StudentResponseDTO> students;
 


}