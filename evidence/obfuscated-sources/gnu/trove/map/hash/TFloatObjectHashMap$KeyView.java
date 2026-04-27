package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.b_ref.b;
import gnu.trove.c_ref.H_ref;
import gnu.trove.e_ref.I_ref;
import gnu.trove.f_ref.d;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

class TFloatObjectHashMap$KeyView implements d {
   // $FF: synthetic field
   final TFloatObjectHashMap this$0;

   TFloatObjectHashMap$KeyView(TFloatObjectHashMap var1) {
      this.this$0 = var1;
   }

   public float getNoEntryValue() {
      return this.this$0.no_entry_key;
   }

   public int size() {
      return TFloatObjectHashMap.access$0(this.this$0);
   }

   public boolean isEmpty() {
      return TFloatObjectHashMap.access$0(this.this$0) == 0;
   }

   public boolean contains(float var1) {
      return this.this$0.containsKey(var1);
   }

   public H_ref iterator() {
      return new TFloatObjectHashMap$KeyView$TFloatHashIterator(this, this.this$0);
   }

   public float[] toArray() {
      return this.this$0.keys();
   }

   public float[] toArray(float[] var1) {
      return this.this$0.keys(var1);
   }

   public boolean add(float var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(float var1) {
      return this.this$0.remove(var1) != null;
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (!this.this$0.containsKey((Float)var3)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(gnu.trove.d var1) {
      if (var1 == this) {
         return true;
      } else {
         U_ref var2 = var1.iterator();

         while(var2.hasNext()) {
            if (!this.this$0.containsKey(var2.nextFloat())) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean containsAll(float[] var1) {
      float[] var4 = var1;
      int var3 = var1.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         float var5 = var4[var2];
         if (!this.this$0.containsKey(var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(gnu.trove.d var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(float[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      H_ref var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(gnu.trove.d var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         H_ref var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean retainAll(float[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      float[] var3 = this.this$0._set;
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
         if ((var4 = var3.next()) instanceof Float) {
            float var5 = (Float)var4;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   public boolean removeAll(gnu.trove.d var1) {
      if (var1 == this) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         U_ref var4 = var1.iterator();

         while(var4.hasNext()) {
            float var3 = var4.nextFloat();
            if (this.remove(var3)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean removeAll(float[] var1) {
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

   public boolean forEach(I_ref var1) {
      return this.this$0.forEachKey(var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof d)) {
         return false;
      } else {
         d var3;
         if ((var3 = (d)var1).size() != this.size()) {
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
      boolean var2 = true;
      int var3 = this.this$0._states.length;

      while(var3-- > 0) {
         if (this.this$0._states[var3] == 1) {
            if (var2) {
               var2 = false;
            } else {
               var1.append(",");
            }

            var1.append(this.this$0._set[var3]);
         }
      }

      return var1.toString();
   }
}
