package gnu.trove.set.hash;

import gnu.trove.impl.hash.TCharHash;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TCharIterator;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCharHashSet extends TCharHash implements gnu.trove.set.TCharSet, Externalizable {
   static final long serialVersionUID = 1L;

   @Override
   public final TCharIterator iterator() {
      return new TCharHashSet$TCharHashIterator(this, this);
   }

   public final boolean add(char var1) {
      if (this.insertKey(var1) < 0) {
         return false;
      } else {
         this.postInsertHook(this.consumeFreeSlot);
         return true;
      }
   }

   public final boolean remove(char var1) {
      int var2;
      if ((var2 = this.index(var1)) >= 0) {
         this.removeAt(var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final void clear() {
      super.clear();
      char[] var1 = this._set;
      byte[] var2 = this._states;

      for(int var3 = var1.length; var3-- > 0; var2[var3] = 0) {
         var1[var3] = this.no_entry_value;
      }

   }

   @Override
   protected final void rehash(int var1) {
      int var2 = this._set.length;
      char[] var3 = this._set;
      byte[] var4 = this._states;
      this._set = new char[var1];
      this._states = new byte[var1];
      var1 = var2;

      while(var1-- > 0) {
         if (var4[var1] == 1) {
            char var5 = var3[var1];
            this.insertKey(var5);
         }
      }

   }

   @Override
   public final boolean equals(Object var1) {
      if (!(var1 instanceof gnu.trove.set.TCharSet)) {
         return false;
      } else {
         gnu.trove.set.TCharSet var3;
         if ((var3 = (gnu.trove.set.TCharSet)var1).size() != this.size()) {
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

   @Override
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

   //@Override
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

   @Override
   public final void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(1);
           super.writeExternal(var1);
           var1.writeInt(this._size);
           var1.writeFloat(this._loadFactor);
           var1.writeChar(this.no_entry_value);
           int var2 = this._states.length;

           while(var2-- > 0) {
               if (this._states[var2] == 1) {
                   var1.writeChar(this._set[var2]);
               }
           }} catch (IOException ex) {
           Logger.getLogger(TCharHashSet.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   @Override
   public final void readExternal(ObjectInput var1) {
       try {
           byte var2 = var1.readByte();
           super.readExternal(var1);
           int var3 = var1.readInt();
           if (var2 > 0) {
               this._loadFactor = var1.readFloat();
               this.no_entry_value = var1.readChar();
               if (this.no_entry_value != 0) {
                   Arrays.fill(this._set, this.no_entry_value);
               }
           }

           this.setUp(var3);

           while(var3-- > 0) {
               char var4 = var1.readChar();
               this.add(var4);
           }} catch (IOException ex) {
           Logger.getLogger(TCharHashSet.class.getName()).log(Level.SEVERE, null, ex);
       }

   }
}
