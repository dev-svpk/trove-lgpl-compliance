package gnu.trove.list.array;

import gnu.trove.iterator.TIntIterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

final class TIntArrayList$TIntArrayIterator implements TIntIterator {
   private int _cursor;
   private int _lastReturned;
   // $FF: synthetic field
   private TIntArrayList this$0;

   TIntArrayList$TIntArrayIterator(TIntArrayList var1, int var2) {
      super();
      this.this$0 = var1;
      this._cursor = 0;
      this._lastReturned = -1;
      this._cursor = 0;
   }

   @Override
   public final boolean hasNext() {
      return this._cursor < this.this$0.size();
   }

   @Override
   public final int next() {
      try {
         int var1 = this.this$0.get(this._cursor);
         this._lastReturned = this._cursor++;
         return var1;
      } catch (IndexOutOfBoundsException var2) {
         throw new NoSuchElementException();
      }
   }

   @Override
   public final void remove() {
      if (this._lastReturned == -1) {
         throw new IllegalStateException();
      } else {
         try {
            this.this$0.remove(this._lastReturned, 1);
            if (this._lastReturned < this._cursor) {
               --this._cursor;
            }

            this._lastReturned = -1;
         } catch (IndexOutOfBoundsException var1) {
            throw new ConcurrentModificationException();
         }
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
