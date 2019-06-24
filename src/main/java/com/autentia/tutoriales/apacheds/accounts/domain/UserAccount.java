package com.autentia.tutoriales.apacheds.accounts.domain;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Entry(
        objectClasses = { "person", "inetOrgPerson", "top", "organizationalPerson"})
public final class UserAccount {
    @Id
    private Name id;
    private  @Attribute(name = "uid")  String login;
    private @Attribute(name = "cn") String name;
    private @Attribute(name = "mail") String email;
    private @Attribute(name = "customAttribute") String customAttribute;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomAttribute() {
        return customAttribute;
    }

    public void setCustomAttribute(String customAttribute) {
        this.customAttribute = customAttribute;
    }
}
