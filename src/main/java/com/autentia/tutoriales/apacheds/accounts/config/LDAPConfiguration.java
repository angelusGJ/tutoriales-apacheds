package com.autentia.tutoriales.apacheds.accounts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableConfigurationProperties
public class LDAPConfiguration {
    @Bean
    @ConfigurationProperties("ldap")
    public LdapContextSource contextSource(){
        return new LdapContextSource();
    }

    @Bean
    public LdapTemplate ldapTemplate(){
        return new LdapTemplate(contextSource());
    }
}
