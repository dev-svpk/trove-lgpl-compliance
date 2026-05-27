package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TDoubleProcedure;

class TDoubleCharHashMap$TKeyView$1 implements TDoubleProcedure {
   private boolean first;
   // $FF: synthetic field
   final TDoubleCharHashMap$TKeyView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TDoubleCharHashMap$TKeyView$1(TDoubleCharHashMap$TKeyView var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(double var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
