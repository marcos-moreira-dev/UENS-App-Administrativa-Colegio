package com.marcosmoreiradev.uensdesktop.nav;

import com.marcosmoreiradev.uensdesktop.session.Role;
import java.util.Set;

public record RouteDefinition(String fxmlPath, Set<Role> allowedRoles, boolean publicView) {
}
