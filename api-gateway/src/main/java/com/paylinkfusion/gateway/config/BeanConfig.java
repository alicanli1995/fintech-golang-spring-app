package com.paylinkfusion.gateway.config;

import com.paylinkfusion.gateway.helpers.ServerUtil;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BeanConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new CustomAuditAware();
    }

    public static class CustomAuditAware implements AuditorAware<String> {

        @NonNull
        @Override
        public Optional<String> getCurrentAuditor() {
            return Optional.of(ServerUtil.LOCAL_HOST_NAME);
        }
    }

}
