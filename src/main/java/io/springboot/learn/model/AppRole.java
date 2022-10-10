package io.springboot.learn.model;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Data
public class AppRole {

    @Id
    private Long id;
    private String roleName;
}
