package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

class TShortObjectHashMap$KeyView implements TShortSet {
   // $FF: synthetic field
   final TShortObjectHashMap this$0;

   TShortObjectHashMap$KeyView(TShortObjectHashMap var1) {
      this.this$0 = var1;
   }

   public short getNoEntryValue() {
      return this.this$0.no_entry_key;
   }

   public int size() {
      return TShortObjectHashMap.access$0(this.this$0);
   }

   public boolean isEmpty() {
      return TShortObjectHashMap.access$0(this.this$0) == 0;
   }

   public boolean contains(short var1) {
      return this.this$0.containsKey(var1);
   }

   public TShortIterator iterator() {
      return new TShortObjectHashMap$KeyView$TShortHashIterator(this, this.this$0);
   }

   public short[] toArray() {
      return this.this$0.keys();
   }

   public short[] toArray(short[] var1) {
      return this.this$0.keys(var1);
   }

   public boolean add(short var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(short var1) {
      return this.this$0.remove(var1) != null;
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         if (!this.this$0.containsKey((Short)var3)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(gnu.trove.TShortCollection var1) {
      if (var1 == this) {
         return true;
      } else {
         TIterator var2 = var1.iterator();

         while(var2.hasNext()) {
            if (!this.this$0.containsKey(var2.nextShort())) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean containsAll(short[] var1) {
      short[] var4 = var1;
      int var3 = var1.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         short var5 = var4[var2];
         if (!this.this$0.containsKey(var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(gnu.trove.TShortCollection var1) {
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

   public boolean retainAll(gnu.trove.TShortCollection var1) {
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
      short[] var3 = this.this$0._set;
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

   public boolean removeAll(gnu.trove.TShortCollection var1) {
      if (var1 == this) {
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
      return this.this$0.forEachKey(var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof TShortSet)) {
         return false;
      } else {
         TShortSet var3;
         if ((var3 = (TShortSet)var1).size() != this.size()) {
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
            var1 += HashFunctions.hash(this.this$0._set[var2]);
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
