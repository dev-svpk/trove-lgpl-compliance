package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.a;
import gnu.trove.c_ref.g;
import gnu.trove.e_ref.h;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

class TObjectByteHashMap$TByteValueCollection implements a {
   // $FF: synthetic field
   final TObjectByteHashMap this$0;

   TObjectByteHashMap$TByteValueCollection(TObjectByteHashMap var1) {
      this.this$0 = var1;
   }

   public g iterator() {
      return new TObjectByteHashMap$TByteValueCollection$TObjectByteValueHashIterator(this);
   }

   public byte getNoEntryValue() {
      return this.this$0.no_entry_value;
   }

   public int size() {
      return TObjectByteHashMap.access$0(this.this$0);
   }

   public boolean isEmpty() {
      return TObjectByteHashMap.access$0(this.this$0) == 0;
   }

   public boolean contains(byte var1) {
      return this.this$0.containsValue(var1);
   }

   public byte[] toArray() {
      return this.this$0.values();
   }

   public byte[] toArray(byte[] var1) {
      return this.this$0.values(var1);
   }

   public boolean add(byte var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(byte var1) {
      byte[] var2 = this.this$0._values;
      Object[] var3 = this.this$0._set;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while(var3[var4] == TObjectByteHashMap.FREE || var3[var4] == TObjectByteHashMap.REMOVED || var1 != var2[var4]);

      this.this$0.removeAt(var4);
      return true;
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3;
         if (!((var3 = var2.next()) instanceof Byte)) {
            return false;
         }

         byte var4 = (Byte)var3;
         if (!this.this$0.containsValue(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(a var1) {
      U_ref var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsValue(var2.nextByte())) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(byte[] var1) {
      byte[] var4 = var1;
      int var3 = var1.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         byte var5 = var4[var2];
         if (!this.this$0.containsValue(var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(a var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(byte[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      g var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(a var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         g var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean retainAll(byte[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      byte[] var3 = this.this$0._values;
      Object[] var4;
      int var5 = (var4 = this.this$0._set).length;

      while(var5-- > 0) {
         if (var4[var5] != TObjectByteHashMap.FREE && var4[var5] != TObjectByteHashMap.REMOVED && Arrays.binarySearch(var1, var3[var5]) < 0) {
            this.this$0.removeAt(var5);
            var2 = true;
         }
      }

      return var2;
   }

   public boolean removeAll(Collection var1) {
      boolean var2 = false;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Object var4;
         if ((var4 = var3.next()) instanceof Byte) {
            byte var5 = (Byte)var4;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   public boolean removeAll(a var1) {
      if (this == var1) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         U_ref var4 = var1.iterator();

         while(var4.hasNext()) {
            byte var3 = var4.nextByte();
            if (this.remove(var3)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean removeAll(byte[] var1) {
      boolean var2 = false;
      int var3 = var1.length;

      while(var3-- > 0) {
         if (this.remove(var1[var3])) {
            var2 = true;
         }
      }

      return var2;
   }

   public void clear() {
      this.this$0.clear();
   }

   public boolean forEach(h var1) {
      return this.this$0.forEachValue(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.this$0.forEachValue(new TObjectByteHashMap$TByteValueCollection$1(this, var1));
      var1.append("}");
      return var1.toString();
   }

   // $FF: synthetic method
   static TObjectByteHashMap access$0(TObjectByteHashMap$TByteValueCollection var0) {
      return var0.this$0;
   }
}
