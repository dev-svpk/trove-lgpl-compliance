package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TByteProcedure;

class TLongByteHashMap$TValueView$1 implements TByteProcedure {
   private boolean first;
   // $FF: synthetic field
   final TLongByteHashMap$TValueView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TLongByteHashMap$TValueView$1(TLongByteHashMap$TValueView var1, StringBuilder var2) {
      this.this$1 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(byte var1) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      return true;
   }
}
