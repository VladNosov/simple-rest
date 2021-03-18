package net.pet.project.simplerest.dto.administrating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.pet.project.simplerest.dto.IDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author VN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto implements IDto {

    private Long id;
    @NotBlank
    private String login;
    @NotBlank
    @Size(min = 5, max = 64)
    private String password;
    private boolean active;
}
