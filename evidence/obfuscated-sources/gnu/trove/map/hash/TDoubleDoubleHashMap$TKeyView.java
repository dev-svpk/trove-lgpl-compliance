package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.b_ref.b;
import gnu.trove.c_ref.y;
import gnu.trove.e_ref.z;
import gnu.trove.f_ref.c;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class TDoubleDoubleHashMap$TKeyView implements c {
   // $FF: synthetic field
   final TDoubleDoubleHashMap this$0;

   protected TDoubleDoubleHashMap$TKeyView(TDoubleDoubleHashMap var1) {
      this.this$0 = var1;
   }

   public y iterator() {
      return new TDoubleDoubleHashMap$TDoubleDoubleKeyHashIterator(this.this$0, this.this$0);
   }

   public double getNoEntryValue() {
      return TDoubleDoubleHashMap.access$0(this.this$0);
   }

   public int size() {
      return TDoubleDoubleHashMap.access$1(this.this$0);
   }

   public boolean isEmpty() {
      return TDoubleDoubleHashMap.access$1(this.this$0) == 0;
   }

   public boolean contains(double var1) {
      return this.this$0.contains(var1);
   }

   public double[] toArray() {
      return this.this$0.keys();
   }

   public double[] toArray(double[] var1) {
      return this.this$0.keys(var1);
   }

   public boolean add(double var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(double var1) {
      return TDoubleDoubleHashMap.access$2(this.this$0) != this.this$0.remove(var1);
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var6;
         if (!((var6 = var2.next()) instanceof Double)) {
            return false;
         }

         double var4 = (Double)var6;
         if (!this.this$0.containsKey(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(gnu.trove.c var1) {
      U_ref var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsKey(var2.nextDouble())) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(double[] var1) {
      double[] var5 = var1;
      int var4 = var1.length;

      for(int var6 = 0; var6 < var4; ++var6) {
         double var2 = var5[var6];
         if (!this.this$0.contains(var2)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(gnu.trove.c var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(double[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      y var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(gnu.trove.c var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         y var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean retainAll(double[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      double[] var3 = this.this$0._set;
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
         if ((var7 = var3.next()) instanceof Double) {
            double var5 = (Double)var7;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   public boolean removeAll(gnu.trove.c var1) {
      if (this == var1) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         U_ref var6 = var1.iterator();

         while(var6.hasNext()) {
            double var4 = var6.nextDouble();
            if (this.remove(var4)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean removeAll(double[] var1) {
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

   public boolean forEach(z var1) {
      return this.this$0.forEachKey(var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof c)) {
         return false;
      } else {
         c var3;
         if ((var3 = (c)var1).size() != this.size()) {
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
      this.this$0.forEachKey(new TDoubleDoubleHashMap$TKeyView$1(this, var1));
      var1.append("}");
      return var1.toString();
   }
}
