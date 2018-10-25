/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patchworkgalaxy.lang;

import com.patchworkgalaxy.general.lang.Localizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalizerTest {
    
    private static final List<String> _vals = new ArrayList<>();
    private static final String[] _namespaces = new String[] {"test"};
    private static final String[] _namespacesWithDummy = new String[] {"dummy", "test", "dummy"};
    
    @BeforeClass public static void setUpClass() {
	Localizer.setLocalization("test.txt");
	_vals.addAll(Arrays.asList(new String[] {"alpha", "beta", "gamma", "delta", "epsilon"}));
    }

    @Test public void testConstant() {
	String result = Localizer.getLocalizedString(_namespaces, "constant");
	assertTrue(_vals.contains(result));
	for(int i = 0; i < 999; ++i)
	    assertEquals(result, Localizer.getLocalizedString(_namespaces, "constant"));
    }
    
    @Test public void testIterator() {
	Iterator<String> expected = _vals.iterator();
	for(int i = 0; i < 999; ++i) {
	    String val = Localizer.getLocalizedString(_namespaces, "iterator");
	    assertEquals(expected.next(), val);
	    if(!expected.hasNext())
		expected = _vals.iterator();
	}
    }
    
    @Test public void testRandomIterator() {
	for(int i = 0; i < 999; ++i) {
	    Set<String> remaining = new HashSet<>(_vals);
	    while(!remaining.isEmpty()) {
		String val = Localizer.getLocalizedString(_namespaces, "randomiterator");
		assertTrue(remaining.contains(val));
		remaining.remove(val);
	    }
	}
    }
    
    @Test public void testNamespaceFallback() {
	for(int i = 0; i < 999; ++i) {
	    String val = Localizer.getLocalizedString(_namespacesWithDummy, "random");
	    assertTrue(_vals.contains(val));
	}
    }
    
    @Test public void testUnknownKey() {
	assertEquals("", Localizer.getLocalizedString(_namespaces, "xyzzy"));
    }
    
    @Test public void testUnknownNamespace() {
	assertEquals("", Localizer.getLocalizedString(new String[] {"xyzzy"}, "xyzzy"));
    }
    
    @Test public void withoutNamespace() {
	assertEquals("lol", Localizer.getLocalizedString(new String[] {""}, "this_has_no_namespace"));
    }
    
}