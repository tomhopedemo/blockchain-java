//package demo.blockchain;
//
//import demo.cryptography.ECDSA;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MerkleRoot {
//
//    public static String getMerkleRoot(List<TransactionRequest> transactionRequests) {
//        int count = transactionRequests.size();
//        List<String> previousTreeLayer = new ArrayList<String>();
//        for(TransactionRequest transaction : transactionRequests) {
//            previousTreeLayer.add(transaction.transactionId);
//        }
//        ArrayList<String> treeLayer = previousTreeLayer;
//        while(count > 1) {
//            treeLayer = new ArrayList<String>();
//            for(int i=1; i < previousTreeLayer.size(); i++) {
//                treeLayer.add(ECDSA.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
//            }
//            count = treeLayer.size();
//            previousTreeLayer = treeLayer;
//        }
//        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
//        return merkleRoot;
//    }
//}
