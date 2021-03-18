package net.pet.project.simplerest.module.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.pet.project.simplerest.controller.administrating.UserController;
import net.pet.project.simplerest.dto.administrating.UserDto;
import net.pet.project.simplerest.model.administrating.User;
import net.pet.project.simplerest.service.administrating.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static net.pet.project.simplerest.util.TestData.asJsonString;
import static net.pet.project.simplerest.util.TestData.createValidUserDtoWithoutId;
import static net.pet.project.simplerest.util.TestData.createValidUserWithId;
import static net.pet.project.simplerest.util.TestData.createValidUserWithoutId;
import static net.pet.project.simplerest.util.TestData.inPage;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserWebMvcTest {

    public static final String API_URL = "/api/v1/admin/user";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService service;

    @BeforeAll
    static void init() { }

    @BeforeEach
    void clearMock() {
        reset(service);
    }

    @Nested
    @DisplayName("getAll()")
    class GetAll {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("getAll() => service.getAll()")
            void getAll_callGetAll() throws Exception {
                when(service.getAll()).thenReturn(emptyList());

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/all").accept(APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
                verify(service, times(1)).getAll();
            }

            @Test
            @DisplayName("getAll() when empty DB => 200 []")
            void getAll_emptyDB_returnEmptyList() throws Exception {
                when(service.getAll()).thenReturn(emptyList());

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/all").accept(APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(0)));
            }

            @Test
            @DisplayName("getAll() => 200 [user, ... , user]")
            void getAll_usersExistInDB_returnUsersList() throws Exception {
                User expected = createValidUserWithId();
                expected.setPassword(null);

                when(service.getAll()).thenReturn(singletonList(createValidUserWithId()));

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/all").accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0]").value(expected));
            }
        }
    }

    @Nested
    @DisplayName("getPage(page)")
    class GetPage {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("getAll(page) => service.getAll(page)")
            void getPage_authorized_callGetAll() throws Exception {
                when(service.getAll(any(Pageable.class))).thenReturn(Page.empty());

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL).accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());

                verify(service, times(1)).getAll(PageRequest.of(0, 10));
            }

            @Test
            @DisplayName("getPage(null) when empty DB => 200 Page([])")
            void getPage_whenEmptyDBAndDefaultPage_returnEmptyPage() throws Exception {
                when(service.getAll(any(Pageable.class))).thenReturn(Page.empty());

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL).accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.content", hasSize(0)));
            }

            @Test
            @DisplayName("getPage() => 200 Page([user, ... , user])")
            void getPage_usersExistInDB_returnUsersPage() throws Exception {
                User expected = createValidUserWithId();
                expected.setPassword(null);

                when(service.getAll(any(Pageable.class))).thenReturn(inPage(createValidUserWithId()));
                when(service.getAll()).thenReturn(singletonList(createValidUserWithId()));

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL).accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.content", hasSize(1)))
                        .andExpect(jsonPath("$.content[0]").value(expected));
            }
        }
    }

    @Nested
    @DisplayName("get(id)")
    class Get {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("get(id not null) => service.get(id)")
            void get_idNotNull_callGet() throws Exception {
                long id = 1L;

                when(service.get(anyLong())).thenReturn(createValidUserWithId());

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
                verify(service, times(1)).get(id);
            }

            @Test
            @DisplayName("get(id) => body {user}")
            void get_userByIdExist_jsonUserInBody() throws Exception {
                User expected = createValidUserWithId();
                expected.setPassword(null);

                when(service.get(expected.getId())).thenReturn(createValidUserWithId());

                mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + expected.getId())
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").value(expected));
            }
        }
    }

    @Nested
    @DisplayName("create(user)")
    class Create {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("create(valid user) => service.create(user)")
            void create_isValid_callCreate() throws Exception {
                User expected = createValidUserWithoutId();
                String json = asJsonString(createValidUserDtoWithoutId());

                when(service.create(createValidUserWithoutId())).thenReturn(createValidUserWithId());

                mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
                verify(service, times(1)).create(expected);
            }

            @Test
            @DisplayName("create(valid user) => 200 {user with id}")
            void create_isValid_jsonCreatedUserInBody() throws Exception {
                User expected = createValidUserWithId();
                expected.setPassword(null);
                String json = asJsonString(createValidUserDtoWithoutId());

                when(service.create(createValidUserWithoutId())).thenReturn(createValidUserWithId());

                mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").value(expected));
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @DisplayName("create(invalid user) => dont call service.create(user)")
            @ParameterizedTest(name = "create(\"{0}\") => dont call service.create(user)")
            @ValueSource(strings = {"", "{}", "{car:\"car\"}"})
            void create_isInvalid_dontCallCreate(String json) throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().is4xxClientError());
                verify(service, never()).create(any());
            }

            @DisplayName("create(user with blank login) => dont call service.create(user)")
            @ParameterizedTest(name = "create(user with login=\"{0}\") => dont call service.create(user)")
            @NullSource
            @ValueSource(strings = {"", " "})
            void create_nameIsBlank_dontCallCreate(String login) throws Exception {
                User user = createValidUserWithoutId();
                user.setLogin(login);

                mockMvc.perform(MockMvcRequestBuilders.post(API_URL)
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(422));
                verify(service, never()).create(any());
            }
        }
    }

    @Nested
    @DisplayName("update(id, user)")
    class Update {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("update(id, valid user) => service.update(user)")
            void update_isValid_callUpdate() throws Exception {
                User expected = createValidUserWithId();
                final UserDto userDto = createValidUserDtoWithoutId();
                userDto.setId(expected.getId());
                String json = asJsonString(userDto);

                when(service.update(expected)).thenReturn(expected);

                mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/1")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
                verify(service, times(1)).update(expected);
            }

            @Test
            @DisplayName("update(id, valid user) => 200 {user with id}")
            void update_isValid_jsonUpdatedUserInBody() throws Exception {
                User expected = createValidUserWithId();
                expected.setPassword(null);
                final UserDto userDto = createValidUserDtoWithoutId();
                userDto.setId(expected.getId());
                String json = asJsonString(userDto);

                when(service.update(createValidUserWithId())).thenReturn(createValidUserWithId());

                mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/1")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$").value(expected));
            }
        }

        @Nested
        @DisplayName("- negative")
        class Negative {

            @DisplayName("update(id, invalid user) => dont call service.update(user)")
            @ParameterizedTest(name = "update(id, \"{0}\") => dont call service.update(user)")
            @ValueSource(strings = {"", "{}", "{car:\"car\"}"})
            void update_isInvalid_dontCallUpdate(String json) throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/1")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().is4xxClientError());
                verify(service, never()).update(any());
            }

            @DisplayName("update(id, user with blank login) => dont call service.update(user)")
            @ParameterizedTest(name = "update(id, user with login=\"{0}\") => dont call service.update(user)")
            @NullSource
            @ValueSource(strings = {"", " "})
            void update_loginIsBlank_dontCallUpdate(String login) throws Exception {
                User user = createValidUserWithoutId();
                user.setLogin(login);

                mockMvc.perform(MockMvcRequestBuilders.put(API_URL + "/1")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(422));
                verify(service, never()).update(any());
            }
        }
    }

    @Nested
    @DisplayName("delete(id)")
    class Delete {

        @Nested
        @DisplayName("+ positive")
        class Positive {

            @Test
            @DisplayName("delete(id) => service.delete(id)")
            void delete_idNotNull_callDelete() throws Exception {
                long id = 1L;

                mockMvc.perform(MockMvcRequestBuilders.delete(API_URL + "/" + id))
                        .andDo(print())
                        .andExpect(status().isOk());
                verify(service, times(1)).delete(id);
            }

            @Test
            @DisplayName("get(id) => status 200")
            void delete_idNotNull_isOkStatus() throws Exception {
                User expected = createValidUserWithId();

                when(service.get(expected.getId())).thenReturn(createValidUserWithId());
                mockMvc.perform(MockMvcRequestBuilders.get(API_URL + "/" + expected.getId())
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
            }
        }
    }
}