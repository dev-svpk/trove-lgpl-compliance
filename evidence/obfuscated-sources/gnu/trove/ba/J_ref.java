package gnu.trove.ba;

import gnu.trove.c_ref.al;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public abstract class J_ref implements al {
   protected final ab _hash;
   protected int _expectedSize;
   protected int _index;

   public J_ref(ab var1) {
      this._hash = var1;
      this._expectedSize = this._hash.size();
      this._index = this._hash.capacity();
   }

   protected final int nextIndex() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         byte[] var1 = this._hash._states;
         int var2 = this._index;

         while(var2-- > 0 && var1[var2] != 1) {
         }

         return var2;
      }
   }

   @Override
   public boolean hasNext() {
      return this.nextIndex() >= 0;
   }

   @Override
   public void remove() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         try {
            this._hash.tempDisableAutoCompaction();
            this._hash.removeAt(this._index);
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
}
