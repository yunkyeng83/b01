package org.zerock.b01.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController    // 해당 클래스 파일은 API서버를 구동하기 위해서 필요한 Controller
@Log4j2
public class SampleJSONController {
    @GetMapping("/helloArr")
    public String[] helloArr() {
        log.info("helloArr.................");

        return new String[] { "AAA", "BBB", "CCC" };
    }

    @GetMapping("/helloJson")
    public ResponseEntity<String> helloJson() {
        log.info("helloJson.................");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return new ResponseEntity<>(
                "{ \"key3\": \"value3\", \"key4\": \"value4\" }",
                headers,
                HttpStatus.OK
        );
    }
}
