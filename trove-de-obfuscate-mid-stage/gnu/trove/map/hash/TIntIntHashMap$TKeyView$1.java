package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TIntProcedure;

class TIntIntHashMap$TKeyView$1 implements TIntProcedure {
   private boolean first;
   // $FF: synthetic field
   final TIntIntHashMap$TKeyView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TIntIntHashMap$TKeyView$1(TIntIntHashMap$TKeyView var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(int var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
