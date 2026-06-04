package com.chat.web.ocr.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 영수증 OCR 분석 결과 VO (WEB 레이어).
 * WAS 응답 역직렬화 및 프론트엔드 전달에 사용된다.
 */
@Getter
@Setter
@NoArgsConstructor
public class OcrResultVo {

    @JsonProperty("is_receipt")
    private Boolean isReceipt;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("merchant")
    private MerchantVo merchant;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("payments")
    private List<PaymentVo> payments;

    @JsonProperty("total_amount")
    private Integer totalAmount;
}
