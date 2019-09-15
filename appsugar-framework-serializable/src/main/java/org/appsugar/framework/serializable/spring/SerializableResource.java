package org.appsugar.framework.serializable.spring;

import java.util.List;

public class SerializableResource {
    public final List<String> scanPackages;
    public final boolean shareReference;

    public SerializableResource(List<String> scanPackages, boolean shareReference) {
        this.scanPackages = scanPackages;
        this.shareReference = shareReference;
    }
}
