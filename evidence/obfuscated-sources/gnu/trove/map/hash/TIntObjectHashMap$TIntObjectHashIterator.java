package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.ba.J_ref;
import gnu.trove.c_ref.S_ref;

class TIntObjectHashMap$TIntObjectHashIterator extends J_ref implements S_ref {
   private final TIntObjectHashMap _map;
   // $FF: synthetic field
   final TIntObjectHashMap this$0;

   public TIntObjectHashMap$TIntObjectHashIterator(TIntObjectHashMap var1, TIntObjectHashMap var2) {
      super(var2);
      this.this$0 = var1;
      this._map = var2;
   }

   public void advance() {
      this.moveToNextIndex();
   }

   public int key() {
      return this._map._set[this._index];
   }

   public Object value() {
      return this._map._values[this._index];
   }

   public Object setValue(Object var1) {
      Object var2 = this.value();
      this._map._values[this._index] = var1;
      return var2;
   }

    @Override
    public boolean contains(char var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public U_ref iterator() {
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
