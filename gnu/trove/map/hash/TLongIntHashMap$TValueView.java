package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.TIntCollection;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TLongIntHashMap$TValueView implements TIntCollection {
   // $FF: synthetic field
   final TLongIntHashMap this$0;

   protected TLongIntHashMap$TValueView(TLongIntHashMap var1) {
      this.this$0 = var1;
   }

   public TIntIterator iterator() {
      return new TLongIntHashMap$TLongIntValueHashIterator(this.this$0, this.this$0);
   }

   public int getNoEntryValue() {
      return TLongIntHashMap.access$2(this.this$0);
   }

   public int size() {
      return TLongIntHashMap.access$1(this.this$0);
   }

   public boolean isEmpty() {
      return TLongIntHashMap.access$1(this.this$0) == 0;
   }

   public boolean contains(int var1) {
      return this.this$0.containsValue(var1);
   }

   public int[] toArray() {
      return this.this$0.values();
   }

   public int[] toArray(int[] var1) {
      return this.this$0.values(var1);
   }

   public boolean add(int var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(int var1) {
      int[] var2 = this.this$0._values;
      long[] var3 = this.this$0._set;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while(var3[var4] == 0L || var3[var4] == 2L || var1 != var2[var4]);

      this.this$0.removeAt(var4);
      return true;
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3;
         if (!((var3 = var2.next()) instanceof Integer)) {
            return false;
         }

         int var4 = (Integer)var3;
         if (!this.this$0.containsValue(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(TIntCollection var1) {
      TIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsValue(var2.nextInt())) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(int[] var1) {
      int[] var4 = var1;
      int var3 = var1.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         int var5 = var4[var2];
         if (!this.this$0.containsValue(var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(TIntCollection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      TIntIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(TIntCollection var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         TIntIterator var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean retainAll(int[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      int[] var3 = this.this$0._values;
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
         if ((var4 = var3.next()) instanceof Integer) {
            int var5 = (Integer)var4;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   public boolean removeAll(TIntCollection var1) {
      if (this == var1) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         TIterator var4 = var1.iterator();

         while(var4.hasNext()) {
            int var3 = var4.nextInt();
            if (this.remove(var3)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean removeAll(int[] var1) {
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

   public boolean forEach(TIntProcedure var1) {
      return this.this$0.forEachValue(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.this$0.forEachValue(new TLongIntHashMap$TValueView$1(this, var1));
      var1.append("}");
      return var1.toString();
   }
}
