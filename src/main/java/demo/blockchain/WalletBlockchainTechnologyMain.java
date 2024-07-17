package demo.blockchain;

import java.security.Security;

public class WalletBlockchainTechnologyMain {

    private record RunParameters (int difficulty, int numBlockchains, int numBlocksToMine) { }

    public static void main(String[] args) throws Exception {
        RunParameters runParameters = new RunParameters(1, 2, 4);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //Construction
        BlockchainStore blockchainStore = new BlockchainSuperFactory(runParameters.difficulty(), runParameters.numBlockchains()).construct();
        WalletStore walletStore = new WalletStoreFactory(2).generate();

        //Transacting
        Transfer transfer = new Transfer(walletStore);
        transfer.sendFunds(walletStore.get(0), walletStore.get(1).publicKeyAddress, 5);
//        new TransactionFactory(blockchainStore.blockchains.getFirst(), walletStore).transact();

        //Mining
        SuperBlockMining superBlockMining = new SuperBlockMining(runParameters.numBlocksToMine(), runParameters.difficulty());
        superBlockMining.mine(blockchainStore);

        //Validation
        SuperValidator superValidator = new SuperValidator(blockchainStore);
        superValidator.validate();

        //Visualization
        new SuperBlockchainVisualiser(blockchainStore).visualise();
        new SuperWalletVisualiser(walletStore).visualise();
    }
}
