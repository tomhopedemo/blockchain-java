package crypto.block.difficulty;

import crypto.Request;

public record DifficultyRequest(Integer difficulty, String currency, String publicKey) implements Request {

    @Override
    public String getPreHash() {
        return publicKey + "~" + currency + "~" + difficulty;
    }

}
