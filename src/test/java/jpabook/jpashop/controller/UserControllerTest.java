package jpabook.jpashop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jpabook.jpashop.controller.request.UserRequest;
import jpabook.jpashop.domain.user.AddressInfo;
import jpabook.jpashop.domain.user.User;
import jpabook.jpashop.repository.UserRepository;
import jpabook.jpashop.util.PasswordUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Test
    @DisplayName("Username/Password로 회원가입을 수행해 DB에 저장할 수 있다.")
    public void testRegisterUsernamePasswordUser() throws Exception{
        //given
        UserRequest.Create createForm = new UserRequest.Create(
                "name",
                "email@email.com",
                "address",
                "detailedAddress",
                "http://example.com/image.png",
                "username",
                "abc1234!"
        );
        String createFormString = objectMapper.writeValueAsString(createForm);

        //when
        mockMvc.perform(post("/api/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createFormString))
                .andDo(print()).andExpect(status().isOk());
        //then
        User user = userRepository.findByUsername("username")
                .orElseThrow(RuntimeException::new);

        assertAll("유저의 기본 정보를 모두 담고 있어야 한다.",
                ()->assertThat(user).extracting("email", "name", "profileImageUrl", "addressInfo")
                        .contains("email@email.com", "name", "http://example.com/image.png", new AddressInfo("address", "detailedAddress")),
                ()->assertThat(user.getUid()).isNotNull());

        boolean isPasswordMatches = passwordUtils.matches("abc1234!", user.getUsernamePasswordAuthInfo().getSaltBytes(), user.getUsernamePasswordAuthInfo().getPassword());

        assertAll("유저의 인증정보가 저장되어 후에 인증이 수행가능해야 한다.",
                ()->assertThat(user.getUsernamePasswordAuthInfo().getUsername()).isEqualTo("username"),
                ()->assertThat(isPasswordMatches).isTrue());

    }


}