package gnu.trove.set.hash;

import gnu.trove.impl.hash.TIntHash;
import gnu.trove.iterator.TIntIterator;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TIntHashSet extends TIntHash implements gnu.trove.set.TIntSet, Externalizable {
   static final long serialVersionUID = 1L;

   public TIntHashSet() {
   }

   public TIntHashSet(int var1) {
      super(5);
   }

   @Override
   public final TIntIterator iterator() {
      return new TIntHashSet$TIntHashIterator(this, this);
   }

   public final boolean add(int var1) {
      if (this.insertKey(var1) < 0) {
         return false;
      } else {
         this.postInsertHook(this.consumeFreeSlot);
         return true;
      }
   }

   public final void clear() {
      super.clear();
      int[] var1 = this._set;
      byte[] var2 = this._states;

      for(int var3 = var1.length; var3-- > 0; var2[var3] = 0) {
         var1[var3] = this.no_entry_value;
      }

   }

   protected final void rehash(int var1) {
      int var2 = this._set.length;
      int[] var3 = this._set;
      byte[] var4 = this._states;
      this._set = new int[var1];
      this._states = new byte[var1];
      var1 = var2;

      while(var1-- > 0) {
         if (var4[var1] == 1) {
            var2 = var3[var1];
            this.insertKey(var2);
         }
      }

   }

   public final boolean equals(Object var1) {
      if (!(var1 instanceof gnu.trove.set.TIntSet)) {
         return false;
      } else {
         gnu.trove.set.TIntSet var3;
         if ((var3 = (gnu.trove.set.TIntSet)var1).size() != this.size()) {
            return false;
         } else {
            int var2 = this._states.length;

            do {
               if (var2-- <= 0) {
                  return true;
               }
            } while(this._states[var2] != 1 || var3.contains(this._set[var2]));

            return false;
         }
      }
   }

   public final int hashCode() {
      int var1 = 0;
      int var2 = this._states.length;

      while(var2-- > 0) {
         if (this._states[var2] == 1) {
            var1 += gnu.trove.impl.HashFunctions.hash(this._set[var2]);
         }
      }

      return var1;
   }

   public final String toString() {
      StringBuilder var1;
      (var1 = new StringBuilder((this._size << 1) + 2)).append("{");
      int var2 = this._states.length;
      int var3 = 1;

      while(var2-- > 0) {
         if (this._states[var2] == 1) {
            var1.append(this._set[var2]);
            if (var3++ < this._size) {
               var1.append(",");
            }
         }
      }

      var1.append("}");
      return var1.toString();
   }

   public final void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(1);
           super.writeExternal(var1);
           var1.writeInt(this._size);
           var1.writeFloat(this._loadFactor);
           var1.writeInt(this.no_entry_value);
           int var2 = this._states.length;

           while(var2-- > 0) {
               if (this._states[var2] == 1) {
                   var1.writeInt(this._set[var2]);
               }
           }} catch (IOException ex) {
           Logger.getLogger(TIntHashSet.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   public final void readExternal(ObjectInput var1) {
       try {
           byte var2 = var1.readByte();
           super.readExternal(var1);
           int var3 = var1.readInt();
           if (var2 > 0) {
               this._loadFactor = var1.readFloat();
               this.no_entry_value = var1.readInt();
               if (this.no_entry_value != 0) {
                   Arrays.fill(this._set, this.no_entry_value);
               }
           }

           this.setUp(var3);

           while(var3-- > 0) {
               int var4 = var1.readInt();
               this.add(var4);
           }} catch (IOException ex) {
           Logger.getLogger(TIntHashSet.class.getName()).log(Level.SEVERE, null, ex);
       }

   }
}
