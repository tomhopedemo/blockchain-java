package demo.blockchain;

import java.security.PublicKey;

public record TransactionOutput (String id, PublicKey recipient, long value){}

