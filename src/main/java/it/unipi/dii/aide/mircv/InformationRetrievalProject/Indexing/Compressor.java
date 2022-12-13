package it.unipi.dii.aide.mircv.InformationRetrievalProject.Indexing;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class Compressor {

    public byte[] simpleEncode(int num){
        BigInteger bigInteger = BigInteger.valueOf(num);
        return bigInteger.toByteArray();
    }

    public int simpleDecode(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getInt();
    }
}
