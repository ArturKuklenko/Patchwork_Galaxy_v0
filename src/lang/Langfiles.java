package lang;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Langfiles {
    
    private Langfiles() {}
    
    public static BufferedReader getReader(String filename) {
	InputStream is = Langfiles.class.getResourceAsStream(filename);
	InputStreamReader isr = new InputStreamReader(is);
	BufferedReader br = new BufferedReader(isr);
	return br;
    }
    
}
