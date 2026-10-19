package com.foodkart.auth_service.security;

import com.foodkart.auth_service.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    private User user;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role("CUSTOMER")
                .active(true)
                .build();
        
        userPrincipal = new UserPrincipal(user);
    }

    @Test
    void getAuthorities_returnsCorrectRole() {
        Collection<?> authorities = userPrincipal.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.toString().equals("ROLE_CUSTOMER")));
    }

    @Test
    void getPassword_returnsEncodedPassword() {
        String password = userPrincipal.getPassword();

        assertEquals("encodedPassword", password);
    }

    @Test
    void getUsername_returnsEmail() {
        String username = userPrincipal.getUsername();

        assertEquals("test@example.com", username);
    }

    @Test
    void isAccountNonExpired_returnsTrue() {
        assertTrue(userPrincipal.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_returnsTrue() {
        assertTrue(userPrincipal.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_returnsTrue() {
        assertTrue(userPrincipal.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_activeUser_returnsTrue() {
        assertTrue(userPrincipal.isEnabled());
    }

    @Test
    void isEnabled_inactiveUser_returnsFalse() {
        user.setActive(false);
        UserPrincipal inactivePrincipal = new UserPrincipal(user);

        assertFalse(inactivePrincipal.isEnabled());
    }

    @Test
    void getAuthorities_adminRole() {
        user.setRole("ADMIN");
        UserPrincipal adminPrincipal = new UserPrincipal(user);

        Collection<?> authorities = adminPrincipal.getAuthorities();

        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.toString().equals("ROLE_ADMIN")));
    }

    @Test
    void getAuthorities_restaurantOwnerRole() {
        user.setRole("RESTAURANT_OWNER");
        UserPrincipal ownerPrincipal = new UserPrincipal(user);

        Collection<?> authorities = ownerPrincipal.getAuthorities();

        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.toString().equals("ROLE_RESTAURANT_OWNER")));
    }

    @Test
    void getUser_returnsOriginalUser() {
        User retrievedUser = userPrincipal.getUser();

        assertEquals(user, retrievedUser);
        assertEquals("test@example.com", retrievedUser.getEmail());
        assertEquals("Test User", retrievedUser.getName());
    }
}
