package dto_response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

 //     To signup ke baad client ko hum ye information dena chahte hain


    private String id ;
    private String username ;
    private String email ;
    private String role ;

}
