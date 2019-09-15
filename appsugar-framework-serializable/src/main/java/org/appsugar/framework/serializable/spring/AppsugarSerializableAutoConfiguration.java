package org.appsugar.framework.serializable.spring;

import org.appsugar.framework.serializable.AppsugarSerializable;
import org.appsugar.framework.serializable.ClassMapping;
import org.appsugar.framework.serializable.fst.FstSerializable;
import org.appsugar.framework.serializable.spring.redis.AppsugarRedisSerializer;
import org.nustaq.serialization.FSTConfiguration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.*;
import java.util.stream.Collectors;

@ConditionalOnProperty(prefix = "spring.appsugar.framework.serializable", name = "enabled", matchIfMissing = true)
@Configuration
public class AppsugarSerializableAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SerializableResource serializableResource(BeanFactory beanFactory) {
        List<String> packages = AutoConfigurationPackages.get(beanFactory);
        return new SerializableResource(packages, false);
    }

    @Bean
    @ConditionalOnClass(FSTConfiguration.class)
    @ConditionalOnMissingBean
    public FstSerializable fstSerializable(SerializableResource resource) {
        return new FstSerializable(scanClassMapping(resource.scanPackages), resource.shareReference);
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(RedisSerializer.class)
    @ConditionalOnBean(FstSerializable.class)
    public AppsugarRedisSerializer<Object> fstRedisSerializer(FstSerializable fs) {
        return new AppsugarRedisSerializer<>(fs);
    }

    protected List<AppsugarSerializable.ClassMappingPair> scanClassMapping(List<String> packages) {
        ClassPathScanningCandidateComponentProvider scan = getScanningProvider();
        Set<String> classNameSet = new HashSet<>();
        for (String aPackage : packages) {
            scan.findCandidateComponents(aPackage).forEach(e -> classNameSet.add(e.getBeanClassName()));
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        List<AppsugarSerializable.ClassMappingPair> result = new ArrayList<>(classNameSet.size());
        for (String className : classNameSet) {
            result.add(initClassMappingPair(className, cl));
        }
        Map<Integer, List<AppsugarSerializable.ClassMappingPair>> groupingResult = result.stream().collect(Collectors.groupingBy(e -> e.code));
        String title = "Some class annotated the same id ";
        StringBuilder sb = new StringBuilder(title);
        groupingResult.values().forEach(e -> {
            if (e.size() == 1) return;
            sb.append(e);
        });
        String warning = sb.toString();
        if (!title.equals(warning)) {
            throw new IllegalArgumentException(warning);
        }
        return result;
    }

    protected AppsugarSerializable.ClassMappingPair initClassMappingPair(String className, ClassLoader cl) {
        try {
            Class clazz = cl.loadClass(className);
            ClassMapping cm = (ClassMapping) clazz.getAnnotation(ClassMapping.class);
            return new AppsugarSerializable.ClassMappingPair(cm.value(), clazz);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("class not found", ex);
        }
    }

    protected ClassPathScanningCandidateComponentProvider getScanningProvider() {
        ClassPathScanningCandidateComponentProvider scan = new ClassPathScanningCandidateComponentProvider(false);
        scan.addIncludeFilter(new AnnotationTypeFilter(ClassMapping.class));
        return scan;
    }
}
