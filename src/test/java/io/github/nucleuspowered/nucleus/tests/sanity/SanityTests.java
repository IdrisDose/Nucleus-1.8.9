/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.tests.sanity;

import com.google.common.reflect.ClassPath;
import io.github.nucleuspowered.nucleus.internal.qsml.NucleusConfigAdapter;
import io.github.nucleuspowered.nucleus.internal.qsml.module.StandardModule;
import org.junit.Assert;
import org.junit.Test;
import uk.co.drnaylor.quickstart.annotations.ModuleData;
import uk.co.drnaylor.quickstart.config.AbstractConfigAdapter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SanityTests {

    @Test
    @SuppressWarnings("unchecked")
    public void testThatAnythingThatIsAConcreteModuleHasAModuleDataAnnotation() throws IOException {
        Set<ClassPath.ClassInfo> ci = ClassPath.from(this.getClass().getClassLoader()).getTopLevelClassesRecursive("io.github.nucleuspowered.plugin.modules");
        Set<Class<? extends StandardModule>> sc = ci.stream().map(ClassPath.ClassInfo::load).filter(StandardModule.class::isAssignableFrom)
                .map(x -> (Class<? extends StandardModule>)x).collect(Collectors.toSet());

        List<Class<?>> moduleList = sc.stream().filter(x -> !x.isAnnotationPresent(ModuleData.class)).collect(Collectors.toList());
        if (!moduleList.isEmpty()) {
            StringBuilder sb = new StringBuilder("Some modules do not have the ModuleData annotation: ");
            moduleList.forEach(x -> sb.append(x.getName()).append(System.lineSeparator()));
            Assert.fail(sb.toString());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testThatAnythingThatIsAnAbstractConfigAdapterIsAlsoANucleusConfigAdapter() throws IOException {
        Set<ClassPath.ClassInfo> ci = ClassPath.from(this.getClass().getClassLoader()).getTopLevelClassesRecursive("io.github.nucleuspowered.plugin.modules");
        Set<Class<? extends AbstractConfigAdapter<?>>> sc = ci.stream().map(ClassPath.ClassInfo::load).filter(AbstractConfigAdapter.class::isAssignableFrom)
                .map(x -> (Class<? extends AbstractConfigAdapter<?>>)x).collect(Collectors.toSet());

        List<Class<?>> moduleList = sc.stream().filter(x -> !NucleusConfigAdapter.class.isAssignableFrom(x)).collect(Collectors.toList());
        if (!moduleList.isEmpty()) {
            StringBuilder sb = new StringBuilder("Some config adapters are not of the NucleusConfigAdapter type: ");
            moduleList.forEach(x -> sb.append(x.getName()).append(System.lineSeparator()));
            Assert.fail(sb.toString());
        }
    }

    @Test
    public void testThatNoResourceKeyIsAParentOfAnother() throws Exception {
        // Get the resource
        testKeysAreNotParents("assets.nucleus.messages");
    }

    @Test
    public void testThatNoCommandResourceKeyIsAParentOfAnother() throws Exception {
        // Get the resource
        testKeysAreNotParents("assets.nucleus.commands");
    }

    private void testKeysAreNotParents(String bundle) throws Exception {
        // Get the resource
        ResourceBundle rb = ResourceBundle.getBundle(bundle, Locale.getDefault());
        Enumeration<String> keys = rb.getKeys();
        Set<String> s = new HashSet<>();

        while (keys.hasMoreElements()) {
            s.add(keys.nextElement());
        }

        Map<String, List<String>> filter = s.parallelStream()
                .map(x -> new DoubleTuple<>(x.toLowerCase(),
                        s.stream().filter(y -> x.toLowerCase().startsWith(y.toLowerCase() + ".") && !x.equalsIgnoreCase(y)).collect(Collectors.toList())))
                .filter(x -> !x.value.isEmpty())
                .sorted()
                .collect(Collectors.toMap(k -> k.key, v -> v.value));
        if (!filter.isEmpty()) {
            StringBuilder sb = new StringBuilder("Some keys are parents of others!").append(System.lineSeparator());
            filter.forEach((x, y) -> sb.append(x).append("->").append(y).append(System.lineSeparator()));
            Assert.fail(sb.toString());
        }
    }

    private class DoubleTuple<A extends Comparable<A>, B> implements Comparable<DoubleTuple<A, B>> {
        A key;
        B value;

        private DoubleTuple(A a, B b) {
            this.key = a;
            this.value = b;
        }

        @Override
        public int compareTo(DoubleTuple<A, B> o) {
            return key.compareTo(o.key);
        }
    }
}
