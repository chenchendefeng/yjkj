package com.jiayi.platform.security.core.entity.part;


import com.jiayi.platform.security.core.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserPwd {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String password;
    private String username;
    @Column(name = "be_valid")
    private int beValid = 1;
    @Column(name = "be_active")
    private int beActive;

    public User copy2User() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setBeValid(this.beValid);
        user.setBeActive(this.beActive);
        return user;
    }
}
