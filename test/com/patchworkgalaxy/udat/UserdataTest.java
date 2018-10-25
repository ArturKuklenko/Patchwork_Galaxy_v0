/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patchworkgalaxy.udat;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserdataTest {
    
    public UserdataTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testIsDelta() {
	System.out.println("isDelta");
	UserData instance = new UserData("Test", true);
	boolean expResult = true;
	boolean result = instance.isDelta();
	assertEquals(expResult, result);
	instance = new UserData("Test", false);
	expResult = false;
	result = instance.isDelta();
	assertEquals(expResult, result);
    }

    @Test
    public void testGetUsername() {
	System.out.println("getUsername");
	UserData instance = new UserData("DEADBEEF", false);
	String expResult = "DEADBEEF";
	String result = instance.getUsername();
	assertEquals(expResult, result);
	instance = new UserData(null, false);
	expResult = "";
	result = instance.getUsername();
	assertEquals(expResult, result);
    }

    @Test
    public void testGetAndSet() {
	System.out.println("getAndSet");
	UserData instance = new UserData("Test", false);
	assertTrue(instance.getData().isEmpty());
	instance.setDatum("foo", "spam");
	assertEquals("spam", instance.getData().get("foo"));
	instance.setDatum("foo", "eggs");
	assertEquals("eggs", instance.getData().get("foo"));
	instance.setDatum("foo", null);
	assertEquals(null, instance.getData().get("foo"));
	instance.setDatum("bar", null);
	assertEquals(null, instance.getData().get("bar"));
    }

    @Test
    public void testUpdate() {
	System.out.println("update");
	UserData instance = new UserData("Test", false);
	instance.setDatum("seven", "7");
	instance.setDatum("three", "3");
	instance.setDatum("foo", "bar");
	UserData other = new UserData("Test", true);
	other.setDatum("foo", "asdf");
	other.setDatum("spam", "eggs");
	other.setDatum("three", null);
	UserData other2 = new UserData(instance);
	instance.update(other);
	assertEquals("7", instance.getData().get("seven"));
	assertEquals("asdf", instance.getData().get("foo"));
	assertEquals("eggs", instance.getData().get("spam"));
	assertEquals(null, instance.getData().get("three"));
	instance.update(other2);
	assertEquals(instance.getData(), other2.getData());
	other2.setDatum("blah", "blah");
	assertFalse(instance.getData().equals(other2.getData()));
	UserData thisbetternotwork = new UserData("DEADBEEF", true);
	try {
	    instance.update(thisbetternotwork);
	    fail();
	}
	catch(IllegalArgumentException e) {}
    }
}