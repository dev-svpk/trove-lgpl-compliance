package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.b_ref.b;
import gnu.trove.c_ref.aa;
import gnu.trove.f_ref.f;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TLongIntHashMap$TKeyView implements f {
   // $FF: synthetic field
   final TLongIntHashMap this$0;

   protected TLongIntHashMap$TKeyView(TLongIntHashMap var1) {
      this.this$0 = var1;
   }

   public aa iterator() {
      return new TLongIntHashMap$TLongIntKeyHashIterator(this.this$0, this.this$0);
   }

   public long getNoEntryValue() {
      return TLongIntHashMap.access$0(this.this$0);
   }

   public int size() {
      return TLongIntHashMap.access$1(this.this$0);
   }

   public boolean isEmpty() {
      return TLongIntHashMap.access$1(this.this$0) == 0;
   }

   public boolean contains(long var1) {
      return this.this$0.contains(var1);
   }

   public long[] toArray() {
      return this.this$0.keys();
   }

   public long[] toArray(long[] var1) {
      return this.this$0.keys(var1);
   }

   public boolean add(long var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(long var1) {
      return TLongIntHashMap.access$2(this.this$0) != this.this$0.remove(var1);
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var6;
         if (!((var6 = var2.next()) instanceof Long)) {
            return false;
         }

         long var4 = (Long)var6;
         if (!this.this$0.containsKey(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(gnu.trove.f var1) {
      U_ref var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsKey(var2.nextLong())) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(long[] var1) {
      long[] var5 = var1;
      int var4 = var1.length;

      for(int var6 = 0; var6 < var4; ++var6) {
         long var2 = var5[var6];
         if (!this.this$0.contains(var2)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(gnu.trove.f var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(long[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      aa var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(gnu.trove.f var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         aa var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean retainAll(long[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      long[] var3 = this.this$0._set;
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
         Object var7;
         if ((var7 = var3.next()) instanceof Long) {
            long var5 = (Long)var7;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   public boolean removeAll(gnu.trove.f var1) {
      if (this == var1) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         U_ref var6 = var1.iterator();

         while(var6.hasNext()) {
            long var4 = var6.nextLong();
            if (this.remove(var4)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean removeAll(long[] var1) {
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

   public boolean forEach(gnu.trove.e_ref.aa var1) {
      return this.this$0.forEachKey(var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof f)) {
         return false;
      } else {
         f var3;
         if ((var3 = (f)var1).size() != this.size()) {
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
      this.this$0.forEachKey(new TLongIntHashMap$TKeyView$1(this, var1));
      var1.append("}");
      return var1.toString();
   }
}
