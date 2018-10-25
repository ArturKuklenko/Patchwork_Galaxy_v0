package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexRuleException;

interface Monitor {
    
    abstract void onPoked(AbstractContext context, Record trigger) throws PlexRuleException;
    
}
