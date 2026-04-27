package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.aa;

class TByteLongHashMap$TValueView$1 implements aa {
   private boolean first;
   // $FF: synthetic field
   final TByteLongHashMap$TValueView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TByteLongHashMap$TValueView$1(TByteLongHashMap$TValueView var1, StringBuilder var2) {
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
