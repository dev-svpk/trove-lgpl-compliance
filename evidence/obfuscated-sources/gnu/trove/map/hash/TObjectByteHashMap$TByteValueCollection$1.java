package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.h;

class TObjectByteHashMap$TByteValueCollection$1 implements h {
   private boolean first;
   // $FF: synthetic field
   final TObjectByteHashMap$TByteValueCollection this$1;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectByteHashMap$TByteValueCollection$1(TObjectByteHashMap$TByteValueCollection var1, StringBuilder var2) {
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
