package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.TShortCollection;
import gnu.trove.iterator.TShortIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TLongShortHashMap$TValueView implements TShortCollection {
   // $FF: synthetic field
   final TLongShortHashMap this$0;

   protected TLongShortHashMap$TValueView(TLongShortHashMap var1) {
      this.this$0 = var1;
   }

   public TShortIterator iterator() {
      return new TLongShortHashMap$TLongShortValueHashIterator(this.this$0, this.this$0);
   }

   public short getNoEntryValue() {
      return TLongShortHashMap.access$2(this.this$0);
   }

   public int size() {
      return TLongShortHashMap.access$1(this.this$0);
   }

   public boolean isEmpty() {
      return TLongShortHashMap.access$1(this.this$0) == 0;
   }

   public boolean contains(short var1) {
      return this.this$0.containsValue(var1);
   }

   public short[] toArray() {
      return this.this$0.values();
   }

   public short[] toArray(short[] var1) {
      return this.this$0.values(var1);
   }

   public boolean add(short var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(short var1) {
      short[] var2 = this.this$0._values;
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
         if (!((var3 = var2.next()) instanceof Short)) {
            return false;
         }

         short var4 = (Short)var3;
         if (!this.this$0.containsValue(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(TShortCollection var1) {
      TIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsValue(var2.nextShort())) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(short[] var1) {
      short[] var4 = var1;
      int var3 = var1.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         short var5 = var4[var2];
         if (!this.this$0.containsValue(var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(TShortCollection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(short[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      TShortIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(TShortCollection var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         TShortIterator var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean retainAll(short[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      short[] var3 = this.this$0._values;
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
         if ((var4 = var3.next()) instanceof Short) {
            short var5 = (Short)var4;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   public boolean removeAll(TShortCollection var1) {
      if (this == var1) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         TIterator var4 = var1.iterator();

         while(var4.hasNext()) {
            short var3 = var4.nextShort();
            if (this.remove(var3)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean removeAll(short[] var1) {
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

   public boolean forEach(gnu.trove.procedure.TShortProcedure var1) {
      return this.this$0.forEachValue(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.this$0.forEachValue(new TLongShortHashMap$TValueView$1(this, var1));
      var1.append("}");
      return var1.toString();
   }
}
