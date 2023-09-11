package com.paylinkfusion.gateway.models.dto;

import lombok.Builder;

@Builder
public record UserRegisterEvent(long accountId) {

}
