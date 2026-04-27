package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.L_ref;

class TIntCharHashMap$1 implements L_ref {
   private boolean first;
   // $FF: synthetic field
   final TIntCharHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TIntCharHashMap$1(TIntCharHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(int var1, char var2) {
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
