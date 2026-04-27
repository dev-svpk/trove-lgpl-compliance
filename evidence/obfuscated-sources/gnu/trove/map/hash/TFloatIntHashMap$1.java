package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.F_ref;

class TFloatIntHashMap$1 implements F_ref {
   private boolean first;
   // $FF: synthetic field
   final TFloatIntHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TFloatIntHashMap$1(TFloatIntHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(float var1, int var2) {
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
