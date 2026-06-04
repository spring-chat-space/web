package com.chat.web.ocr.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 영수증 결제 정보 VO (WEB 레이어).
 * WAS 응답 역직렬화 및 프론트엔드 전달에 사용된다.
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentVo {

    @JsonProperty("card_issuer")
    private String cardIssuer;

    @JsonProperty("approval_number")
    private String approvalNumber;

    @JsonProperty("installment")
    private Integer installment;

    @JsonProperty("amount")
    private Integer amount;
}
