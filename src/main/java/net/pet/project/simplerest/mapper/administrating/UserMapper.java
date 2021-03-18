package net.pet.project.simplerest.mapper.administrating;

import lombok.experimental.UtilityClass;
import net.pet.project.simplerest.dto.administrating.UserDto;
import net.pet.project.simplerest.model.administrating.User;

import java.util.Objects;

/**
 * @author Vlad Nosov
 */
@UtilityClass
public class UserMapper {

    public static User toEntity(UserDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }
        return User.builder()
                .id(dto.getId())
                .login(dto.getLogin())
                .password(dto.getPassword())
                .active(dto.isActive())
                .build();
    }

    public static UserDto toDto(User entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return UserDto.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .active(entity.isActive())
                .build();
    }
}
