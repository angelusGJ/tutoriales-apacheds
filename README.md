# ApacheDS: test de integración utilizando un esquema propio 

## Introducción

Hace tiempo nuestro compañero [Jose Manuel](https://www.adictosaltrabajo.com/author/jose-manuel-sanchez/) publicó un [tutorial](https://www.adictosaltrabajo.com/2013/12/26/apache-d-s-embedded-ldap-server/) sobre como realizar test de integración accediendo a un LDAP usando el servidor embebido ApacheDS. En ese tutorial se hacía uso de esquemas estándar que incluye cualquier LDAP, como son: **core.schema**, **inetorgperson.schema**, etc. 

Aunque lo normal es utilizar esquemas estándar, puede ocurrir que nos encontremos empresas que personalizan el LDAP añadiendo esquemas propios.

En este tutorial vamos un paso más y os enseñaremos a cargar esos esquemas propios y probar la funcionalidad que hayamos desarrollado.

## Entorno.
El tutorial está escrito usando el siguiente entorno:

- Hardware: Portátil MacBook Pro 15′ (2.6 GHz Intel Core i7, 32GB DDR4).
- Sistema Operativo: Mac OS Mojave 10.4.5
- Oracle Java: 11.0.3
- Maven: 3.6.0
- Spring Boot 2.1.3.RELEASE
- Spring Data LDAP: 2.1.3.RELEASE


## Esquema propio

En el tutorial de Jose Manuel se uso un LDAP tradicional creando un conjunto de usuarios con los tipos: **organizationalPerson**, **person** e **inetOrgPerson**. Apoyándonos en dicho ejemplo, imaginaros que después de un tiempo se necesita añadir un atributo nuevo (customAttribute) a los usuarios. Este atributo no existe en ninguno de los tipos estándar de LDAP y tenemos que crear uno propio. Entonces hacemos nuestro desarrollo y queremos comprobar con un test de integración que todo funcióna correctamente. 

Lo primero que tenemos que hacer es definir el esquema son sus objectclass y attributetypes que lo van a componer. A continuación os enseñarmos cada uno de los ficheros.

El primero es "**cn=custom.ldif**", donde definimos el nombre del nuestro esquema "custom" y las dependencias con otros esquemas.

```
version: 1
dn: cn=custom,ou=schema
cn: custom
m-disabled: FALSE
objectclass: metaSchema
objectclass: top
m-dependencies: system
m-dependencies: core
m-dependencies: cosine
m-dependencies: inetorgperson
```

El segundo "**cn=custom/ou=objectclasses.ldif**" definimos la raíz donde estarán todas nuestros tipos objectClass:

```
version: 1
dn: ou=objectClasses,cn=custom,ou=schema
ou: objectclasses
objectclass: organizationalUnit
objectclass: top
```

De la misma forma para los tipos de atributos. Creamos el fichero "**cn=custom/ou=attributetypes.ldif**":

```
version: 1
dn: ou=attributeTypes,cn=custom,ou=schema
ou: attributetypes
objectclass: organizationalUnit
objectclass: top
```

Ahora creamos el fichero "**cn=custom/ou=objectclasses/m-oid=1.3.6.1.4.1.42.2.27.32.1.ldif**" con el objectClass "customPerson" al que asociaremos el atributo "customAttribute" más tarde.
Destacar aquí, el tipo de objeto que estamos creando:  de tipo estructural, hijo de inetOrgPerson y que con el atributo "customAttribute". 

```
version: 1
dn: m-oid=1.3.6.1.4.1.42.2.27.32.1,ou=objectClasses,cn=custom,ou=schema
m-oid: 1.3.6.1.4.1.42.2.27.32.1
m-obsolete: FALSE
m-supobjectclass: inetOrgPerson
m-description: -
objectclass: metaObjectClass
objectclass: metaTop
objectclass: top
m-name: customPerson
m-typeobjectclass: STRUCTURAL
m-may: customAttribute
m-equality: objectIdentifierMatch
```

Y por último creamos el fichero "**cn=custom/ou=attributetypes/m-oid=1.3.6.1.4.1.42.2.27.32.1.1.ldif**" donde definimos el atributo "customAttribute", este attributo se ha configurado para que: no puede ser modificado por el usuario (m-nousermodification: FALE), no permite colecciones (m-collective: FALSE) y es de tipo string (m-syntax: 1.3.6.1.4.1.1466.115.121.1.15).

```
version: 1
dn: m-oid=1.3.6.1.4.1.42.2.27.32.1.1,ou=attributeTypes,cn=custom,ou=schema
m-collective: FALSE
m-singlevalue: TRUE
m-oid: 1.3.6.1.4.1.42.2.27.32.1.1
m-obsolete: FALSE
m-description: Custom Attribute
m-nousermodification: FALSE
objectclass: metaAttributeType
objectclass: metaTop
objectclass: top
m-syntax: 1.3.6.1.4.1.1466.115.121.1.15
m-usage: USER_APPLICATIONS
m-name: customAttribute
```

Y ya sólo nos queda modificar el fichero "**autentia-identity-repository.ldif**" usado en nuestros test e incluir el nuevo campo:

```
version: 1

dn: o=autentia
changetype: add
objectClass: extensibleObject
objectClass: organization
objectClass: top
description: autentia

dn: ou=users,o=autentia
changetype: add
objectClass: extensibleObject
objectClass: organizationalUnit
objectClass: top
ou: users

dn: ou=groups,o=autentia
changetype: add
objectClass: extensibleObject
objectClass: organizationalUnit
objectClass: top
ou: groups

dn: cn=administrativos,ou=groups,o=autentia
changetype: add
objectClass: groupOfUniqueNames
objectClass: top
cn: administrativos
uniqueMember: cn=jmsanchez,ou=users,o=autentia
uniqueMember: cn=psanchez,ou=users,o=autentia

dn: cn=tramitadores,ou=groups,o=autentia
changetype: add
objectClass: groupOfUniqueNames
objectClass: top
cn: tramitadores
uniqueMember: cn=ablanco,ou=users,o=autentia
uniqueMember: cn=msanchez,ou=users,o=autentia

dn: cn=admin,ou=groups,o=autentia
changetype: add
objectClass: groupOfUniqueNames
objectClass: top
cn: admin
uniqueMember: cn=administrador,ou=users,o=autentia

dn: cn=jmsanchez,ou=users,o=autentia
changetype: add
objectClass: organizationalPerson
objectClass: person
objectClass: inetOrgPerson
objectClass: customPerson
objectClass: top
cn: Jose Manuel Sánchez
sn: jmsanchez
uid: jmsanchez
mail: jmsanchez@autentia.com
customAttribute: dummy
userPassword:: cGFzcw==

dn: cn=psanchez,ou=users,o=autentia
changetype: add
objectClass: organizationalPerson
objectClass: person
objectClass: inetOrgPerson
objectClass: top
cn: Pablo Sánchez
sn: psanchez
uid: psanchez
mail: psanchez@autentia.com
customAttribute: another dummy
userPassword:: cGFzcw==

dn: cn=msanchez,ou=users,o=autentia
changetype: add
objectClass: organizationalPerson
objectClass: person
objectClass: inetOrgPerson
objectClass: top
cn: Mario Sánchez
sn: msanchez
uid: msanchez
mail: msanchez@autentia.com
customAttribute: second dummy
userPassword:: cGFzcw==

dn: cn=ablanco,ou=users,o=autentia
changetype: add
objectClass: organizationalPerson
objectClass: person
objectClass: inetOrgPerson
objectClass: top
cn: Alfonso Blanco
sn: ablanco
uid: ablanco
mail: ablanco@autentia.com
customAttribute: thrid dummy
userPassword:: cGFzcw==

dn: cn=administrador,ou=users,o=autentia
changetype: add
objectClass: organizationalPerson
objectClass: person
objectClass: inetOrgPerson
objectClass: top
cn: admin
sn: admin
uid: administrador
userPassword:: cGFzcw==
```

## Test de integración

Ahora creamos nuestro test de integración, no vamos a explicar cada una de las anotaciones que vienen con la librería de ApacheDS ya que fueron explicadas en el anterior tutorial.

```java
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
```

Si ejecutásemos nuestro test de integración veremos que nos dá un error de este tipo:: 

```log
Caused by: org.apache.directory.api.ldap.model.exception.LdapException: ERR_04269 ATTRIBUTE_TYPE for OID customattribute does not exist!
	at org.apache.directory.api.ldap.model.schema.registries.DefaultSchemaObjectRegistry.lookup(DefaultSchemaObjectRegistry.java:235)
	at org.apache.directory.api.ldap.model.schema.registries.DefaultAttributeTypeRegistry.lookup(DefaultAttributeTypeRegistry.java:300)
	... 15 more
```

Es debido a que nuestro LDAP no tiene cargado todavía nuestro esquema. Para que ApachaDS cargue nuestro esquema debemos añadir al classpath el fichero "**apacheds-schema.index**" dentro del directorio "**META-INF**".

Cuando ApacheDS localiza este recurso  carga cada uno de los ficheros que vengan definidos en él. Si tomamos como referencia los ficheros generados en el apartado anterior, el fichero "apacheds-schema.index" quedaría así:

```
schema/ou=schema/cn=custom.ldif
schema/ou=schema/cn=custom/ou=attributetypes.ldif
schema/ou=schema/cn=custom/ou=objectclasses.ldif
schema/ou=schema/cn=custom/ou=objectclasses/m-oid=1.3.6.1.4.1.42.2.27.32.1.ldif
schema/ou=schema/cn=custom/ou=attributetypes/m-oid=1.3.6.1.4.1.42.2.27.32.1.1.ldif 
```

Ahora si volvemos a ejecutar nuestro test de integración estará en verde.


## Referencias
- [https://www.adictosaltrabajo.com/2013/12/26/apache-d-s-embedded-ldap-server/](https://www.adictosaltrabajo.com/2013/12/26/apache-d-s-embedded-ldap-server/)
- [https://github.com/angelusGJ/tutoriales-apacheds](https://github.com/angelusGJ/tutoriales-apacheds)

## Conclusiones

Como habéis podido comprobar es muy sencillo con ApacheDS realizar test de integración de funcionalidades que acceden a un LDAP incluso si éste tiene esquema propios. 
