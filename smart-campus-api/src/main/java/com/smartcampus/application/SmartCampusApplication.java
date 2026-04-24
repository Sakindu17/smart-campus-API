package com.smartcampus.application;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Main JAX-RS configuration class.
 * Defines the root path for all API services as /api/v1.
 *
 * Architecture Detail: Resource instances are typically created for each request.
 * Shared state is managed via the singleton DataStore to ensure consistency.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // Automatic discovery of resources is handled through package scanning.
}
