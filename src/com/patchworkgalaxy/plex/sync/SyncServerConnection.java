package com.patchworkgalaxy.plex.sync;

import com.patchworkgalaxy.plex.Transaction;
import java.util.List;

public interface SyncServerConnection {
    
    void synchronize(List<String> syncData);
    
    void accept(Transaction transaction);
    
}
