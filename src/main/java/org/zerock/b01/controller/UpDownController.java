package org.zerock.b01.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.upload.UploadFileDTO;
import org.zerock.b01.dto.upload.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Log4j2
@RestController
public class UpDownController {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    @ApiOperation(value = "첨부파일 POST", notes = "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
        log.info(uploadFileDTO);

        if(uploadFileDTO.getFiles() != null) {
            List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {
                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                // 첨부파일 이름 규칙 -> UUID_기존의첨부파일이름
                String uuid = UUID.randomUUID().toString();   // 임의의 문자열
                String newFileName = uuid + "_" + originalName;

                Path savePath = Paths.get(uploadPath, newFileName);

                boolean image = false;
                // 실제 하드디스크에 첨부파일 저장
                try {
                    multipartFile.transferTo(savePath);

                    // 업로드된 파일이 이미지인지 체크
                    if(Files.probeContentType(savePath).startsWith("image")) {
                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                        // savePath에 있는 파일을 thumbFile로 바꾸되 크기는 50x50으로 하여 저장
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 50, 50);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                UploadResultDTO uploadResultDTO = UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(image)
                        .build();
                list.add(uploadResultDTO);
            });

            return list;
        }
        return null;
    }

    // 첨부파일 조회 api
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String filename) {
        // 파라미터로 전달된 filename을 가지고 uploadPath폴더 안에 있는 파일들을 찾기
        Resource resource = new FileSystemResource(uploadPath + File.separator + filename);
        String resourceName = resource.getFilename();
        log.info("resourceName: " + resourceName);

        // 응답헤더에 담을 정보를 바꾸기 위해 필요
        HttpHeaders headers = new HttpHeaders();
        try {
            // 응답 헤더로 이미지파일을 표시할 수 있도록 Content-Type값을 변경.
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 첨부파일 삭제 api
    @ApiOperation(value = "remove 파일", notes = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")
    public Map<String,Boolean> removeFile(@PathVariable String fileName){
        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);
        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            //섬네일이 존재한다면
            if(contentType.startsWith("image")){
                File thumbnailFile = new File(uploadPath+File.separator +"s_" + fileName);
                thumbnailFile.delete();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);
        return resultMap;
    }
}
