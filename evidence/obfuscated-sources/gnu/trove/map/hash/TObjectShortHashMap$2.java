package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.aj;

class TObjectShortHashMap$2 implements aj {
   private boolean first;
   // $FF: synthetic field
   final TObjectShortHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TObjectShortHashMap$2(TObjectShortHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(Object var1, short var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(",");
      }

      this.val$buf.append(var1).append("=").append(var2);
      return true;
   }
}
