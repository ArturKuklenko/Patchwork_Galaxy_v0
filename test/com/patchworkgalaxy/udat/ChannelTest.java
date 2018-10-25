package com.patchworkgalaxy.udat;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChannelTest {
    
    public ChannelTest() {
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
	assertTrue(new ChannelData("Dread Piracy", true).isDelta());
	assertFalse(new ChannelData("Dread Piracy", false).isDelta());
    }

    @Test
    public void testGetChannelName() {
	System.out.println("getChannelName");
	ChannelData instance = new ChannelData("Dread Piracy", false);
	assertEquals("Dread Piracy", instance.getChannelName());
	instance = new ChannelData(null, false);
	assertEquals("", instance.getChannelName());
    }

    @Test
    public void testGetAndSet() {
	System.out.println("getAndSet");
	ChannelData instance = new ChannelData("Dread Piracy", false);
	UserData user1 = new UserData("Foofoo", false);
	UserData user2 = new UserData("Barbar", false);
	instance.add(user1);
	instance.add(user2);
	assertEquals(user1, instance.getUserdata().get("Foofoo"));
	assertEquals(user2, instance.getUserdata().get("Barbar"));
	assertEquals(null, instance.getUserdata().get("Potato"));
	instance.remove("Foofoo");
	assertEquals(null, instance.getUserdata().get("Foofoo"));
    }

    @Test
    public void testUpdate_Channel() {
	ChannelData instance = new ChannelData("Dread Piracy", false);
	UserData commonUser = new UserData("Foofoo", false);
	commonUser.setDatum("foo", "spam");
	UserData leavingUser = new UserData("Leaving", false);
	instance.add(commonUser);
	instance.add(leavingUser);
	
	ChannelData update = new ChannelData("Dread Piracy", true);
	commonUser = new UserData(commonUser);
	commonUser.setDatum("foo", "eggs");
	UserData joiningUser = new UserData("Joining", false);
	update.add(commonUser);
	update.add(joiningUser);
	update.remove("Leaving");
	
	instance.update(update);
	
	assertEquals("eggs", instance.getUserdata().get("Foofoo").getData().get("foo"));
	assertNotNull(instance.getUserdata().get("Joining"));
	assertEquals(null, instance.getUserdata().get("Leaving"));
	
    }

    @Test
    public void testUpdate_Userdata() {
	ChannelData instance = new ChannelData("Dread Piracy", false);
	UserData user1 = new UserData("Foofoo", false);
	UserData user2 = new UserData(user1);
	user1.setDatum("foo", "spam");
	user2.setDatum("foo", "eggs");
	instance.add(user1);
	instance.update(user2);
	assertEquals("eggs", instance.getUserdata().get("Foofoo").getData().get("foo"));
    }
}