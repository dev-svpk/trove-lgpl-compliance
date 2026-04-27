package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.f;
import gnu.trove.c_ref.aa;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

class TObjectLongHashMap$TLongValueCollection implements f {
   // $FF: synthetic field
   final TObjectLongHashMap this$0;

   TObjectLongHashMap$TLongValueCollection(TObjectLongHashMap var1) {
      this.this$0 = var1;
   }

   public aa iterator() {
      return new TObjectLongHashMap$TLongValueCollection$TObjectLongValueHashIterator(this);
   }

   public long getNoEntryValue() {
      return this.this$0.no_entry_value;
   }

   public int size() {
      return TObjectLongHashMap.access$0(this.this$0);
   }

   public boolean isEmpty() {
      return TObjectLongHashMap.access$0(this.this$0) == 0;
   }

   public boolean contains(long var1) {
      return this.this$0.containsValue(var1);
   }

   public long[] toArray() {
      return this.this$0.values();
   }

   public long[] toArray(long[] var1) {
      return this.this$0.values(var1);
   }

   public boolean add(long var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(long var1) {
      long[] var3 = this.this$0._values;
      Object[] var4 = this.this$0._set;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return false;
         }
      } while(var4[var5] == TObjectLongHashMap.FREE || var4[var5] == TObjectLongHashMap.REMOVED || var1 != var3[var5]);

      this.this$0.removeAt(var5);
      return true;
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var6;
         if (!((var6 = var2.next()) instanceof Long)) {
            return false;
         }

         long var4 = (Long)var6;
         if (!this.this$0.containsValue(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(f var1) {
      U_ref var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsValue(var2.nextLong())) {
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
         if (!this.this$0.containsValue(var2)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(f var1) {
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

   public boolean retainAll(f var1) {
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
      long[] var3 = this.this$0._values;
      Object[] var4;
      int var5 = (var4 = this.this$0._set).length;

      while(var5-- > 0) {
         if (var4[var5] != TObjectLongHashMap.FREE && var4[var5] != TObjectLongHashMap.REMOVED && Arrays.binarySearch(var1, var3[var5]) < 0) {
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

   public boolean removeAll(f var1) {
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
      return this.this$0.forEachValue(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.this$0.forEachValue(new TObjectLongHashMap$TLongValueCollection$1(this, var1));
      var1.append("}");
      return var1.toString();
   }

   // $FF: synthetic method
   static TObjectLongHashMap access$0(TObjectLongHashMap$TLongValueCollection var0) {
      return var0.this$0;
   }
}
