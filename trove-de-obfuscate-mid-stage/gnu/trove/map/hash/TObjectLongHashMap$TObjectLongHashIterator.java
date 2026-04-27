package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;

class TObjectLongHashMap$TObjectLongHashIterator extends TObjectHashIterator implements TObjectLongIterator {
   private final TObjectLongHashMap _map;
   // $FF: synthetic field
   final TObjectLongHashMap this$0;

   public TObjectLongHashMap$TObjectLongHashIterator(TObjectLongHashMap var1, TObjectLongHashMap var2) {
      super(var2);
      this.this$0 = var1;
      this._map = var2;
   }

   public void advance() {
      this.moveToNextIndex();
   }

   public Object key() {
      return this._map._set[this._index];
   }

   public long value() {
      return this._map._values[this._index];
   }

   public long setValue(long var1) {
      long var3 = this.value();
      this._map._values[this._index] = var1;
      return var3;
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
