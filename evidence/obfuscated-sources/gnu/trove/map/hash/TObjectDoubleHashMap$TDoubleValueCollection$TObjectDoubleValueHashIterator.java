package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.ba.H_ref;
import gnu.trove.ba.aa;
import gnu.trove.c_ref.y;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

class TObjectDoubleHashMap$TDoubleValueCollection$TObjectDoubleValueHashIterator implements y {
   protected H_ref _hash;
   protected int _expectedSize;
   protected int _index;
   // $FF: synthetic field
   final TObjectDoubleHashMap$TDoubleValueCollection this$1;

   TObjectDoubleHashMap$TDoubleValueCollection$TObjectDoubleValueHashIterator(TObjectDoubleHashMap$TDoubleValueCollection var1) {
      this.this$1 = var1;
      this._hash = TObjectDoubleHashMap$TDoubleValueCollection.access$0(var1);
      this._expectedSize = this._hash.size();
      this._index = this._hash.capacity();
   }

   public boolean hasNext() {
      return this.nextIndex() >= 0;
   }

   public double next() {
      this.moveToNextIndex();
      return TObjectDoubleHashMap$TDoubleValueCollection.access$0(this.this$1)._values[this._index];
   }

   public void remove() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         try {
            this._hash.tempDisableAutoCompaction();
            TObjectDoubleHashMap$TDoubleValueCollection.access$0(this.this$1).removeAt(this._index);
         } finally {
            this._hash.reenableAutoCompaction(false);
         }

         --this._expectedSize;
      }
   }

   protected final void moveToNextIndex() {
      if ((this._index = this.nextIndex()) < 0) {
         throw new NoSuchElementException();
      }
   }

   protected final int nextIndex() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         Object[] var1 = TObjectDoubleHashMap$TDoubleValueCollection.access$0(this.this$1)._set;
         int var2 = this._index;

         while(var2-- > 0 && (var1[var2] == aa.FREE || var1[var2] == aa.REMOVED)) {
         }

         return var2;
      }
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
