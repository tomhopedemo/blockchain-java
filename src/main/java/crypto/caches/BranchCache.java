package crypto.caches;

import crypto.block.Branch;
import crypto.block.Currency;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class BranchCache {

    private final Set<Branch> branches = new LinkedHashSet<>();

    public void add(Branch branch){
        Optional<Branch> found = get(branch.branchKey());
        if (found.isEmpty()){
            branches.add(branch);
        }
    }

    public Optional<Branch> get(String branchKey) {
        return branches.stream().filter(c -> branchKey.equals(c.key())).findFirst();
    }

    public void remove(String branchKey){
        branches.removeIf(branch -> branchKey.equals(branch.branchKey()));
    }

}
