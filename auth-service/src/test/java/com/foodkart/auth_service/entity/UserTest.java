package com.foodkart.auth_service.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void builder_createsUser() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role("CUSTOMER")
                .active(true)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("CUSTOMER", user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void noArgsConstructor_createsEmptyUser() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertFalse(user.isActive());
    }

    @Test
    void allArgsConstructor_createsUser() {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "Test User", "test@example.com", "encodedPassword", "CUSTOMER", true, now);

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("CUSTOMER", user.getRole());
        assertTrue(user.isActive());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void setId_setsId() {
        User user = new User();
        user.setId(5L);
        assertEquals(5L, user.getId());
    }

    @Test
    void setName_setsName() {
        User user = new User();
        user.setName("New Name");
        assertEquals("New Name", user.getName());
    }

    @Test
    void setEmail_setsEmail() {
        User user = new User();
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void setPassword_setsPassword() {
        User user = new User();
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void setRole_setsRole() {
        User user = new User();
        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void setActive_setsActive() {
        User user = new User();
        user.setActive(true);
        assertTrue(user.isActive());
        
        user.setActive(false);
        assertFalse(user.isActive());
    }

    @Test
    void setCreatedAt_setsCreatedAt() {
        User user = new User();
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void getId_returnsId() {
        User user = User.builder().id(10L).build();
        assertEquals(10L, user.getId());
    }

    @Test
    void getName_returnsName() {
        User user = User.builder().name("User Name").build();
        assertEquals("User Name", user.getName());
    }

    @Test
    void getEmail_returnsEmail() {
        User user = User.builder().email("email@example.com").build();
        assertEquals("email@example.com", user.getEmail());
    }

    @Test
    void getPassword_returnsPassword() {
        User user = User.builder().password("mypass").build();
        assertEquals("mypass", user.getPassword());
    }

    @Test
    void getRole_returnsRole() {
        User user = User.builder().role("ADMIN").build();
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void isActive_returnsActive() {
        User user = User.builder().active(true).build();
        assertTrue(user.isActive());
        
        User inactiveUser = User.builder().active(false).build();
        assertFalse(inactiveUser.isActive());
    }

    @Test
    void getCreatedAt_returnsCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().createdAt(now).build();
        assertEquals(now, user.getCreatedAt());
    }
}
