package org.appsugar.framework.serializable.fst;

import org.appsugar.framework.serializable.AppsugarSerializable;
import org.nustaq.serialization.FSTClazzNameRegistry;
import org.nustaq.serialization.FSTConfiguration;

import java.util.List;

public class FstSerializable implements AppsugarSerializable {
    protected List<ClassMappingPair> preRegistryClassPair;
    protected boolean shareReference;

    FstThreadLocal ftl = new FstThreadLocal();

    public FstSerializable(List<ClassMappingPair> preRegistryClassPair, boolean shareReference) {
        this.preRegistryClassPair = preRegistryClassPair;
        this.shareReference = shareReference;
    }

    @Override
    public Object asObject(byte[] content) {
        return getFstConfiguration().asObject(content);
    }

    @Override
    public byte[] asByteArray(Object content) {
        return getFstConfiguration().asByteArray(content);
    }

    protected FSTConfiguration getFstConfiguration() {
        return ftl.get();
    }

    class FstThreadLocal extends ThreadLocal<FSTConfiguration> {
        @Override
        protected FSTConfiguration initialValue() {
            FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
            FSTClazzNameRegistry registry = conf.getClassRegistry();
            preRegistryClassPair.forEach(e -> registry.registerClass(e.clazz, e.code, conf));
            conf.setShareReferences(shareReference);
            return conf;
        }
    }


}
