package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.b_ref.b;
import gnu.trove.c_ref.g;
import gnu.trove.e_ref.h;
import gnu.trove.f_ref.a;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TByteDoubleHashMap$TKeyView implements a {
   // $FF: synthetic field
   final TByteDoubleHashMap this$0;

   protected TByteDoubleHashMap$TKeyView(TByteDoubleHashMap var1) {
      this.this$0 = var1;
   }

   public g iterator() {
      return new TByteDoubleHashMap$TByteDoubleKeyHashIterator(this.this$0, this.this$0);
   }

   public byte getNoEntryValue() {
      return TByteDoubleHashMap.access$0(this.this$0);
   }

   public int size() {
      return TByteDoubleHashMap.access$1(this.this$0);
   }

   public boolean isEmpty() {
      return TByteDoubleHashMap.access$1(this.this$0) == 0;
   }

   public boolean contains(byte var1) {
      return this.this$0.contains(var1);
   }

   public byte[] toArray() {
      return this.this$0.keys();
   }

   public byte[] toArray(byte[] var1) {
      return this.this$0.keys(var1);
   }

   public boolean add(byte var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(byte var1) {
      return TByteDoubleHashMap.access$2(this.this$0) != this.this$0.remove(var1);
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3;
         if (!((var3 = var2.next()) instanceof Byte)) {
            return false;
         }

         byte var4 = (Byte)var3;
         if (!this.this$0.containsKey(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(gnu.trove.a var1) {
      U_ref var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsKey(var2.nextByte())) {
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
         if (!this.this$0.contains(var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(gnu.trove.a var1) {
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

   public boolean retainAll(gnu.trove.a var1) {
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
      byte[] var3 = this.this$0._set;
      byte[] var4 = this.this$0._states;
      int var5 = var3.length;

      while(var5-- > 0) {
         if (var4[var5] == 1 && Arrays.binarySearch(var1, var3[var5]) < 0) {
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

   public boolean removeAll(gnu.trove.a var1) {
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
      return this.this$0.forEachKey(var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof a)) {
         return false;
      } else {
         a var3;
         if ((var3 = (a)var1).size() != this.size()) {
            return false;
         } else {
            int var2 = this.this$0._states.length;

            do {
               if (var2-- <= 0) {
                  return true;
               }
            } while(this.this$0._states[var2] != 1 || var3.contains(this.this$0._set[var2]));

            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      int var2 = this.this$0._states.length;

      while(var2-- > 0) {
         if (this.this$0._states[var2] == 1) {
            var1 += b.a(this.this$0._set[var2]);
         }
      }

      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.this$0.forEachKey(new TByteDoubleHashMap$TKeyView$1(this, var1));
      var1.append("}");
      return var1.toString();
   }
}
