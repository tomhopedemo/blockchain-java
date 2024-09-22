package crypto;

import crypto.hashing.Hashing;

import java.util.List;

public record BlockData<T extends BlockDataHashable> (List<T> data) implements BlockDataHashable {

    //not sure this is really prehash - also not sure why we need to hash
    //again we may want indices here for the individual data records.
    //also is it not sufficient to get the prehashes
    @Override
    public String getPreHash()  {
        return String.join("", data.stream().map(data -> data.getPreHash()).toList());
    }

}
