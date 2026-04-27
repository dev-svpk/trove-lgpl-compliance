package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.m;

class TCharFloatHashMap$1 implements m {
   private boolean first;
   // $FF: synthetic field
   final TCharFloatHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TCharFloatHashMap$1(TCharFloatHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(char var1, float var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
