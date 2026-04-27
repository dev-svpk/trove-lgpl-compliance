package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.Iterator;

public class THashMap$ValueView extends THashMap$MapBackedView {
   // $FF: synthetic field
   final THashMap this$0;

   protected THashMap$ValueView(THashMap var1) {
      super(var1, (THashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new THashMap$ValueView$1(this, this.this$0);
   }

   public boolean containsElement(Object var1) {
      return this.this$0.containsValue(var1);
   }

   public boolean removeElement(Object var1) {
      Object[] var2 = this.this$0._values;
      Object[] var3 = this.this$0._set;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return false;
         }
      } while((var3[var4] == THashMap.FREE || var3[var4] == THashMap.REMOVED || var1 != var2[var4]) && (var2[var4] == null || !THashMap.access$0(this.this$0, var2[var4], var1)));

      this.this$0.removeAt(var4);
      return true;
   }

   // $FF: synthetic method
   static THashMap access$0(THashMap$ValueView var0) {
      return var0.this$0;
   }
}
