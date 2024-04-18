package jpabook.jpashop.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NanoIdProvider {

    @Value("${nanoId.size}")
    private int nanoIdSize;

    public String createNanoId(int size){
        Random random = new Random();
        return NanoIdUtils.randomNanoId(random, NanoIdUtils.DEFAULT_ALPHABET, size);
    }


    public String createNanoId(){
        return createNanoId(nanoIdSize);
    }

}
