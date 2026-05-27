package gnu.trove.list.array;

import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TIntArrayList implements TIntCollection, Externalizable {
   static final long serialVersionUID = 1L;
   private int[] _data;
   private int _pos;
   private int no_entry_value;

   public TIntArrayList() {
      this(10, 0);
   }

   private TIntArrayList(int var1, int var2) {
      this._data = new int[10];
      this._pos = 0;
      this.no_entry_value = 0;
   }

   public final int size() {
      return this._pos;
   }

   public final boolean add(int var1) {
      int var3 = this._pos + 1;
      if (var3 > this._data.length) {
         int[] var4 = new int[Math.max(this._data.length << 1, var3)];
         System.arraycopy(this._data, 0, var4, 0, this._data.length);
         this._data = var4;
      }

      this._data[this._pos++] = var1;
      return true;
   }

   public final int get(int var1) {
      if (var1 >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         return this._data[var1];
      }
   }

   public final void remove(int var1, int var2) {
      if (var1 >= 0 && var1 < this._pos) {
         if (var1 == 0) {
            System.arraycopy(this._data, 1, this._data, 0, this._pos - 1);
         } else if (this._pos - 1 != var1) {
            System.arraycopy(this._data, var1 + 1, this._data, var1, this._pos - (var1 + 1));
         }

         --this._pos;
      } else {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   public final TIntIterator iterator() {
      return new TIntArrayList$TIntArrayIterator(this, 0);
   }

   public final void reverse() {
      int var2 = this._pos;
      boolean var1 = false;
      TIntArrayList var8 = this;
      if (var2 != 0) {
         if (var2 < 0) {
            throw new IllegalArgumentException("from cannot be greater than to");
         } else {
            int var3 = 0;
            --var2;

            while(var3 < var2) {
               int var7 = var8._data[var3];
               int[] var10002 = var8._data;
               var10002[var3] = var10002[var2];
               var8._data[var2] = var7;
               ++var3;
               --var2;
            }

         }
      }
   }

   public final int[] toArray() {
      int var2 = this._pos;
      boolean var1 = false;
      int[] var3 = new int[var2];
      byte var4 = 0;
      if (var2 != 0) {
         if (var4 < 0 || var4 >= this._pos) {
            throw new ArrayIndexOutOfBoundsException(var4);
         }

         System.arraycopy(this._data, var4, var3, 0, var2);
      }

      return var3;
   }

   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TIntArrayList)) {
         return false;
      } else {
         TIntArrayList var3;
         if ((var3 = (TIntArrayList)var1)._pos != this._pos) {
            return false;
         } else {
            int var2 = this._pos;

            while(var2-- > 0) {
               if (this._data[var2] != var3._data[var2]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public final int hashCode() {
      int var1 = 0;

      for(int var2 = this._pos; var2-- > 0; var1 += gnu.trove.impl.HashFunctions.hash(this._data[var2])) {
      }

      return var1;
   }

   public final void sort() {
      Arrays.sort(this._data, 0, this._pos);
   }

   public final boolean contains(int var1) {
      int var3 = var1;
      int var2 = this._pos;
      TIntArrayList var4 = this;
      var2 = var2;

      int var10000;
      while(true) {
         if (var2-- <= 0) {
            var10000 = -1;
            break;
         }

         if (var4._data[var2] == var3) {
            var10000 = var2;
            break;
         }
      }

      return var10000 >= 0;
   }

   public final String toString() {
      StringBuilder var1 = new StringBuilder("{");
      int var2 = 0;

      for(int var3 = this._pos - 1; var2 < var3; ++var2) {
         var1.append(this._data[var2]);
         var1.append(", ");
      }

      if (this._pos > 0) {
         var1.append(this._data[this._pos - 1]);
      }

      var1.append("}");
      return var1.toString();
   }

   public final void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(0);
           var1.writeInt(this._pos);
           var1.writeInt(this.no_entry_value);
           int var2 = this._data.length;
           var1.writeInt(var2);

           for(int var3 = 0; var3 < var2; ++var3) {
               var1.writeInt(this._data[var3]);
           }} catch (IOException ex) {
           Logger.getLogger(TIntArrayList.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   public final void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           this._pos = var1.readInt();
           this.no_entry_value = var1.readInt();
           int var2 = var1.readInt();
           this._data = new int[var2];

           for(int var3 = 0; var3 < var2; ++var3) {
               this._data[var3] = var1.readInt();
           }} catch (IOException ex) {
           Logger.getLogger(TIntArrayList.class.getName()).log(Level.SEVERE, null, ex);
       }

   }
}
