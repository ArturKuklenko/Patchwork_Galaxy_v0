package com.patchworkgalaxy.general.lang;

import java.util.Collection;

enum LocalizedStringMode {
    
    CONSTANT {
	@Override LocalizedString createLocalizedString(Collection<String> values) {
	    return new LocalizedStringConstant(values);
	}
    },
    
    ITERATOR {
	@Override LocalizedString createLocalizedString(Collection<String> values) {
	    if(values.size() == 1)
		return new LocalizedStringConstant(values);
	    return new LocalizedStringIterator(values, false);
	}
    },
    
    RANDOM {
	@Override LocalizedString createLocalizedString(Collection<String> values) {
	    if(values.size() == 1)
		return new LocalizedStringConstant(values);
	    return new LocalizedStringRandom(values);
	}
    }, 
    
    RANDOM_ITERATOR {
	@Override LocalizedString createLocalizedString(Collection<String> values) {
	    if(values.size() == 1)
		return new LocalizedStringConstant(values);
	    return new LocalizedStringIterator(values, true);
	}
    };
	
    abstract LocalizedString createLocalizedString(Collection<String> values);
    
}
