package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.Iterator;

public class TShortObjectHashMap$ValueView extends TShortObjectHashMap$MapBackedView {
   // $FF: synthetic field
   final TShortObjectHashMap this$0;

   protected TShortObjectHashMap$ValueView(TShortObjectHashMap var1) {
      super(var1, (TShortObjectHashMap$MapBackedView)null);
      this.this$0 = var1;
   }

   public Iterator iterator() {
      return new TShortObjectHashMap$ValueView$1(this, this, this.this$0);
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
   static TShortObjectHashMap access$0(TShortObjectHashMap$ValueView var0) {
      return var0.this$0;
   }
}
