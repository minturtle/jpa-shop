package jpabook.jpashop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jpabook.jpashop.controller.response.ErrorResponse;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.dto.MemberDto;
import jpabook.jpashop.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = MemberController.class)
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    private MemberDto memberDto;
    private MockHttpSession session;
    @BeforeEach
    void setUp() {
        memberDto = new MemberDto(1L, "helloworld", "1111", "민석", "구미시", "금오공대", "본관");
        session = new MockHttpSession();
    }

    @Test
    @DisplayName("회원가입 하기")
    void t1() throws Exception {
        //given
        //when
        final ResultActions resultAction = mockMvc.perform(post("/member/signIn")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberDto)));
        //then
        resultAction.andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패-비밀번호 길이")
    void t2() throws Exception {
        //given
        memberDto.setPassword("11");
        doThrow(new MemberService.RegisterFailed("비밀번호는 4글자 이상이여야 합니다.")).when(memberService).register(memberDto);

        //when
        final ResultActions resultAction = mockMvc.perform(post("/member/signIn")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberDto)));
        //then
        ErrorResponse responseBody = getResponseBody(resultAction, ErrorResponse.class);

        resultAction.andExpect(status().is4xxClientError());
        assertThat(responseBody.getMessage()).isEqualTo("비밀번호는 4글자 이상이여야 합니다.");
    }

    @Test
    @DisplayName("회원가입 실패-중복되는 아이디")
    void t11() throws Exception {
        //given
        doThrow(new MemberService.RegisterFailed("이미 존재하는 ID입니다.")).when(memberService).register(memberDto);
        //when
        final ResultActions resultAction = mockMvc.perform(post("/member/signIn")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberDto)));
        //then
        ErrorResponse responseBody = getResponseBody(resultAction, ErrorResponse.class);

        resultAction.andExpect(status().is4xxClientError());
        assertThat(responseBody.getMessage()).isEqualTo("이미 존재하는 ID입니다.");
    }


    @Test
    @DisplayName("로그인 하기")
    void t3() throws Exception {
        //given
        given(memberService.login(memberDto)).willReturn(1L);
        //when
        ResultActions resultAction = mockMvc.perform(post("/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(memberDto)));
        //then
        resultAction.andExpect(status().isOk());
        assertThat(session.getAttribute("userId")).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인하기-아이디 찾기 불가능")
    void t4() throws Exception {
        //given
        given(memberService.login(memberDto)).willThrow(new MemberService.LoginFailed("유저 정보를 찾을 수 없습니다."));
        //when
        ResultActions resultAction = mockMvc.perform(post("/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(memberDto)));
        //then
        ErrorResponse responseBody = getResponseBody(resultAction, ErrorResponse.class);

        resultAction.andExpect(status().is4xxClientError());
        assertThat(responseBody.getMessage()).isEqualTo("유저 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인하기-비밀번호 오류")
    void t12() throws Exception {
        //given
        given(memberService.login(memberDto)).willThrow(new MemberService.LoginFailed("잘못된 비밀번호 입니다."));
        //when
        ResultActions resultAction = mockMvc.perform(post("/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(memberDto)));
        //then
        ErrorResponse responseBody = getResponseBody(resultAction, ErrorResponse.class);

        resultAction.andExpect(status().is4xxClientError());
        assertThat(responseBody.getMessage()).isEqualTo("잘못된 비밀번호 입니다.");
    }
    @Test
    @DisplayName("마이페이지 정보 조회")
    void t5() throws Exception {
        //given

        given(memberService.getMemberDetail(1L)).willReturn(memberDto);
        session.setAttribute("userId", 1L);

        //when
        ResultActions resultAction = mockMvc.perform(get("/member/detail")
                .session(session)
                .characterEncoding(StandardCharsets.UTF_8));
        //then
        MemberDetailResponse responseBody = getResponseBody(resultAction, MemberDetailResponse.class);
        resultAction.andExpect(status().isOk());
        assertThat(responseBody).isEqualTo(new MemberDetailResponse("민석", "구미시 금오공대 본관"));
    }

    @Test
    @DisplayName("이름 변경-통합 테스트에서 실제 변경되었는지 확인 필요")
    void t6() throws Exception {
        //given
        //when
        ResultActions resultAction = mockMvc.perform(patch("/member/name")
                .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\" : \"김민민석\"}"));
        //then
        resultAction.andExpect(status().isOk());
    }

    @Test
    @DisplayName("주소 변경-통합 테스트에서 실제 변경되었는지 확인 필요")
    void t7() throws Exception {
        //given
        session.setAttribute("userId", 1L);
        //when
        ResultActions resultAction = mockMvc.perform(patch("/member/address")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(new Address("경북 구미시", "옥계동", "무한리필 고깃집"))));
        //then
        resultAction.andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 변경-통합 테스트에서 실제 변경되었는지 확인 필요")
    void t8() throws Exception {
        //given
        session.setAttribute("userId", 1L);
        //when
        ResultActions resultAction = mockMvc.perform(patch("/member/password")
                .session(session)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\" : \"1122\"}"));
        //then
        resultAction.andExpect(status().isOk());
    }

    @Test
    @DisplayName("비밀번호 변경-실패")
    void t9() throws Exception {
        //given
        session.setAttribute("userId", 1L);
        Mockito.doThrow(new IllegalStateException("비밀번호는 4글자 이상이여야 합니다.")).when(memberService).updatePassword(1L, "11");
        //when
        ResultActions resultAction = mockMvc.perform(patch("/member/password")
                .session(session)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\" : \"11\"}"));
        //then
        final ErrorResponse responseBody = getResponseBody(resultAction, ErrorResponse.class);

        resultAction.andExpect(status().is4xxClientError());
        assertThat(responseBody.getMessage()).isEqualTo("비밀번호는 4글자 이상이여야 합니다.");
    }
    @Test
    @DisplayName("로그아웃")
    void t10() throws Exception {
        //given
        session.setAttribute("userId", 1L);

        //when
        final ResultActions resultAction = mockMvc.perform(post("/member/logout").session(session));
        //then
        resultAction.andExpect(status().isOk());
        assertThat(session.getAttribute("userId")).isNull();
    }

    private <T> T getResponseBody(ResultActions action, Class<T> responseType) throws JsonProcessingException, UnsupportedEncodingException {
        final String resultString = action.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(resultString, responseType);
    }
}