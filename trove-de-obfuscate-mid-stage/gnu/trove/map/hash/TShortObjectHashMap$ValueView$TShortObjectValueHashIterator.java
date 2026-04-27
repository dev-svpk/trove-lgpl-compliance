package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import java.util.Iterator;

class TShortObjectHashMap$ValueView$TShortObjectValueHashIterator extends THashPrimitiveIterator implements Iterator {
   protected final TShortObjectHashMap _map;
   // $FF: synthetic field
   final TShortObjectHashMap$ValueView this$1;

   public TShortObjectHashMap$ValueView$TShortObjectValueHashIterator(TShortObjectHashMap$ValueView var1, TShortObjectHashMap var2) {
      super(var2);
      this.this$1 = var1;
      this._map = var2;
   }

   protected Object objectAtIndex(int var1) {
      byte[] var2 = TShortObjectHashMap$ValueView.access$0(this.this$1)._states;
      Object var3 = this._map._values[var1];
      return var2[var1] != 1 ? null : var3;
   }

   public Object next() {
      this.moveToNextIndex();
      return this._map._values[this._index];
   }

    @Override
    public boolean contains(char var1) {
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

    @Override
    public boolean contains(int var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(long var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
