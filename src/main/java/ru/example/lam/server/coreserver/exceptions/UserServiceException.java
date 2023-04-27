package ru.example.lam.server.coreserver.exceptions;

import lombok.Getter;
import ru.example.lam.server.entity.users.User;

import java.util.List;

public class UserServiceException extends Exception{
    @Getter
    private final User user;
    @Getter
    private final List<String> wrongRoleValues;

    public UserServiceException(User user, List<String> wrongRoleValues) {
        this.user = user;
        this.wrongRoleValues = wrongRoleValues;
    }
}
