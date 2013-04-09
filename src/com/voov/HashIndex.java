package com.voov;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class HashIndex {

    private class KeyValue {
        private String key;
        private String value;

        private KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        private String getKey() {
            return key;
        }

        private void setKey(String key) {
            this.key = key;
        }

        private String getValue() {
            return value;
        }

        private void setValue(String value) {
            this.value = value;
        }
    }

    private class Bucket {
        public ArrayList<KeyValue> contents = new ArrayList<KeyValue>(3);
        private int localDepth = 0;

        private int getLocalDepth() {
            return localDepth;
        }

        private void setLocalDepth(int localDepth) {
            this.localDepth = localDepth;
        }

        public boolean canFit() {
            return contents.size() < 3;
        }

        public void add(KeyValue kv) {
            contents.add(kv);
        }
    }

    private ByteBuffer dir;
    private ArrayList<Bucket> bucketList;
    private int globalDepth = 0;

    public HashIndex() {
        bucketList = new ArrayList<Bucket>();
        dir = ByteBuffer.allocate(4); // 2^0
        bucketList.add(new Bucket());
        dir.putInt(0);
    }

    public void expandDirectory() {
        int newDepth = globalDepth+1;
        int startIdx = (int)Math.pow(2, globalDepth);
        int endIdx = (int)Math.pow(2, newDepth);



        globalDepth = newDepth;
    }

    /* */
    public void add(String key, String value) {
        int hashCode = key.hashCode();
        int searchMask = hashCode & ((1 << globalDepth) -1);
        KeyValue kv = new KeyValue(key, value);

        dir.position(searchMask*4); // 4 int mérete

        int pos = dir.getInt();

        Bucket b = bucketList.get(pos);
        if(!b.canFit()) {
            // need to expand the directory
            ArrayList<KeyValue> buffer = new ArrayList<KeyValue>();
            for(KeyValue kvBuffer : b.contents) {
                buffer.add(kvBuffer);
            }
            buffer.add(kv);

            if(b.getLocalDepth() == globalDepth) {
                expandDirectory();
            }

            int posForNewBucket = bucketList.size();


        } else {
            b.add(kv);
        }
    }
}
