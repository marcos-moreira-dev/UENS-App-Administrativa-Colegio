package com.marcosmoreiradev.uens_backend.common.exception;

import com.marcosmoreiradev.uensbackend.common.api.response.ApiErrorResponse;
import com.marcosmoreiradev.uensbackend.common.api.response.ErrorDetailDto;
import com.marcosmoreiradev.uensbackend.common.constants.RequestAttributes;
import com.marcosmoreiradev.uensbackend.common.exception.GlobalExceptionHandler;
import com.marcosmoreiradev.uensbackend.common.exception.base.BusinessRuleException;
import com.marcosmoreiradev.uensbackend.common.exception.codes.ApiErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.codes.StudentErrorCodes;
import com.marcosmoreiradev.uensbackend.common.exception.mapper.ExceptionToApiErrorMapper;
import com.marcosmoreiradev.uensbackend.common.i18n.MessageResolver;
import com.marcosmoreiradev.uensbackend.common.validation.ValidationErrorAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(
                new ExceptionToApiErrorMapper(),
                new ValidationErrorAssembler(),
                new MessageResolver(new StaticMessageSource())
        );
    }

    @Test
    void handleApiExceptionReturnsStableBusinessPayload() {
        MockHttpServletRequest request = buildRequest("/api/v1/estudiantes/10");
        BusinessRuleException exception = new BusinessRuleException(
                StudentErrorCodes.RN_EST_04_CUPO_SECCION_AGOTADO,
                "La seccion no tiene cupo disponible.",
                Map.of("seccionId", 10L)
        );

        var response = handler.handleApiException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo("RN-EST-04-CUPO_SECCION_AGOTADO");
        assertThat(response.getBody().getMessage()).isEqualTo("La seccion no tiene cupo disponible.");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/estudiantes/10");
        assertThat(response.getBody().getRequestId()).isEqualTo("req-test-01");
        assertThat(response.getBody().getDetails()).isEqualTo(Map.of("seccionId", 10L));
    }

    @Test
    void handleBeanValidationReturnsVrCodeAndFieldDetails() throws Exception {
        MockHttpServletRequest request = buildRequest("/api/v1/reportes/solicitudes");
        Method method = DummyController.class.getDeclaredMethod("crear", DummyRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new DummyRequest(), "request");
        bindingResult.addError(new FieldError(
                "request",
                "seccionId",
                null,
                false,
                new String[]{"NotNull"},
                null,
                "La seccion es obligatoria."
        ));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        var response = handler.handleBeanValidation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiErrorCodes.VR_01_REQUEST_INVALIDO.code());
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/reportes/solicitudes");
        assertThat(response.getBody().getRequestId()).isEqualTo("req-test-01");
        assertThat(response.getBody().getDetails()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<ErrorDetailDto> details = (List<ErrorDetailDto>) response.getBody().getDetails();
        assertThat(details).hasSize(1);
        assertThat(details.getFirst().field()).isEqualTo("seccionId");
        assertThat(details.getFirst().code()).isEqualTo("NotNull");
        assertThat(details.getFirst().message()).isEqualTo("La seccion es obligatoria.");
    }

    @Test
    void handleResponseStatusMapsLegacyReasonToStableBusinessCode() {
        MockHttpServletRequest request = buildRequest("/api/v1/estudiantes");
        ResponseStatusException exception = new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La seccion no tiene cupo disponible."
        );

        var response = handler.handleResponseStatus(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(StudentErrorCodes.RN_EST_04_CUPO_SECCION_AGOTADO.code());
        assertThat(response.getBody().getMessage()).isEqualTo("La seccion no tiene cupo disponible.");
    }

    @Test
    void handleResponseStatusFallsBackToApiCodeByStatus() {
        MockHttpServletRequest request = buildRequest("/api/v1/inexistente");
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND);

        var response = handler.handleResponseStatus(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiErrorCodes.API_04_RECURSO_NO_ENCONTRADO.code());
        assertThat(response.getBody().getMessage()).isEqualTo(ApiErrorCodes.API_04_RECURSO_NO_ENCONTRADO.defaultMessage());
    }

    @Test
    void handleUnexpectedReturnsGenericSysCode() {
        MockHttpServletRequest request = buildRequest("/api/v1/reportes/solicitudes/20/resultado");

        var response = handler.handleUnexpected(new IllegalStateException("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ApiErrorCodes.SYS_01_ERROR_INTERNO.code());
        assertThat(response.getBody().getMessage()).isEqualTo(ApiErrorCodes.SYS_01_ERROR_INTERNO.defaultMessage());
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/reportes/solicitudes/20/resultado");
    }

    private MockHttpServletRequest buildRequest(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        request.setAttribute(RequestAttributes.REQUEST_ID, "req-test-01");
        return request;
    }

    @SuppressWarnings("unused")
    private static final class DummyController {
        private void crear(DummyRequest request) {
        }
    }

    private static final class DummyRequest {
    }
}
