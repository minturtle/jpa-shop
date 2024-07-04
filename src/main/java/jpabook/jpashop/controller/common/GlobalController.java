package jpabook.jpashop.controller.common;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "전역 API", description = "테스트용 전역 API입니다.")
public class GlobalController {



    @Operation(summary = "Health Check API", description = "서버의 정상 구동 여부를 확인하기 위한 API입니다.")
    @GetMapping("/health-check")
    public String healthCheck(){
        return "hello!";
    }

}
