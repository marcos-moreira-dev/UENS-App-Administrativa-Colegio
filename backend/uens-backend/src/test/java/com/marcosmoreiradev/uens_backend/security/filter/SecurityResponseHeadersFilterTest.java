package com.marcosmoreiradev.uens_backend.security.filter;

import com.marcosmoreiradev.uensbackend.config.properties.SecurityHeadersProperties;
import com.marcosmoreiradev.uensbackend.security.filter.SecurityResponseHeadersFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityResponseHeadersFilterTest {

    @Test
    void addsDefensiveHeadersAndHstsForSecureRequests() throws Exception {
        SecurityResponseHeadersFilter filter = new SecurityResponseHeadersFilter(
                new SecurityHeadersProperties(true, 600L, true, "no-referrer", "camera=(), microphone=()")
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/system/ping");
        request.setSecure(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(response.getHeader("X-Frame-Options")).isEqualTo("DENY");
        assertThat(response.getHeader("Referrer-Policy")).isEqualTo("no-referrer");
        assertThat(response.getHeader("Permissions-Policy")).isEqualTo("camera=(), microphone=()");
        assertThat(response.getHeader("Strict-Transport-Security")).isEqualTo("max-age=600; includeSubDomains");
    }

    @Test
    void doesNotEmitHstsForNonSecureRequests() throws Exception {
        SecurityResponseHeadersFilter filter = new SecurityResponseHeadersFilter(
                new SecurityHeadersProperties(true, 600L, true, "no-referrer", "camera=()")
        );
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/system/ping");
        request.setSecure(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader("Strict-Transport-Security")).isNull();
    }
}
