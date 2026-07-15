package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TLongProcedure;

class TLongShortHashMap$TKeyView$1 implements TLongProcedure {
   private boolean first;
   // $FF: synthetic field
   final TLongShortHashMap$TKeyView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TLongShortHashMap$TKeyView$1(TLongShortHashMap$TKeyView var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(long var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
