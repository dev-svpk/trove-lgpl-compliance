package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.iterator.TCharIterator;
import java.util.ConcurrentModificationException;

class TByteIntHashMap$TByteIntHashIterator extends THashPrimitiveIterator implements TByteIntIterator {
   // $FF: synthetic field
   final TByteIntHashMap this$0;

   TByteIntHashMap$TByteIntHashIterator(TByteIntHashMap var1, TByteIntHashMap var2) {
      super(var2);
      this.this$0 = var1;
   }

   public void advance() {
      this.moveToNextIndex();
   }

   public byte key() {
      return this.this$0._set[this._index];
   }

   public int value() {
      return this.this$0._values[this._index];
   }

   public int setValue(int var1) {
      int var2 = this.value();
      this.this$0._values[this._index] = var1;
      return var2;
   }

   public void remove() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         try {
            this._hash.tempDisableAutoCompaction();
            this.this$0.removeAt(this._index);
         } finally {
            this._hash.reenableAutoCompaction(false);
         }

         --this._expectedSize;
      }
   }

    @Override
    public boolean contains(char var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TCharIterator iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(int var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(long var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte nextByte() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int nextInt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public char nextChar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long nextLong() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float nextFloat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public short nextShort() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double nextDouble() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
