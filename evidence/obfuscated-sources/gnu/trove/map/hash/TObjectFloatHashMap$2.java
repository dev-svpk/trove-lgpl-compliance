package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.af;

class TObjectFloatHashMap$2 implements af {
   private boolean first;
   // $FF: synthetic field
   final TObjectFloatHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectFloatHashMap$2(TObjectFloatHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(Object var1, float var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1).append("=").append(var2);
      return true;
   }
}
