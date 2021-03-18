package net.pet.project.simplerest.controller.administrating;

import lombok.extern.slf4j.Slf4j;
import net.pet.project.simplerest.controller.AbstractController;
import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.service.administrating.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Realisation {@link RestController} for {@link User}.
 * @author VN
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/user")
public class UserController extends AbstractController<User, User, Long> {

    @Autowired
    public UserController(UserService service) {
        super(service);
    }

    //================================================= PRIVATE METHODS ================================================

    @Override
    protected User toEntity(User dto) {
        return dto;
    }

    @Override
    protected User toDTO(User entity) {
        return entity;
    }
}