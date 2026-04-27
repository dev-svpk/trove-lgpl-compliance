package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.c_ref.ai;
import gnu.trove.ca.a;

class TObjectIntHashMap$TObjectIntHashIterator extends a implements ai {
   private final TObjectIntHashMap _map;
   // $FF: synthetic field
   final TObjectIntHashMap this$0;

   public TObjectIntHashMap$TObjectIntHashIterator(TObjectIntHashMap var1, TObjectIntHashMap var2) {
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

   public int value() {
      return this._map._values[this._index];
   }

   public int setValue(int var1) {
      int var2 = this.value();
      this._map._values[this._index] = var1;
      return var2;
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
