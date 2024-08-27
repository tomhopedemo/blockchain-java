# Java Blockchain Implementation
### Overview

### Basics

All of the implementations are fundamentally blockchain instances of the following classes/interfaces :

```Blockchain.java, Block.java, BlockDataHashable.java```

### Implementations

#### SimpleChain
SimpleChain is a simple (non-signed implementation of a blockchain). The block data is string-based and is built forward compatability in mind, leveraging Json/Gson with the restriction that data objects should have leaf fields built from strings which when serialised completely represent the data. 

#### SignedChain
SignedChain is a blockchain with multiple data per block. Each instance of data is signed.


#### UTXOChain

UTXOChain is a blockchain with a multiple UTXO-based transactions per block. 
The transaction is composed of a list of inputs and outputs. 
The data is signed for inclusion into the next block, and the transaction inputs are solved through ECDSA signature authentication on the transaction output public key.

#### AccountChain

AccountChain is an account-based blockchain with multiple transactions per block. There are restrictions on which transactions can be accepted by the miner, and the same validation on transactions is applied as with UTXOChain. Additional validation is applied across the set of transactions submitted to prevent double spending.     
