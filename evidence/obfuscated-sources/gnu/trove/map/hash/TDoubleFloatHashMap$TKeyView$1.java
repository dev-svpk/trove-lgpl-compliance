package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.z;

class TDoubleFloatHashMap$TKeyView$1 implements z {
   private boolean first;
   // $FF: synthetic field
   final TDoubleFloatHashMap$TKeyView this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TDoubleFloatHashMap$TKeyView$1(TDoubleFloatHashMap$TKeyView var1, StringBuilder var2) {
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
