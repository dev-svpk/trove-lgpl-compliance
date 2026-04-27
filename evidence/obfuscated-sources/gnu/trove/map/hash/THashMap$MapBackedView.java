package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

abstract class THashMap$MapBackedView extends AbstractSet implements Iterable, Set {
   // $FF: synthetic field
   final THashMap this$0;

   private THashMap$MapBackedView(THashMap var1) {
      this.this$0 = var1;
   }

   public abstract Iterator iterator();

   public abstract boolean removeElement(Object var1);

   public abstract boolean containsElement(Object var1);

   public boolean contains(Object var1) {
      return this.containsElement(var1);
   }

   public boolean remove(Object var1) {
      try {
         return this.removeElement(var1);
      } catch (ClassCastException var2) {
         return false;
      }
   }

   public void clear() {
      this.this$0.clear();
   }

   public boolean add(Object var1) {
      throw new UnsupportedOperationException();
   }

   public int size() {
      return this.this$0.size();
   }

   public Object[] toArray() {
      Object[] var1 = new Object[this.size()];
      Iterator var2 = this.iterator();

      for(int var3 = 0; var2.hasNext(); ++var3) {
         var1[var3] = var2.next();
      }

      return var1;
   }

   public Object[] toArray(Object[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = (Object[])Array.newInstance(var1.getClass().getComponentType(), var2);
      }

      Iterator var3 = this.iterator();
      Object[] var4 = var1;

      for(int var5 = 0; var5 < var2; ++var5) {
         var4[var5] = var3.next();
      }

      if (var1.length > var2) {
         var1[var2] = null;
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.this$0.isEmpty();
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      boolean var2 = false;
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         if (!var1.contains(var3.next())) {
            var3.remove();
            var2 = true;
         }
      }

      return var2;
   }

   public String toString() {
      Iterator var1;
      if (!(var1 = this.iterator()).hasNext()) {
         return "{}";
      } else {
         StringBuilder var2;
         (var2 = new StringBuilder()).append('{');

         while(true) {
            Object var3 = var1.next();
            var2.append(var3 == this ? "(this Collection)" : var3);
            if (!var1.hasNext()) {
               return var2.append('}').toString();
            }

            var2.append(", ");
         }
      }
   }

   // $FF: synthetic method
   THashMap$MapBackedView(THashMap var1, THashMap$MapBackedView var2) {
      this(var1);
   }
}
