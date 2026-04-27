package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.c_ref.af;
import gnu.trove.ca.a;

class TObjectCharHashMap$TObjectCharHashIterator extends a implements af {
   private final TObjectCharHashMap _map;
   // $FF: synthetic field
   final TObjectCharHashMap this$0;

   public TObjectCharHashMap$TObjectCharHashIterator(TObjectCharHashMap var1, TObjectCharHashMap var2) {
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

   public char value() {
      return this._map._values[this._index];
   }

   public char setValue(char var1) {
      char var2 = this.value();
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
