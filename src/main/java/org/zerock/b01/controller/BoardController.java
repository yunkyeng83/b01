package org.zerock.b01.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.*;
import org.zerock.b01.service.BoardService;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        // 게시글
        //PageResponseDTO<BoardDTO> pageResponseDTO = boardService.list(pageRequestDTO);
        // 게시글 + 댓글 수
//        PageResponseDTO<BoardListReplyCountDTO> pageResponseDTO
//                = boardService.listWithReplyCount(pageRequestDTO);
        // 게시글 + 댓글 수 + 첨부파일 이미지 정보
        PageResponseDTO<BoardListAllDTO> pageResponseDTO
                = boardService.listWithAll(pageRequestDTO);
        log.info("responseDTO: " + pageResponseDTO);

        model.addAttribute("responseDTO", pageResponseDTO);
    }

    @PreAuthorize("hasRole('USER')")    // ROLE_USER인가(권한)를 가진 사람만 사용이 가능
    //@PreAuthorize("hasAnyRole('USER', 'ADMIN'") // 인증된 사용자가 USER 혹은 ADMIN권한 하나만 존재해도 사용 가능
    @GetMapping("/register")
    public void registerGET() {
        // /board/register.html파일로 이동
    }
    @PostMapping("/register")
    public String registerPOST (
        @Valid BoardDTO boardDTO,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        log.info("POST board register.......");
        log.info(boardDTO);

        // 만약에 todoDTO에서 설정한 validation이 통과되지 않은 경우
        if(bindingResult.hasErrors()) {
            log.info("has errors.......");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            return "redirect:/board/register";
        }

        Long bno = boardService.register(boardDTO);
        // list.html에 보낼 등록 게시물의 번호 결과값을 전달
        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/board/list";
    }

    @PreAuthorize("isAuthenticated()")    // 인증만 되면 사용 가능
    @GetMapping({"/read", "/modify"})
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info(boardDTO);
        model.addAttribute("dto", boardDTO);
    }

    @PreAuthorize("principal.username == #boardDTO.writer") // 로그인한 username과 boardDTO의 writer가 같은 경우만 허용
    @PostMapping("/modify")
    public String modify(
            PageRequestDTO pageRequestDTO,   // 페이지 네비게이션 처리를 위한 변수
            @Valid BoardDTO boardDTO,       // 실제 게시판 수정을 위한 변수
            BindingResult bindingResult,    // valid오류가 났을 때 처리하기 위한 변수
            RedirectAttributes redirectAttributes // 성공했을 경우 화면에 출력할 데이터 저장 변수
    ) {
        log.info("board modify post......." + boardDTO);

        // boardDTO의 validation처리에서 오류가 났을 경우
        if(bindingResult.hasErrors()) {
            log.info("has errors.......");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors() );
            redirectAttributes.addAttribute("bno", boardDTO.getBno());

            return "redirect:/board/modify?" + link;
        }
        boardService.modify(boardDTO);  // 실제 게시판 내용 수정

        // 모달창을 보이기 위한 속성
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("bno", boardDTO.getBno());

        return "redirect:/board/read";  // 수정 성공 후 게시판 상세조회 페이지로 이동
    }

    @PreAuthorize("principal.username == #boardDTO.writer") // 로그인한 username과 boardDTO의 writer가 같은 경우만 허용
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO, RedirectAttributes rttr) {
        Long bno = boardDTO.getBno();
        boardService.remove(bno);   // 실제 게시물 번호 삭제

        // 게시물의 첨부파일 삭제
        log.info(boardDTO.getFileNames());
        List<String> fileNames = boardDTO.getFileNames();
        if(fileNames != null && fileNames.size() > 0){
            removeFiles(fileNames);
        }

        rttr.addFlashAttribute("result", "removed");
        return "redirect:/board/list";
    }

    // 실제 첨부파일 삭제
    public void removeFiles(List<String> files){
        for (String fileName:files) {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            //String resourceName = resource.getFilename();

            try {
                // 원본파일 삭제
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();

                //섬네일이 존재한다면 섬네일 파일 삭제
                if (contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }//end for
    }
}
