package ru.example.lam.server.service.mapping;

import lombok.extern.log4j.Log4j2;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import ru.example.lam.server.coreserver.utils.deserialize.CustomDateDeserializer;
import ru.example.lam.server.dto.users.UserDTO;
import ru.example.lam.server.entity.users.Role;
import ru.example.lam.server.entity.users.User;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@Log4j2
public abstract class UserMapper {

    @AfterMapping
    protected void setRolesList(UserDTO userDTO, List<Role> rolesList, List<String> wrongRoleValues, @MappingTarget User.UserBuilder userBuilder) {
        log.debug("Method setRolesList is started");
        Set<String> roleNamesSet = userDTO.getRolesSet();
        Set<Role> roleSetForUser = new HashSet<>();
        Map<String, Role> rolesMap = rolesList.stream()
                .collect(Collectors.toMap(Role::getRoleValue, Function.identity()));
        log.debug("Roles map collected {}", rolesMap);
        for(String roleName : roleNamesSet) {
            if(rolesMap.containsKey(roleName)) {
                roleSetForUser.add(rolesMap.get(roleName));
                log.debug("Role set for user added {}", roleSetForUser);
            } else {
                wrongRoleValues.add(roleName);
                log.debug("Wrong role values added {}", wrongRoleValues);
            }
        }
        userBuilder.roles(roleSetForUser);
    }

    @Mapping(target = "registrationTime", source = "userDTO.registrationTime", dateFormat = CustomDateDeserializer.DATE_FORMAT)
    public abstract User fromDtoToUser(UserDTO userDTO, List<Role> rolesList, List<String> wrongRoleValues);
}
