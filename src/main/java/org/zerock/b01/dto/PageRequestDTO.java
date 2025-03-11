package org.zerock.b01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page = 1;   // 보여줄 page 쪽
    @Builder.Default
    private int size = 10;  // 보여줄 page size

    private String link;    // page와 size를 같이 세팅한 문자열

    // 검색/필터링 요청 속성들
    private String type;   // 검색의 종류(t, c, w. tc, tw, twc)
    private String keyword;   // 검색할 문자들

    private boolean isToggle; // toggle메뉴인지 아닌지 여부

    public String[] getTypes(){
        if(type == null || type.isEmpty()){
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String...props) {  // aa, bb, cc, ...
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
        //return PageRequest.of(this.page - 1, this.size, Sort.Direction.DESC, props);
    }

    public String getLink() {
        if(link == null) {
            StringBuilder builder = new StringBuilder();
            // page와 page size에 대한 링크 처리
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(type != null && type.length() > 0) {  // 할일에 대한 제목과 작성자에 대한 링크값
                builder.append("&type=" + type);  // t, w, c, tw, tc, twc
            }
            if(keyword != null) {			// 할일에 대한 제목과 작성자의 문자 링크값
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));  // 한글처리를 위한 코드
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            builder.append("&isToggle=" + this.isToggle);

            link = builder.toString();
        }

        return link;
    }
}