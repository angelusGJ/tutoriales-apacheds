package com.autentia.tutoriales.apacheds.accounts.repositories;

import com.autentia.tutoriales.apacheds.accounts.config.LDAPConfiguration;
import com.autentia.tutoriales.apacheds.accounts.domain.UserAccount;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.CreateLdapServerRule;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.ldap.DataLdapTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = LDAPConfiguration.class)
@CreateLdapServer(transports = {@CreateTransport(protocol = "LDAP", port = 18888)})
@CreateDS(allowAnonAccess = true, name = "Autentia", partitions = {
        @CreatePartition(name = "Autentia", suffix = "o=autentia")})
@ApplyLdifFiles(value = {"autentia-identity-repository.ldif"})
@EnableLdapRepositories
public class UserAccountRepositoryIntegrationTest {
    @ClassRule
    public static CreateLdapServerRule serverRule = new CreateLdapServerRule();

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    public void shouldFindUserAccounyByLogin(){
        final String login = "jmsanchez";
        final UserAccount userAccount = userAccountRepository.findByLogin(login);

        assertThat(userAccount).isNotNull();
        assertThat(userAccount.getName()).isEqualTo("Jose Manuel Sánchez");
    }

    @Test
    public void shouldReturnCustomAttribute() {
        final String login = "jmsanchez";
        final UserAccount userAccount = userAccountRepository.findByLogin(login);

        assertThat(userAccount).isNotNull();
        assertThat(userAccount.getCustomAttribute()).isEqualTo("dummy");
    }
}
