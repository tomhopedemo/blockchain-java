package crypto.caches;

import crypto.block.Currency;
import crypto.block.Referendum;

import java.util.*;

public class ReferendumCache {

    private final Map<String, Referendum> referendums = new HashMap<>();

    public void add(Referendum referendum){
        referendums.putIfAbsent(referendum.referendumKey(), referendum);
    }

    public Referendum get(String referendumKey) {
        return referendums.get(referendumKey);
    }

}
