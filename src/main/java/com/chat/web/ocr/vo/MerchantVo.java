package com.chat.web.ocr.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 영수증 가맹점 정보 VO (WEB 레이어).
 * WAS 응답 역직렬화 및 프론트엔드 전달에 사용된다.
 */
@Getter
@Setter
@NoArgsConstructor
public class MerchantVo {

    @JsonProperty("biz_number")
    private String bizNumber;

    @JsonProperty("name")
    private String name;

    @JsonProperty("address")
    private String address;

    @JsonProperty("tel")
    private String tel;
}
