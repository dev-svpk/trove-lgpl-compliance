package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.TCharCollection;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.procedure.TCharProcedure;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

class TObjectCharHashMap$TCharValueCollection implements TCharCollection {
   // $FF: synthetic field
   final TObjectCharHashMap this$0;

   TObjectCharHashMap$TCharValueCollection(TObjectCharHashMap var1) {
      this.this$0 = var1;
   }

   public TCharIterator iterator() {
      return new TObjectCharHashMap$TCharValueCollection$TObjectCharValueHashIterator(this);
   }

   public char getNoEntryValue() {
      return this.this$0.no_entry_value;
   }

   public int size() {
      return TObjectCharHashMap.access$0(this.this$0);
   }

   public boolean isEmpty() {
      return TObjectCharHashMap.access$0(this.this$0) == 0;
   }

   public boolean contains(char var1) {
      return this.this$0.containsValue(var1);
   }

   public char[] toArray() {
      return this.this$0.values();
   }

   public char[] toArray(char[] var1) {
      return this.this$0.values(var1);
   }

   public boolean add(char var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(char var1) {
      char[] var2 = this.this$0._values;
      Object[] var3 = this.this$0._set;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while(var3[var4] == TObjectCharHashMap.FREE || var3[var4] == TObjectCharHashMap.REMOVED || var1 != var2[var4]);

      this.this$0.removeAt(var4);
      return true;
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3;
         if (!((var3 = var2.next()) instanceof Character)) {
            return false;
         }

         char var4 = (Character)var3;
         if (!this.this$0.containsValue(var4)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(TCharCollection var1) {
      TIterator var2 = var1.iterator();

      while(var2.hasNext()) {
         if (!this.this$0.containsValue(var2.nextChar())) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(char[] var1) {
      char[] var4 = var1;
      int var3 = var1.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         char var5 = var4[var2];
         if (!this.this$0.containsValue(var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(TCharCollection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(char[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      TCharIterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public boolean retainAll(TCharCollection var1) {
      if (this == var1) {
         return false;
      } else {
         boolean var2 = false;
         TCharIterator var3 = this.iterator();

         while(var3.hasNext()) {
            if (!var1.contains(var3.next())) {
               var3.remove();
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean retainAll(char[] var1) {
      boolean var2 = false;
      Arrays.sort(var1);
      char[] var3 = this.this$0._values;
      Object[] var4;
      int var5 = (var4 = this.this$0._set).length;

      while(var5-- > 0) {
         if (var4[var5] != TObjectCharHashMap.FREE && var4[var5] != TObjectCharHashMap.REMOVED && Arrays.binarySearch(var1, var3[var5]) < 0) {
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
         if ((var4 = var3.next()) instanceof Character) {
            char var5 = (Character)var4;
            if (this.remove(var5)) {
               var2 = true;
            }
         }
      }

      return var2;
   }

   public boolean removeAll(TCharCollection var1) {
      if (this == var1) {
         this.clear();
         return true;
      } else {
         boolean var2 = false;
         TIterator var4 = var1.iterator();

         while(var4.hasNext()) {
            char var3 = var4.nextChar();
            if (this.remove(var3)) {
               var2 = true;
            }
         }

         return var2;
      }
   }

   public boolean removeAll(char[] var1) {
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

   public boolean forEach(TCharProcedure var1) {
      return this.this$0.forEachValue(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.this$0.forEachValue(new TObjectCharHashMap$TCharValueCollection$1(this, var1));
      var1.append("}");
      return var1.toString();
   }

   // $FF: synthetic method
   static TObjectCharHashMap access$0(TObjectCharHashMap$TCharValueCollection var0) {
      return var0.this$0;
   }
}
