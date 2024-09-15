package crypto.caches;

import crypto.block.difficulty.DifficultyRequest;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class DifficultyCache {

    private final Set<DifficultyRequest> difficulties = new LinkedHashSet<>();

    public void add(DifficultyRequest difficultyData){
        Optional<DifficultyRequest> found = get(difficultyData.currency());
        if (found.isEmpty()){
            difficulties.add(difficultyData);
        }
    }

    public Optional<DifficultyRequest> get(String currency) {
        return difficulties.stream().filter(c -> currency.equals(c.currency())).findFirst();
    }

}
