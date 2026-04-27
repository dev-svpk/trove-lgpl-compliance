package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import java.util.Iterator;

public class TCustomHashMap$ValueView extends TCustomHashMap$MapBackedView {
   // $FF: synthetic field
   final TCustomHashMap this$0;

   protected TCustomHashMap$ValueView(TCustomHashMap var1) {
      super(var1, (TCustomHashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new TCustomHashMap$ValueView$1(this, this.this$0);
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
      } while((var3[var4] == TCustomHashMap.FREE || var3[var4] == TCustomHashMap.REMOVED || var1 != var2[var4]) && (var2[var4] == null || !TCustomHashMap.access$0(this.this$0).b()));

      this.this$0.removeAt(var4);
      return true;
   }

   // $FF: synthetic method
   static TCustomHashMap access$0(TCustomHashMap$ValueView var0) {
      return var0.this$0;
   }
}
