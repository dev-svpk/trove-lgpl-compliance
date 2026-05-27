package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TFloatProcedure;

class TCharFloatHashMap$TValueView$1 implements TFloatProcedure {
   private boolean first;
   // $FF: synthetic field
   final TCharFloatHashMap$TValueView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TCharFloatHashMap$TValueView$1(TCharFloatHashMap$TValueView var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(float var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
