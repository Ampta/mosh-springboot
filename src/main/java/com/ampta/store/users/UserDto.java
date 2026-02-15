package com.ampta.store.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserDto {

    private Long id;
    private String name;
    private String email;
}
