package com.autentia.tutoriales.apacheds.accounts.repositories;

import com.autentia.tutoriales.apacheds.accounts.domain.UserAccount;
import org.springframework.data.repository.Repository;

public interface UserAccountRepository extends Repository<UserAccount, Long> {
    UserAccount findByLogin(String login);
}
