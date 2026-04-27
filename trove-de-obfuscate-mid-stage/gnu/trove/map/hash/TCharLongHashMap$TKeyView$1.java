package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TCharProcedure;

class TCharLongHashMap$TKeyView$1 implements TCharProcedure {
   private boolean first;
   // $FF: synthetic field
   final TCharLongHashMap$TKeyView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TCharLongHashMap$TKeyView$1(TCharLongHashMap$TKeyView var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(char var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
