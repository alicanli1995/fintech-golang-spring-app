package com.paylinkfusion.payment.models.dto.request;


import lombok.Builder;

@Builder
public record UserRegisterEvent(
        long accountId
) {

}
