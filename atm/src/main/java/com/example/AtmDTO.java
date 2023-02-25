package com.example;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@Data
public class AtmDTO {

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public AtmUser getAtmUser() {
        AtmUser atmUser = new AtmUser();
        atmUser.setFirstname(this.firstname);
        atmUser.setUsername(this.username);
        atmUser.setRole(this.role);
        atmUser.setLastname(this.lastname);
        atmUser.setPhone(this.phone);
        atmUser.setEmail(this.email);
        atmUser.setPassword(this.password);
        return atmUser;
    }
}
