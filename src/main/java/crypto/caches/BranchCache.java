package crypto.caches;

import crypto.block.Branch;
import crypto.block.Currency;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class BranchCache {

    private final Set<Branch> branches = new LinkedHashSet<>();

    public void add(Branch currencyData){
        Optional<Branch> found = get(currencyData.key());
        if (found.isEmpty()){
            branches.add(currencyData);
        }
    }

    public Optional<Branch> get(String key) {
        return branches.stream().filter(c -> key.equals(c.key())).findFirst();
    }

}
