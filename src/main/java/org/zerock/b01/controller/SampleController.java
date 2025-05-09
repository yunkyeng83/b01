package org.zerock.b01.controller;

import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Log4j2
public class SampleController {
    @GetMapping("/hello")  // url이 /hello로 끝나면 해당 메소드가 실행
    public void hello(Model model) {
        log.info("hello.................");

        model.addAttribute("msg", "Hello World");
        // 마지막에는 /resources/templates/hello.html파일로 이동
    }

    @GetMapping("/ex/ex1")
    public void ex1(Model model) {
        List<String> list = Arrays.asList("AAA", "BBB", "CCC", "DDD");

        model.addAttribute("list1", list);
        // 마지막에는 /resources/templates/ex/ex1.html파일로 이동
    }

    // SampleController안에 있는 내부 클래스(inner class)
    @ToString
    class SampleDTO {
        private String p1,p2,p3;

        public String getP1() {
            return p1;
        }
        public String getP2() {
            return p2;
        }
        public String getP3() {
            return p3;
        }
    }

    @GetMapping("/ex/ex2")
    public void ex2(Model model) {
        log.info("ex/ex2................");

        List<String> strList = IntStream.range(1,10)
                .mapToObj(i -> "Data"+i)
                .collect(Collectors.toList());

        model.addAttribute("list", strList);

        // -------------------------------------------------------
        Map<String, String> map = new HashMap<>();
        map.put("A","AAAA");
        map.put("B","BBBB");

        model.addAttribute("map", map);
        // -------------------------------------------------------
        SampleDTO sampleDTO = new SampleDTO();
        sampleDTO.p1 = "Value -- p1";
        sampleDTO.p2 = "Value -- p2";
        sampleDTO.p3 = "Value -- p3";

        model.addAttribute("dto", sampleDTO);
    }

    @GetMapping("/ex/ex3")
    public void ex3(Model model) {
        model.addAttribute("arr", new String[]{"AAA","BBB","CCC"});
    }

    @GetMapping("/ex/ex4")
    public void ex4(Model model) {

    }
}
