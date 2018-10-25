package com.patchworkgalaxy.general.lang;

import java.util.Random;

interface LocalizedString {
    
    String getLocalizedValue(Random rng);
    
    boolean isMultiple();
    
}
