package com.patchworkgalaxy.template;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.general.util.TypedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParserTypeTest {
    
    public ParserTypeTest() {
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
    public void tearDown() {}
    
    @Test
    public void testVector3f() throws ParseException {
	
	Vector3f vec3 = ParserType.VECTOR3F.check(null, "Blah", "3, 4, 5");
	assertEquals(new Vector3f(3, 4, 5), vec3);
	
    }
    
    @Test
    public void testColor() throws ParseException {
	
	ColorRGBA color;
	color = ParserType.COLOR.check(null, "Blah", "00ff00");
	assertEquals(new ColorRGBA(0f, 1f, 0f, 1f), color);
	
	color = ParserType.COLOR.check(null, "Blah", "00ff0000");
	assertEquals(new ColorRGBA(0f, 1f, 0f, 0f), color);
	
    }
    
    @Test
    public void testParserListType() throws ParseException {
	
	MutableGameProps props = new MutableGameProps();
	
	ParserType vec3list = ParserType.VECTOR3F.toListType();
	
	String key = "blah";
	props.set(key, vec3list.check(props, key, "0, 1, 2"));
	props.set(key, vec3list.check(props, key, "3, 4, 5"));
	props.set(key, vec3list.check(props, key, "6, 7, 8"));
	
	@SuppressWarnings("unchecked")
	List<Vector3f> vecs = props.get(TypedList.class, key).asListOfType(Vector3f.class);
	
	assertEquals(3, vecs.size());
	assertEquals(new Vector3f(0, 1, 2), vecs.get(0));
	assertEquals(new Vector3f(3, 4, 5), vecs.get(1));
	assertEquals(new Vector3f(6, 7, 8), vecs.get(2));
	
    }
    
}