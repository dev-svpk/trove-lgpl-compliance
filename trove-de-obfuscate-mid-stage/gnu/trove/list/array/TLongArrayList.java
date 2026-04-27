package gnu.trove.list.array;

import gnu.trove.TLongCollection;
import gnu.trove.iterator.TLongIterator;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TLongArrayList implements TLongCollection, Externalizable {
   static final long serialVersionUID = 1L;
   private long[] _data;
   private int _pos;
   private long no_entry_value;

   public TLongArrayList() {
      this(10, 0L);
   }

   private TLongArrayList(int var1, long var2) {
      this._data = new long[10];
      this._pos = 0;
      this.no_entry_value = 0L;
   }

   public final int size() {
      return this._pos;
   }

   public final boolean add(long var1) {
      int var4 = this._pos + 1;
      if (var4 > this._data.length) {
         long[] var5 = new long[Math.max(this._data.length << 1, var4)];
         System.arraycopy(this._data, 0, var5, 0, this._data.length);
         this._data = var5;
      }

      this._data[this._pos++] = var1;
      return true;
   }

   public final long get(int var1) {
      if (var1 >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         return this._data[var1];
      }
   }

   public final void clear() {
      this._pos = 0;
      Arrays.fill(this._data, this.no_entry_value);
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

   public final TLongIterator iterator() {
      return new TLongArrayList$TLongArrayIterator(this, 0);
   }

   @Override
   public final boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TLongArrayList)) {
         return false;
      } else {
         TLongArrayList var3;
         if ((var3 = (TLongArrayList)var1)._pos != this._pos) {
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

   @Override
   public final int hashCode() {
      int var1 = 0;

      for(int var2 = this._pos; var2-- > 0; var1 += gnu.trove.impl.HashFunctions.hash(this._data[var2])) {
      }

      return var1;
   }

   public final boolean contains(long var1) {
      long var8 = var1;
      int var2 = this._pos;
      TLongArrayList var10 = this;
      var2 = var2;

      int var10000;
      while(true) {
         if (var2-- <= 0) {
            var10000 = -1;
            break;
         }

         if (var10._data[var2] == var8) {
            var10000 = var2;
            break;
         }
      }

      return var10000 >= 0;
   }

   @Override
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

   @Override
   public final void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(0);
           var1.writeInt(this._pos);
           var1.writeLong(this.no_entry_value);
           int var2 = this._data.length;
           var1.writeInt(var2);

           for(int var3 = 0; var3 < var2; ++var3) {
               var1.writeLong(this._data[var3]);
           }} catch (IOException ex) {
           Logger.getLogger(TLongArrayList.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   @Override
   public final void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           this._pos = var1.readInt();
           this.no_entry_value = var1.readLong();
           int var2 = var1.readInt();
           this._data = new long[var2];

           for(int var3 = 0; var3 < var2; ++var3) {
               this._data[var3] = var1.readLong();
           }} catch (IOException ex) {
           Logger.getLogger(TLongArrayList.class.getName()).log(Level.SEVERE, null, ex);
       }

   }
}
