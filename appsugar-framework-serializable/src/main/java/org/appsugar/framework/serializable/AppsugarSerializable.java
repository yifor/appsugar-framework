package org.appsugar.framework.serializable;

public interface AppsugarSerializable {
    Object asObject(byte[] content);

    byte[] asByteArray(Object content);

    static class ClassMappingPair {
        public final int code;
        public final Class<?> clazz;

        public ClassMappingPair(int code, Class<?> clazz) {
            this.code = code;
            this.clazz = clazz;
        }

        @Override
        public String toString() {
            return "ClassMappingPair{" +
                    "code=" + code +
                    ", clazz=" + clazz +
                    '}';
        }
    }
}
