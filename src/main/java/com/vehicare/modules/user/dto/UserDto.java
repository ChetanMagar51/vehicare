package com.vehicare.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class UserDto {
	
	 private Long id;
	    private String firstName;
	    private String lastName;
	    private String phone;
	    private String address;
	    private String email;

}
