package com.marcosmoreiradev.uensdesktop.modules.auth.view;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.LoginResponseDto;
import com.marcosmoreiradev.uensdesktop.api.modules.auth.dto.MeResponseDto;
import com.marcosmoreiradev.uensdesktop.app.AppContext;
import com.marcosmoreiradev.uensdesktop.app.ContextAwareController;
import com.marcosmoreiradev.uensdesktop.modules.auth.application.AuthService;
import com.marcosmoreiradev.uensdesktop.nav.ViewId;
import com.marcosmoreiradev.uensdesktop.session.UsuarioSession;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxExecutors;
import java.time.Instant;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public final class LoginController implements ContextAwareController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label feedbackLabel;

    @FXML
    private Button loginButton;

    private AppContext appContext;
    private AuthService authService;

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.authService = appContext.services().authService();
    }

    @FXML
    private void onLogin() {
        String login = loginField.getText() == null ? "" : loginField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (login.isBlank() || password.isBlank()) {
            feedbackLabel.setText("Ingresa usuario y contraseña.");
            return;
        }

        setLoading(true, "Conectando con backend...");

        FxExecutors.submitIo(() -> authService.login(login, password), this::handleLoginResult);
    }

    private void handleLoginResult(ApiResult<LoginResponseDto> result) {
        setLoading(false, null);
        if (!result.isSuccess()) {
            String message = result.error().map(error -> error.message()).orElse("No se pudo iniciar sesión.");
            feedbackLabel.setText(message);
            return;
        }

        LoginResponseDto loginResponse = result.data().orElseThrow();
        appContext.sessionState().login(
                loginResponse.accessToken(),
                Instant.now().plusSeconds(loginResponse.expiresInSeconds()),
                loginResponse.refreshToken(),
                Instant.now().plusSeconds(loginResponse.refreshExpiresInSeconds()),
                loginResponse.usuario());
        hydrateCurrentUser();
        passwordField.clear();
        feedbackLabel.setText("Inicio de sesión exitoso.");
        appContext.navigator().navigate(ViewId.DASHBOARD);
    }

    private void hydrateCurrentUser() {
        ApiResult<MeResponseDto> meResult = authService.me();
        if (meResult.isSuccess()) {
            MeResponseDto me = meResult.data().orElseThrow();
            appContext.sessionState().updateUsuario(new UsuarioSession(me.id(), me.login(), me.rol(), me.estado()));
        }
    }

    private void setLoading(boolean loading, String message) {
        loginButton.setDisable(loading);
        loginField.setDisable(loading);
        passwordField.setDisable(loading);
        if (message != null) {
            feedbackLabel.setText(message);
        }
    }
}
