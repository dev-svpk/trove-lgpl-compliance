package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.Iterator;
import java.util.Map.Entry;

public class THashMap$EntryView extends THashMap$MapBackedView {
   // $FF: synthetic field
   final THashMap this$0;

   protected THashMap$EntryView(THashMap var1) {
      super(var1, (THashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new THashMap$EntryView$EntryIterator(this, this.this$0);
   }

   public boolean removeElement(Entry var1) {
      if (var1 == null) {
         return false;
      } else {
         Object var2 = this.keyForEntry(var1);
         Object var3;
         int var4;
         if ((var4 = THashMap.access$1(this.this$0, var2)) < 0 || (var3 = this.valueForEntry(var1)) != this.this$0._values[var4] && (var3 == null || !THashMap.access$0(this.this$0, var3, this.this$0._values[var4]))) {
            return false;
         } else {
            this.this$0.removeAt(var4);
            return true;
         }
      }
   }

   public boolean containsElement(Entry var1) {
      Object var2 = this.this$0.get(this.keyForEntry(var1));
      Object var3;
      return (var3 = var1.getValue()) == var2 || var2 != null && THashMap.access$0(this.this$0, var2, var3);
   }

   protected Object valueForEntry(Entry var1) {
      return var1.getValue();
   }

   protected Object keyForEntry(Entry var1) {
      return var1.getKey();
   }

   // $FF: synthetic method
   static THashMap access$2(THashMap$EntryView var0) {
      return var0.this$0;
   }

    @Override
    public boolean removeElement(Object var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsElement(Object var1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
