# Java Blockchain Implementation
### Overview

Simple Blockchain is a general purpose blockchain structure. The block data is generic and is built forward compatability in mind, leveraging Json/Gson with the restriction that data objects should have leaf fields built from strings which when serialised completely represent the data. 

Transational Blockchain is a blockchain with a single transaction per block. The transaction is composed of a list of inputs and outputs. the data is signed for inclusion into the next block, and the transaction inputs are solved through ECDSA signature authentication on the transaction output public key.