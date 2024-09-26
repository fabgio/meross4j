package org.meross4j.bundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Activator implements BundleActivator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public void start(BundleContext bundleContext) {
        logger.debug("Starting meross4j bundle");
    }

    @Override
    public void stop(BundleContext bundleContext) {
        logger.debug("Stopping meross4j bundle");
    }
}
