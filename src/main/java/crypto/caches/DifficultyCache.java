package crypto.caches;

import crypto.block.Difficulty;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class DifficultyCache {

    private final Set<Difficulty> difficulties = new LinkedHashSet<>();

    public void add(Difficulty difficultyData){
        Optional<Difficulty> found = get(difficultyData.currency());
        if (found.isEmpty()){
            difficulties.add(difficultyData);
        }
    }

    public Optional<Difficulty> get(String currency) {
        return difficulties.stream().filter(c -> currency.equals(c.currency())).findFirst();
    }

}
