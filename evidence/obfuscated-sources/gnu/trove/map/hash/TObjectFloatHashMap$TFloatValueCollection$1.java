package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.I_ref;

class TObjectFloatHashMap$TFloatValueCollection$1 implements I_ref {
   private boolean first;
   // $FF: synthetic field
   final TObjectFloatHashMap$TFloatValueCollection this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectFloatHashMap$TFloatValueCollection$1(TObjectFloatHashMap$TFloatValueCollection var1, StringBuilder var2) {
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
