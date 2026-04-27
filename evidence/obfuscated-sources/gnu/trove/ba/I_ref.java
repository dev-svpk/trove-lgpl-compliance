package gnu.trove.ba;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class I_ref implements gnu.trove.c_ref.U_ref, Iterator {
   private final aa _object_hash;
   protected final H_ref _hash;
   protected int _expectedSize;
   protected int _index;

   protected I_ref(aa var1) {
      this._hash = var1;
      this._expectedSize = this._hash.size();
      this._index = this._hash.capacity();
      this._object_hash = var1;
   }

   @Override
   public Object next() {
      this.moveToNextIndex();
      return this.objectAtIndex(this._index);
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

   protected final int nextIndex() {
      if (this._expectedSize != this._hash.size()) {
         throw new ConcurrentModificationException();
      } else {
         Object[] var1 = this._object_hash._set;
         int var2 = this._index;

         while(var2-- > 0 && (var1[var2] == aa.FREE || var1[var2] == aa.REMOVED)) {
         }

         return var2;
      }
   }

   protected abstract Object objectAtIndex(int var1);
}
