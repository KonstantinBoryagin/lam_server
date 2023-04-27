package ru.example.lam.server.service.users;

import java.util.List;
import java.util.Set;

import ru.example.lam.server.coreserver.exceptions.LamServerException;
import ru.example.lam.server.coreserver.exceptions.UserServiceException;
import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.entity.users.User;

public interface IUsersService {
    List<User> findAllWithPredicate(UserDTO userDTO, List<String> sortList, String direction);
    Set<User> getUsersByRoles(List<String> roleList);
    User getUserById(Long id) throws LamServerException;
    User addUser(UserDTO userDTO) throws LamServerException, UserServiceException;
    boolean deleteUserById(Long id);
    User editUserById(Long id, UserDTO userDTO) throws LamServerException, UserServiceException;
}
