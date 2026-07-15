package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TLongProcedure;

class TIntLongHashMap$TValueView$1 implements TLongProcedure {
   private boolean first;
   // $FF: synthetic field
   final TIntLongHashMap$TValueView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TIntLongHashMap$TValueView$1(TIntLongHashMap$TValueView var1, StringBuilder var2) {
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
