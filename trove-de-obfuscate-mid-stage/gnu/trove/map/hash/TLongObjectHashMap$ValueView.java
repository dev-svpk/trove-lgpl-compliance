package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import java.util.Iterator;

public class TLongObjectHashMap$ValueView extends TLongObjectHashMap$MapBackedView {
   // $FF: synthetic field
   final TLongObjectHashMap this$0;

   protected TLongObjectHashMap$ValueView(TLongObjectHashMap var1) {
      super(var1, (TLongObjectHashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new TLongObjectHashMap$ValueView$1(this, this, this.this$0);
   }

   public boolean containsElement(Object var1) {
      return this.this$0.containsValue(var1);
   }

   public boolean removeElement(Object var1) {
      Object[] var2 = this.this$0._values;
      byte[] var3 = this.this$0._states;
      int var4 = var2.length;

      do {
         do {
            if (var4-- <= 0) {
               return false;
            }
         } while(var3[var4] != 1);
      } while(var1 != var2[var4] && (var2[var4] == null || !var2[var4].equals(var1)));

      this.this$0.removeAt(var4);
      return true;
   }

   // $FF: synthetic method
   static TLongObjectHashMap access$0(TLongObjectHashMap$ValueView var0) {
      return var0.this$0;
   }
}
