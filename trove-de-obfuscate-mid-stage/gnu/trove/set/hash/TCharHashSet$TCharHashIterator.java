package gnu.trove.set.hash;

import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TCharHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.TCharIterator;

public class TCharHashSet$TCharHashIterator extends THashPrimitiveIterator implements TCharIterator {
   private final TCharHash _set;
   // $FF: synthetic field
   private TCharHashSet this$0;

   public TCharHashSet$TCharHashIterator(TCharHashSet var1, TCharHash var2) {
      super(var2);
      this.this$0 = var1;
      this._set = var2;
   }

   @Override
   public final char next() {
      this.moveToNextIndex();
      return this._set._set[this._index];
   }

    @Override
    public boolean contains(char var1) {
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
    public TIterator iterator() {
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
