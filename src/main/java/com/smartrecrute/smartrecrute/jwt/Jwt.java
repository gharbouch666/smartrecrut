package com.smartrecrute.smartrecrute.jwt;

import com.smartrecrute.smartrecrute.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
public class Jwt implements Serializable {

    private static final long SerialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private boolean expired = false;
    private boolean desactivated = false;
    @Column(length = 2000)
    private String value;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private Utilisateur user;

    public Jwt() {
    }

    public Jwt(long id, boolean expired, boolean desactivated, String value, Utilisateur user) {
        this.id = id;
        this.expired = expired;
        this.desactivated = desactivated;
        this.value = value;
        this.user = user;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
    public boolean isDesactivated() { return desactivated; }
    public void setDesactivated(boolean desactivated) { this.desactivated = desactivated; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public Utilisateur getUser() { return user; }
    public void setUser(Utilisateur user) { this.user = user; }

    public static JwtBuilder builder() {
        return new JwtBuilder();
    }

    public static class JwtBuilder {
        private long id;
        private boolean expired;
        private boolean desactivated;
        private String value;
        private Utilisateur user;

        public JwtBuilder id(long id) { this.id = id; return this; }
        public JwtBuilder expired(boolean expired) { this.expired = expired; return this; }
        public JwtBuilder desactivated(boolean desactivated) { this.desactivated = desactivated; return this; }
        public JwtBuilder value(String value) { this.value = value; return this; }
        public JwtBuilder user(Utilisateur user) { this.user = user; return this; }
        public Jwt build() { return new Jwt(id, expired, desactivated, value, user); }
    }
}